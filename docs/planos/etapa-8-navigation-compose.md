# Etapa 8 — Migração para Navigation Compose

## Context

O CLAUDE.md (seções 5, 9 e 11) define como etapa 8 do plano a substituição da
navegação manual por Navigation Compose. Hoje toda a navegação está centralizada em
`app/src/main/java/com/example/ic_app/navigation/AppScreen.kt`, num único
`when (telaAtual)` que compõe a tela correspondente e troca de tela mutando
`telaAtual: String`. Não há back stack (o botão físico de voltar hoje sai do app),
nem `NavHost`/`rememberNavController` em lugar nenhum do repo (confirmado por grep —
só há menção em docs).

O objetivo é trocar esse `when` por um `NavHost`, **preservando exatamente as mesmas
transições e o mesmo fluxo de telas** já mapeados, sem reescrever nenhuma tela e sem
mudar a experiência visual (regra da seção 10 do CLAUDE.md). A seção 9 já orienta:
"rotas equivalentes às mesmas chaves de tela, preservando o fluxo" — então as rotas
vão **reusar as mesmas strings** que hoje são valores de `telaAtual`
(`"boas-vindas"`, `"consentimento"`, `"home"`, `"regularidade1"`, etc.).

## Descobertas da exploração (base do plano)

- **Navigation Compose não é dependência** — precisa ser adicionada. O projeto usa
  version catalog (`gradle/libs.versions.toml`) + `app/build.gradle.kts`. Compose BOM =
  `2024.10.01` (que **não** gerencia `navigation-compose`, precisa de versão própria).
  Versão compatível: `navigation-compose = "2.8.4"` (alinha com lifecycle 2.8.7 já em uso).
- **Nenhuma tela precisa mudar de assinatura.** Todas expõem `modifier` + callbacks:
  - Callbacks que devolvem valor (`onContinuarClick: (String) -> Unit`):
    `DataNascimentoScreen`, `PesoScreen`, `NomeScreen`, `ObjetivoScreen` e **as 21 telas
    `objetivos.*`** (todas com a mesma assinatura de 3 params).
  - Telas que recebem valor de entrada: `HomeScreen(nomeUsuario, objetivoUsuario)`,
    `ChatLunaScreen(nomeUsuario)`, `PerfilScreen(nome, dataNascimento, peso, objetivo)`.
  - Telas que já têm seu próprio `viewModel()` (continuam iguais): `ConsentScreen`,
    `CalendarioScreen`, `PerfilScreen`, `RegistrarHojeScreen`, `LoginScreen`, `CriarConta`.
- **Estado compartilhado do onboarding** (`nomeUsuario`, `objetivoUsuario`,
  `dataNascimentoUsuario`, `pesoUsuario`) e o `respostaObjetivoViewModel` hoje vivem
  dentro de `AppScreen()` (`AppScreen.kt:47-52`). Como `AppScreen` continua sendo o host
  do `NavHost` e não sai de composição durante a navegação, esse estado pode continuar
  ali — as lambdas dos `composable(...)` leem/escrevem essas mesmas variáveis. **Nenhum
  ViewModel novo é necessário** (abordagem incremental, seção 10).
- Dois descasamentos nome-de-arquivo × nome-de-função a lembrar: `BemvindaScreen.kt`
  define `fun BemVindoScreen`, e `Data_nascimento.kt` define `fun DataNascimentoScreen`.
- Mapeamento objetivo→primeira rota do sub-fluxo está em `AppScreen.kt:124-133` e será
  replicado igual dentro da lambda `onContinuarClick` de `ObjetivoScreen`.

## Objetivo

Trocar o `when (telaAtual)` por um `NavHost`, mantendo fluxo, telas e visual idênticos,
e ganhando de brinde o back stack padrão do Navigation Compose (botão voltar do sistema
passa a funcionar). Etapa isolada — não mexe em backend, ViewModels ou telas.

## Abordagem

### 1. Adicionar a dependência (2 arquivos)
- `gradle/libs.versions.toml`:
  - em `[versions]`: `navigationCompose = "2.8.4"`
  - em `[libraries]`: `androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }`
- `app/build.gradle.kts` (bloco `dependencies`): `implementation(libs.androidx.navigation.compose)`

### 2. Reescrever apenas `navigation/AppScreen.kt`
Substituir o corpo do `Scaffold { when(...) }` por um `NavHost`, mantendo por cima:
- `val navController = rememberNavController()`
- As 4 variáveis de estado do onboarding (manter como estão hoje —
  `remember { mutableStateOf("") }`; opcionalmente `rememberSaveable` para sobreviver a
  rotação, mas por fidelidade ao comportamento atual manter `remember`).
- `val respostaObjetivoViewModel: RespostaObjetivoViewModel = viewModel()` (igual hoje).

Estrutura:
```kotlin
Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
    NavHost(
        navController = navController,
        startDestination = "boas-vindas",
        modifier = Modifier.padding(innerPadding)
    ) {
        composable("boas-vindas") {
            BemVindoScreen(onFinalizar = { navController.navigate("consentimento") })
        }
        composable("nome") {
            NomeScreen(
                onContinuarClick = { nome -> nomeUsuario = nome; navController.navigate("objetivo") },
                onPularClick = { nomeUsuario = "Usuária"; navController.navigate("objetivo") }
            )
        }
        // ... uma entrada composable("<chave>") por branch atual do when ...
    }
}
```

Regra de tradução (1-para-1 com o `when` atual):
- `telaAtual = "x"`  →  `navController.navigate("x")`
- callbacks de "voltar para home" (`onVoltarHomeClick`, `onVoltarClick` do CriarConta,
  `onSalvarClick`/`onVoltarClick` do RegistrarHoje) → `navController.popBackStack()`
  (volta ao destino anterior, que é sempre a home/login de onde veio).
- `objetivos.*`: cada `onContinuarClick = { opcao -> respostaObjetivoViewModel.salvar(objetivoUsuario, <etapa>, opcao); navController.navigate("<próxima>") }`
  — idêntico ao de hoje, só troca a atribuição de `telaAtual` por `navigate`.
- `ObjetivoScreen.onContinuarClick`: replicar o `when(objetivoEscolhido)` de
  `AppScreen.kt:124-133` para escolher a rota inicial e chamar `navigate(...)`.
- Telas que recebem valores: `HomeScreen(nomeUsuario = nomeUsuario, objetivoUsuario = objetivoUsuario, ...)`,
  `PerfilScreen(nomeUsuario, dataNascimentoUsuario, pesoUsuario, objetivoUsuario, ...)`,
  `ChatLunaScreen(nomeUsuario = nomeUsuario, ...)` — lidos das variáveis do host.

### 3. Semântica de back stack (única mudança de comportamento, positiva)
Para o botão voltar não reentrar no onboarding depois de chegar na home, as transições
que **terminam o onboarding** (fim de cada sub-fluxo de objetivo e os `onPularClick` que
vão direto pra home, mais `onPularClick` de `ObjetivoScreen`) usam:
```kotlin
navController.navigate("home") {
    popUpTo("boas-vindas") { inclusive = true }
}
```
Isso limpa a pilha do onboarding. As telas secundárias abertas a partir da home
(`perfil`, `calendario`, `chat`, `registrar_hoje`, `login` → `criar`) entram normalmente
na pilha e o voltar as remove (via `popBackStack()`), retornando à home/login.

### 4. Telas: nenhuma alteração
Todos os composables em `onboarding/`, `objetivos/`, `home/`, `auth/` ficam **intactos**
— continuam recebendo os mesmos callbacks; só muda quem os implementa (as lambdas do
`NavHost` em vez do `when`).

## Arquivos a modificar
- `gradle/libs.versions.toml` — nova versão + biblioteca.
- `app/build.gradle.kts` — nova linha `implementation`.
- `app/src/main/java/com/example/ic_app/navigation/AppScreen.kt` — troca do `when` pelo
  `NavHost` (único arquivo de código reescrito, mantendo os mesmos imports de tela +
  novos imports de `androidx.navigation.compose.*`).

## Verificação
1. Build: `./gradlew assembleDebug` (a partir da raiz) — deve compilar sem erro.
2. Instalar e rodar no emulador (já configurado com `-gpu angle_indirect` nesta sessão):
   `./gradlew installDebug` e abrir o app.
3. Percorrer o fluxo completo e conferir por screenshot (`adb shell screencap -p /sdcard/s.png`
   + `adb pull` via PowerShell, para não corromper o binário):
   - boas-vindas (2s) → consentimento → data_nascimento → peso → nome → objetivo →
     escolher um objetivo → 3 telas do sub-fluxo → **home** exibindo o nome digitado.
   - Home → perfil (mostra nome/data/peso/objetivo), calendario, chat, registrar_hoje,
     login → criar → voltar. Testar o **botão voltar do sistema** em cada uma (deve
     retornar à home; e na home pós-onboarding não deve voltar ao onboarding).
   - Confirmar persistência inalterada: registrar hoje / calendário / perfil / respostas
     de objetivo continuam chamando o backend como antes (a camada MVVM não muda).
4. `./gradlew lint` opcional para pegar avisos.

## Notas / riscos
- Se `navigation-compose 2.8.4` conflitar na resolução com os artefatos Compose pinados
  (`foundation`/`ui-graphics 1.11.2`), subir para `2.8.5`/`2.8.x` mais recente resolve.
- Rotas por **string** (mesmas chaves de `telaAtual`), não type-safe/serializable — mais
  próximo do estado atual e sem dependência extra (`kotlinx-serialization`).
- Mudança de comportamento intencional e desejável: o botão voltar do sistema passa a
  navegar na pilha em vez de sair do app. Documentar isso no CLAUDE.md ao final (atualizar
  seções 5 e 11 marcando etapa 8 como concluída) — commit à parte, combinado com o usuário.
