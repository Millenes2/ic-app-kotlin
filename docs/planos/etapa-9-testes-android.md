# Etapa 9 — Testes automatizados no Android (ViewModel/Repository + instrumentados)

## Context

O CLAUDE.md (seções 4 e 11) define a etapa 9: "testes unitários de ViewModel/Repository,
testes instrumentados dos fluxos críticos (cadastro/login, registro diário), tratamento
de erro de rede e estados de carregamento nas telas migradas". Hoje o lado Android **não
tem nenhum teste real** — só os templates `ExampleUnitTest.kt` (JVM) e
`ExampleInstrumentedTest.kt` (device) gerados pelo Android Studio. Em contraste, o backend
já tem 150 testes. O objetivo desta etapa é cobrir a camada MVVM com testes de unidade
(JVM, rápidos) e criar testes instrumentados dos fluxos críticos, sempre exercitando os
três estados de UI: **carregando / sucesso / erro**.

Etapa independente da etapa 8 (Navigation Compose): os testes de unidade não dependem dela,
e os testes instrumentados de tela renderizam telas isoladas — só os testes de *fluxo de
navegação ponta-a-ponta* se beneficiam da etapa 8 (ver "Ordenação" abaixo).

## Descobertas da exploração (base do plano)

### Testabilidade da camada de dados
- **Repositories já são testáveis como estão** — cada um recebe sua `Api` por construtor
  com default apontando ao `RetrofitClient` (ex.: `class RegistroDiarioRepository(private val api: RegistroDiarioApi = RetrofitClient.registroDiarioApi)`).
  Em teste, passa-se um `mockk<XxxApi>()` e nunca se toca no singleton. **Não precisa de
  MockWebServer nem refactor** nessa camada.
  - `AuthRepository(sessaoDataStore, api = ...)` tem **duas** deps injetáveis (mockar a
    `AuthApi` e o `SessaoDataStore` — `coVerify { sessaoDataStore.salvarToken(...) }`).
  - Todos retornam um sealed `XxxResultado { Sucesso; data class Erro(val mensagem) }`
    (não `Result<T>`, não lançam para HTTP) — capturam `IOException` → `Erro`.
  - `PerfilRepository.salvar(...)` tem **validação client-side pura** (data BR→ISO inválida
    → `Erro("Data de nascimento inválida...")`; peso não numérico → `Erro("Peso inválido")`;
    blanks → `null`) que retorna **sem chamar a API** — testes de maior valor, sem mock.
  - `extrairMensagemDeErro(resposta, gson)` (em `RespostaErroUtils.kt`) é função pura,
    `gson` injetável — testável com um `Response.error(code, body)` real.
- **`SessaoDataStore(context)`** depende de Context + Preferences DataStore real → não
  testar em JUnit puro; em `AuthRepository` basta um mock dele.
- **`RetrofitClient`** é `object` singleton com `BASE_URL` hardcoded e Apis eager — só é
  alcançado se usarmos o default arg; nos testes construímos o repo com o mock, evitando-o.

### Testabilidade dos ViewModels (exige um pequeno seam)
- Os 6 ViewModels estendem `AndroidViewModel(application)` e **instanciam o Repository
  internamente** num `private val repository = XxxRepository()` — sem costura para injetar
  mock. É a única mudança de produção necessária: adicionar um parâmetro de construtor
  `repository` com default (ver Abordagem §2). `AuthViewModel` ainda constrói
  `SessaoDataStore(application)` inline no default do repo.
- Estados expostos (todos `StateFlow`):
  - `AuthViewModel.estado: AuthUiState { Idle; Carregando; Sucesso; Erro(msg) }` — `login`, `registrar`.
  - `RegistroDiarioViewModel.estado: RegistroDiarioUiState {Idle;Carregando;Sucesso;Erro}` — `salvar`.
  - `PerfilViewModel`: `estado: PerfilUiState {Idle;Carregando;Sucesso(usuario);Erro}` + `eventoSalvar: PerfilSalvarEvento {Idle;Salvando;Salvo;Erro}` — `carregar`, `salvar`.
  - `RegistroCicloViewModel.estado` idem — `salvar(menstruacao, observacao)` **computa `Date()` internamente** (não determinístico; ver Riscos).
  - `RespostaObjetivoViewModel` e `ConsentimentoViewModel`: **sem estado** (fire-and-forget).
    `RespostaObjetivoViewModel.salvar` tem guarda `if (objetivo.isBlank() || opcao.isBlank()) return` (testável).
- `viewModelScope.launch` em todos → testes precisam de uma `MainDispatcherRule` trocando
  `Dispatchers.Main` por um `TestDispatcher`.

### Dependências de teste
- **Presentes:** JUnit4 (`junit:junit:4.13.2`), `androidx.test.ext:junit:1.2.1`,
  `espresso-core:3.6.1`, `androidx.compose.ui:ui-test-junit4` (via BOM), `ui-test-manifest`
  (debug). `testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"` já setado.
- **Ausentes (a adicionar):** `kotlinx-coroutines-test`, MockK, Turbine (opcional),
  `mockwebserver` (opcional), `androidx.arch.core:core-testing`, `navigation-testing` (opcional).
- Toolchain: Kotlin `2.2.20`, coroutines `1.9.0`, compileSdk 36, minSdk 24, JVM 11.

## Objetivo

Cobrir Repository + ViewModel com testes JVM rápidos (sucesso/erro-de-rede/carregamento) e
criar testes instrumentados dos fluxos críticos (cadastro/login e registro diário)
exercitando os estados de UI, sem depender de um backend real (mock em todas as camadas).

## Abordagem

### 1. Adicionar dependências de teste (version catalog + build.gradle)
`gradle/libs.versions.toml` — `[versions]`:
```toml
mockk = "1.13.13"            # compatível com Kotlin 2.2.20 (1.14.x também serve)
coroutinesTest = "1.9.0"     # casar com coroutines em uso
turbine = "1.1.0"            # opcional, asserções de Flow
```
`[libraries]`:
```toml
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
mockk-android = { group = "io.mockk", name = "mockk-android", version.ref = "mockk" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutinesTest" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
```
`app/build.gradle.kts` (`dependencies`):
```kotlin
testImplementation(libs.mockk)
testImplementation(libs.kotlinx.coroutines.test)
testImplementation(libs.turbine)                 // opcional
androidTestImplementation(libs.mockk.android)
androidTestImplementation(libs.kotlinx.coroutines.test)
```

### 2. Seam mínimo de testabilidade nos 6 ViewModels (única mudança de produção)
Extrair a dependência sem reescrever a classe (regra da seção 10 do CLAUDE.md): trocar
`private val repository = XxxRepository()` por um parâmetro de construtor com default —
comportamento de produção inalterado, testes injetam mock.
```kotlin
class RegistroDiarioViewModel(
    application: Application,
    private val repository: RegistroDiarioRepository = RegistroDiarioRepository()
) : AndroidViewModel(application)
```
- `AuthViewModel`: `repository: AuthRepository = AuthRepository(SessaoDataStore(application))`.
- Mantém a base `AndroidViewModel`; nos testes usa-se `mockk<Application>(relaxed = true)`.

### 3. Testes unitários JVM — `app/src/test/java/com/example/ic_app/`
Helper compartilhado: `MainDispatcherRule` (JUnit `TestWatcher` que faz
`Dispatchers.setMain(UnconfinedTestDispatcher())` em `starting` e `resetMain()` em `finished`).

**Repositories (MockK na Api):** para cada um dos 6, os três ramos:
- sucesso (`coEvery { api.x(...) } returns Response.success(dto)` → `Sucesso`),
- erro HTTP (`Response.error(400, body {"detail":"..."})` → `Erro("<detail>")`),
- `IOException` (`coEvery { ... } throws IOException()` → `Erro("Não foi possível conectar ao servidor")`).
- `AuthRepository`: além disso, token salvo no sucesso (`coVerify { sessaoDataStore.salvarToken("tok") }`),
  ramo de token nulo (`Erro("Resposta inválida do servidor")`), `registrar` encadeando `login`
  (stubar ambos), `buscarUsuarioAutenticado` sucesso/erro.
- `PerfilRepository`: ramos de validação **pura** (data inválida, peso inválido, blanks→null,
  sem chamar API) + sucesso com body + erro.
- `extrairMensagemDeErro`: detail válido, JSON malformado → `"Erro inesperado (<code>)"`, body nulo → fallback.

**ViewModels (MockK no Repository + MainDispatcherRule):**
- `AuthViewModel`: `login`/`registrar` → estado `Carregando` e depois `Sucesso`/`Erro(msg)`
  conforme o `AuthResultado` stubado.
- `RegistroDiarioViewModel`, `RegistroCicloViewModel`: `salvar` → `Carregando`→`Sucesso`/`Erro`.
- `PerfilViewModel`: `carregar` com body → `Sucesso(usuario)`; `carregar` com `null` → estado
  permanece `Idle` (sem erro — caso explícito); `salvar` → `eventoSalvar` `Salvando`→`Salvo`/`Erro`.
- `RespostaObjetivoViewModel`: entrada em branco → `coVerify(exactly = 0) { repository.salvar(...) }`;
  entrada válida → repo chamado. `ConsentimentoViewModel.registrar` → repo chamado.
- Asserção de estado: ler `viewModel.estado.value` após o dispatcher processar, ou usar Turbine.

### 4. Testes instrumentados — `app/src/androidTest/java/com/example/ic_app/`
Fluxos críticos com Compose UI test, **injetando um ViewModel com Repository mockado**
(mockk-android) na própria tela (os composables aceitam `viewModel: XxxViewModel = viewModel()`,
então o teste passa a sua instância) — sem rede real:
- **Cadastro/Login** (`LoginScreen`/`CriarConta` com `AuthViewModel` de repo mockado):
  digitar email/senha, tocar em "Entrar"/"Cadastrar", asserir indicador de carregamento,
  callback de sucesso disparado, e mensagem de erro exibida quando o repo devolve `Erro`.
- **Registro diário** (`RegistrarHojeScreen` com `RegistroDiarioViewModel` mockado):
  selecionar humor/sintoma, tocar em salvar, asserir estados carregando→sucesso (chama
  `onSalvarClick`) e erro (mensagem na tela).
- Usar `createAndroidComposeRule<ComponentActivity>()` ou `createComposeRule()` + `setContent`.

### 5. Ordenação e relação com a etapa 8
- Testes de unidade (§3) e de tela isolada (§4) **não dependem** da etapa 8.
- Um teste de **fluxo de navegação ponta-a-ponta** (percorrer onboarding→home) fica melhor
  **depois** da etapa 8, usando `androidx.navigation:navigation-testing` + `TestNavHostController`.
  Deixar como item opcional/posterior para não acoplar as duas etapas.

## Arquivos a criar/modificar
- `gradle/libs.versions.toml`, `app/build.gradle.kts` — dependências de teste.
- Os 6 ViewModels em `app/src/main/java/com/example/ic_app/viewmodel/` — só o seam de construtor.
- Novos em `app/src/test/java/com/example/ic_app/`:
  - `util/MainDispatcherRule.kt`
  - `repository/*RepositoryTest.kt` (6) + `repository/RespostaErroUtilsTest.kt`
  - `viewmodel/*ViewModelTest.kt` (6)
- Novos em `app/src/androidTest/java/com/example/ic_app/`:
  - `auth/LoginFlowTest.kt` (cadastro/login), `home/RegistrarHojeFlowTest.kt`.
- `ExampleUnitTest.kt`/`ExampleInstrumentedTest.kt`: podem ser removidos (templates) ou mantidos — decidir na execução.

## Verificação
1. `./gradlew test` (a partir da raiz) — todos os testes JVM de repository/viewmodel passam.
2. `./gradlew connectedAndroidTest` — testes instrumentados passam no emulador (já ativo nesta
   sessão, com `-gpu angle_indirect`; a etapa 8 não é pré-requisito).
3. `./gradlew testDebugUnitTest --tests "com.example.ic_app.repository.*"` para rodar um subconjunto.
4. `./gradlew lint` opcional.

## Notas / riscos
- A única mudança de código de produção é o seam de construtor nos ViewModels; o
  comportamento em runtime é idêntico (default arg preserva o `XxxRepository()` atual).
- `RegistroCicloViewModel.salvar` computa `Date()` internamente → data não determinística;
  asserir com `coVerify { repository.salvar(any(), menstruacao, observacao) }` ou, se quiser
  determinismo, injetar um provedor de data (refactor opcional, à parte).
- `PerfilRepository` usa `SimpleDateFormat(..., Locale.getDefault())` em `formatoTelaBr` →
  fixar `Locale` no teste (ex.: `Locale.setDefault(Locale("pt","BR"))`) para estabilidade.
- Versão do MockK deve ser compatível com Kotlin 2.2.20 (1.13.13+/1.14.x); `coroutines-test`
  casado em 1.9.0.
- Teste de rede real ponta-a-ponta exigiria um seam no `BASE_URL`/`RetrofitClient` (hoje
  hardcoded) + MockWebServer — fora do escopo desta etapa; cobrimos os estados de UI via
  ViewModel de repo mockado.
- Ao concluir, atualizar o CLAUDE.md (seções 4 e 11) marcando a etapa 9 e o número de testes
  Android — commit à parte, combinado com o usuário.
