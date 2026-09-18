# Base factual do projeto Luna — Latin.Science 2026

Documento de apoio para a escrita de um Short Paper (3–4 páginas, trabalho em andamento). Gerado por
análise factual do repositório `ic-app-kotlin` em 2026-08-11, branch `develop`, commit `b5d5087`
(working tree limpo). Nenhum arquivo de código, configuração ou documentação existente foi alterado
para produzir este relatório; nenhum commit ou push foi feito. Este é o único arquivo criado.

**Regra de evidência aplicada:** `CLAUDE.md` e os documentos em `docs/artigo-latinscience/` foram
tratados como afirmações a verificar, não como evidência. Evidência = código lido diretamente,
teste executado com saída observada, ou comando executado com saída observada.

**Legenda de classificação:** `[CONFIRMADO E FUNCIONANDO]` · `[IMPLEMENTADO, MAS NÃO VALIDADO]` ·
`[PARCIAL]` · `[NÃO IMPLEMENTADO]` · `[NÃO FOI POSSÍVEL VERIFICAR]`.

---

## 1. Veredito

**Sim, há material verificável suficiente para um Short Paper de 3–4 páginas — mas como relato de
engenharia de um sistema em construção, não como resultado de pesquisa.** O ativo mais forte é
factual e forte: um backend FastAPI real, com 21 endpoints, 5 entidades persistidas via SQLAlchemy
+ Alembic, autenticação JWT funcional, e uma suíte de **146 testes automatizados, executada nesta
sessão com sucesso (146 passed, 90,50s) e 94% de cobertura de linha** (ver seção 10). O Android
também evoluiu de fato: existem ViewModels, Retrofit e DataStore reais consumindo esse backend, e o
build de debug **compilou com sucesso nesta máquina** (ver seção 6.2 e 10) — isso não estava
confirmado em nenhum documento anterior do repositório.

**A lacuna mais crítica não é de código, é de contribuição.** Não há absolutamente nenhuma
inteligência artificial no sistema (o "chat com a Lunete" é um `when` de palavras-chave hardcoded,
seção 7), não há nenhuma avaliação com usuárias reais, nenhuma métrica de uso, nenhum dado de
saúde persistido a partir do app (o Android ainda não chama `/registros-ciclo`, `/perfil` nem
`/consentimentos`), e nenhuma revisão de literatura no repositório. Um Short Paper hoje só pode ser
honestamente enquadrado como **relato de arquitetura e progresso de engenharia de um app em
desenvolvimento**, não como artigo com resultado científico, IA aplicada ou validação de usuárias.
Se o objetivo do Latin.Science exigir alguma forma de contribuição ou avaliação, essa é a peça que
falta por completo — não existe hoje nem o esqueleto dela no repositório.

---

## 2. Resumo do Luna

Luna é um aplicativo Android (Kotlin + Jetpack Compose, pacote `com.example.ic_app`) para
acompanhamento de ciclo menstrual e saúde feminina (humor, sintomas, fertilidade, gestação,
bem-estar), acompanhado de um backend próprio em FastAPI + SQLAlchemy com autenticação JWT. O
projeto está em desenvolvimento ativo: o fluxo de telas do Android existe integralmente e uma parte
dele já está conectada a um backend real (registro/login/registrar-hoje), mas a maior parte da
persistência de dados de saúde ainda não está ligada de ponta a ponta, e não há nenhuma camada de
inteligência artificial implementada.

## 3. Problema que o projeto aborda

Acompanhamento de ciclo menstrual, sintomas, humor e fertilidade é hoje comum em apps comerciais,
mas o Luna nasce como projeto próprio (contexto acadêmico/IC) com dados sob controle de um backend
próprio em vez de plataformas de terceiros. `CLAUDE.md` (seção 1) descreve o objetivo como
acompanhamento de "ciclo menstrual e da saúde feminina: humor, sintomas, fertilidade, gestação e
bem-estar". Não há, em nenhum lugar do repositório, uma justificativa de pesquisa (motivação
teórica, gap identificado na literatura, dados epidemiológicos) para esse problema — apenas a
descrição funcional do que o app faz. `NECESSITA DE REFERÊNCIA` para qualquer afirmação sobre a
importância/prevalência do problema.

## 4. Objetivo do aplicativo

Segundo `CLAUDE.md` (seção 1), o objetivo é evoluir um protótipo de interface para um app com
dados reais e multiusuário, via: (a) backend FastAPI + SQLAlchemy próprio; (b) autenticação JWT
emitida pelo backend; (c) reorganização do Android em MVVM consumindo esse backend via Retrofit,
com token armazenado em DataStore; (d) futura migração de navegação para Navigation Compose; (e)
decisão sobre o papel do Firebase. A exploração de código nesta sessão mostra que (a), (b) e parte
de (c) já têm código real (seções 6 e 7), enquanto (d) e a execução da decisão de (e) ainda não
foram feitas.

---

## 5. Material para a Introdução

### 5.1 Problema

Mulheres que desejam acompanhar ciclo menstrual, sintomas e fertilidade usam apps de terceiros que
concentram dados de saúde sensíveis fora do controle da usuária/instituição. `NECESSITA DE
REFERÊNCIA` para qualquer estatística de adoção, privacidade ou insatisfação com apps existentes —
nenhuma foi encontrada no repositório.

### 5.2 Contexto

Ecossistema de "femtech" e apps de saúde móvel; ampla adoção de wearables/apps de bem-estar;
crescente preocupação regulatória (no Brasil, LGPD) sobre dados de saúde. `NECESSITA DE REFERÊNCIA`
para todas as afirmações de contexto — o repositório não contém nenhuma citação sobre o mercado,
adoção ou regulação.

### 5.3 Lacuna

Possível lacuna a explorar: apps comerciais de ciclo menstrual raramente expõem como os dados são
tratados/armazenados, e frequentemente adicionam IA (chatbots, previsão) sem transparência sobre o
mecanismo. O Luna, sendo open-source e escrito de forma pedagógica/incremental, permite auditoria
completa de como cada dado é tratado — mas isso é uma característica do projeto, não uma lacuna de
literatura comprovada. `NECESSITA DE REFERÊNCIA` para qualquer afirmação sobre o estado da arte de
transparência em apps de saúde feminina.

### 5.4 Papel da IA

**Hoje, papel real = nenhum.** Não há IA implementada em nenhuma camada do sistema (ver seção 7 para
o detalhamento completo). Qualquer discussão sobre "papel da IA" na introdução deve ser apresentada
como proposta/trabalho futuro, nunca como algo já implementado — o "chat com a Lunete" é 100%
baseado em regras de palavra-chave fixas no código Android (`ChatLunaScreen.kt:185-209`).

### 5.5 Proposta do Luna

Proposta factual, sustentada pelo código: um app Android com fluxo completo de onboarding e
sub-fluxos por objetivo de saúde, ligado a um backend FastAPI próprio com autenticação JWT e
endpoints REST para perfil, registro diário, registro de ciclo, respostas de objetivo e
consentimento — arquitetura cliente-servidor tradicional, sem qualquer componente de IA nesta
etapa. Ver seção 6 para detalhamento.

### 5.6 Afirmações que precisam de referência externa

Todas marcadas `NECESSITA DE REFERÊNCIA` — nenhuma foi verificada nem deve ser tratada como
factual sem fonte:
- Prevalência/relevância de acompanhamento de ciclo menstrual como problema de saúde pública.
- Estatísticas de adoção de apps de saúde feminina/femtech.
- Riscos de privacidade documentados em apps comerciais de ciclo menstrual.
- Qualquer benefício clínico ou comportamental do autoacompanhamento de humor/sintomas.
- Qualquer afirmação sobre eficácia de chatbots ou IA conversacional em contexto de saúde.
- Enquadramento legal de dado de ciclo menstrual como dado sensível de saúde sob a LGPD (a
  classificação legal em si é factual — Lei 13.709/2018, art. 5º, II — mas nenhuma citação legal
  está presente no repositório; se usada no artigo, deve ser citada à parte, não como achado do
  código).

### 5.7 Tópicos a pesquisar / referências já existentes no repositório

**Nenhuma referência bibliográfica existe no repositório.** Busca por bibliografia, `.bib`,
citações, DOIs ou nomes de autores em `.md`/comentários de código não encontrou nenhum resultado —
`CLAUDE.md` e os dois arquivos em `docs/artigo-latinscience/` (`evidencias-testes.md`,
`instrucoes-build-ufu.md`) são documentação de engenharia, sem nenhuma citação acadêmica.

Tópicos a pesquisar na literatura antes de posicionar o trabalho (lista de lacunas a preencher, não
um posicionamento já feito):
- Apps de saúde feminina/menstrual tracking: panorama, privacidade e crítica acadêmica existente.
- LGPD e dados de saúde sensíveis em apps móveis brasileiros.
- Arquiteturas MVVM + backend REST para apps de saúde — trabalhos comparáveis.
- Uso (ou crítica ao uso) de chatbots/LLMs em apps de saúde reprodutiva e diretrizes de segurança
  clínica para esse tipo de funcionalidade (relevante já que o Luna pretende evoluir o chat).
- Boas práticas de consentimento informado digital e "privacy by design" em apps de saúde.

---

## 6. Material para o Desenvolvimento

### 6.1 Arquitetura geral e comunicação entre componentes

`[PARCIAL]` — Arquitetura cliente-servidor: Android (Kotlin/Compose) ↔ backend FastAPI via HTTP
REST/JSON, autenticado por JWT no header `Authorization: Bearer`. **Parte da comunicação já é real
e observável em código**, parte ainda não existe.

- Cliente Retrofit configurado com `BASE_URL = "http://10.0.2.2:8000/"` (alias do emulador Android
  para o host) — `app/src/main/java/com/example/ic_app/data/remote/RetrofitClient.kt:15`, com
  `OkHttpClient` + `HttpLoggingInterceptor` no nível `BODY` (linhas 17–23) e
  `GsonConverterFactory` (linhas 25–29).
- Endpoints Android→backend efetivamente ligados: `AuthApi.kt` (`POST auth/registrar`, `POST
  auth/login`, `GET auth/me`) e `RegistroDiarioApi.kt` (`POST registros-diarios`, `GET
  registros-diarios`), ambos consumidos por `viewmodel/AuthViewModel.kt` e
  `viewmodel/RegistroDiarioViewModel.kt`.
- Endpoints do backend que **existem mas não são chamados por nenhuma tela Android**: `/perfil`,
  `/registros-ciclo`, `/respostas-objetivo`, `/consentimentos` — confirmado por ausência de
  referência a essas rotas em `app/src/main/java` (nenhum arquivo em `data/remote/` cobre essas
  quatro entidades).
- `[CONFIRMADO E FUNCIONANDO]`: build Android compila e roda a suíte de testes do backend com
  sucesso nesta máquina (seção 10) — mas a integração ponta a ponta real (app rodando em
  emulador/dispositivo conversando com um backend ativo) **não foi observada nesta sessão** (sem
  emulador ativo) — ver seção 10 e limitações.

### 6.2 Frontend / Android

`[PARCIAL]` — mais avançado do que `CLAUDE.md` descreve (ver inconsistências, seção 17).

- **Telas**: 27 arquivos com "Screen" no nome (26 telas de UI + `AppScreen.kt`, que é a máquina de
  navegação, não uma tela). Onboarding (`BemVindoScreen`, `ConsentScreen`, `DataNascimentoScreen`,
  `PesoScreen`, `NomeScreen`, `ObjetivoScreen`), sub-fluxos por objetivo (`regularidade`,
  `entender_corpo`, `engravidar`, `gestacao`, `saude_mental`), Home (`HomeScreen`, `ChatLunaScreen`,
  `CalendarioScreen`, `PerfilScreen`, `RegistrarHojeScreen`), autenticação (`LoginScreen`,
  `CriarConta`). 85 funções `@Composable` em 33 arquivos `.kt` (`grep -rc "@Composable" *.kt`,
  contagem confirmada nesta sessão).
- **Navegação**: sem Jetpack Navigation/`NavHost`. `navigation/AppScreen.kt` mantém `telaAtual:
  String` em `remember { mutableStateOf("boas-vindas") }` (linha 37) e um `when(telaAtual)`
  (linha 45) que compõe a tela correspondente. Estado global compartilhado limitado a
  `nomeUsuario`, `objetivoUsuario`, `dataNascimentoUsuario`, `pesoUsuario` (linhas 38–41), todos
  `remember { mutableStateOf(...) }`. `[CONFIRMADO]` bug de navegação já mapeado em `CLAUDE.md`
  seção 4: as chaves `"sintomas1"`/`"bemestar1"` (geradas por `ObjetivoScreen`) não têm branch no
  `when` → tela em branco.
  - Achado adicional não documentado em `CLAUDE.md`: o branch `"chat"` está duplicado dentro do
    `when` (aparece duas vezes); a segunda ocorrência é código morto.
- **Gerenciamento de estado**: **existem ViewModels reais**, contrariando `CLAUDE.md` seção 5
  ("Não existe ViewModel nem Repository hoje"). `viewmodel/AuthViewModel.kt` — `class
  AuthViewModel(application) : AndroidViewModel`, expõe `StateFlow<AuthUiState>` (linha 34); métodos
  `login()`, `registrar()`, `buscarUsuarioAutenticado()`. `viewmodel/RegistroDiarioViewModel.kt` —
  mesma estrutura, método `salvar()`. Não existe camada `Repository` — os ViewModels chamam
  `RetrofitClient`/`SessaoDataStore` diretamente.
- **Persistência de sessão**: `data/local/SessaoDataStore.kt` — Preferences DataStore nomeado
  `"sessao"`, guarda o JWT sob a chave `access_token` (`salvarToken`, `tokenFlow`, `limparToken`).
  Nenhum Room ou outra persistência local de dados de domínio (consistente com a diretriz de
  `CLAUDE.md` seção 10 de não introduzir Room sem necessidade).
- **Limitação real observada**: mesmo com o token salvo, reabrir o app sempre volta ao onboarding —
  não há checagem de sessão existente em `AppScreen.kt` (confirmado por leitura de código e também
  descrito como limitação conhecida em `docs/artigo-latinscience/instrucoes-build-ufu.md:151-154`).
- **Arquivos órfãos/duplicados**: `HumorItem.kt` e `RegistroOpcaoCard.kt` em
  `app/src/main/java/` sem `package` declarado; `RegistroOpcaoCard` está duplicado dentro de
  `home/RegistrarHojeScreen.kt:272` (versão realmente usada pela tela).

### 6.3 Backend e API

`[CONFIRMADO E FUNCIONANDO]` para os endpoints e testes; `[IMPLEMENTADO, MAS NÃO VALIDADO]` para o
consumo real por qualquer cliente Android.

Framework: FastAPI `0.139.2`, Starlette `1.3.1`, Uvicorn `0.51.0` (`backend/requirements.txt`).
CORS: `allow_origins=["*"]`, `allow_credentials=False` (`app/main.py:63-69`) — origem totalmente
aberta.

**21 endpoints** em 6 routers + 2 rotas de sistema:

| Método | Rota | Arquivo:linha | Auth |
|---|---|---|---|
| GET | `/` | `main.py:79` | não |
| GET | `/health` | `main.py:91` | não |
| POST | `/auth/registrar` | `routers/auth.py:17` | não |
| POST | `/auth/login` | `routers/auth.py:58` | não |
| GET | `/auth/me` | `routers/auth.py:88` | sim |
| GET | `/perfil` | `routers/perfil.py:12` | sim |
| PATCH | `/perfil` | `routers/perfil.py:24` | sim |
| POST | `/registros-diarios` | `registros_diarios.py:65` | sim |
| GET | `/registros-diarios` | `registros_diarios.py:112` | sim |
| GET | `/registros-diarios/{id}` | `registros_diarios.py:154` | sim |
| PATCH | `/registros-diarios/{id}` | `registros_diarios.py:176` | sim |
| DELETE | `/registros-diarios/{id}` | `registros_diarios.py:245` | sim |
| POST | `/registros-ciclo` | `registros_ciclo.py:65` | sim |
| GET | `/registros-ciclo` | `registros_ciclo.py:107` | sim |
| GET | `/registros-ciclo/{id}` | `registros_ciclo.py:149` | sim |
| PATCH | `/registros-ciclo/{id}` | `registros_ciclo.py:171` | sim |
| DELETE | `/registros-ciclo/{id}` | `registros_ciclo.py:223` | sim |
| POST | `/respostas-objetivo` | `respostas_objetivo.py:29` | sim |
| GET | `/respostas-objetivo` | `respostas_objetivo.py:93` | sim |
| POST | `/consentimentos` | `consentimentos.py:29` | sim |
| GET | `/consentimentos/atual` | `consentimentos.py:100` | sim |

Contagem confirmada nesta sessão via `grep -cE "@(router|app)\.(get|post|patch|delete|put)"` = 21.

Padrões de design observados no código: escrita idempotente ("upsert") em respostas de objetivo e
consentimento; 404 (não 403) para registros de outro usuário, deliberadamente para não revelar
existência do recurso (`registros_diarios.py`, comentários próximos às linhas 163-166);
`_commit_ou_conflito` trata `IntegrityError` de corrida e responde 409 em vez de 500.

### 6.4 Banco de dados

`[CONFIRMADO E FUNCIONANDO]` — ORM SQLAlchemy `2.0.51`; `DATABASE_URL` padrão
`sqlite:///./luna.db` (`app/core/config.py:11`); engine/sessão em `app/db/session.py`. Alembic
`1.18.5` com 5 migrations lineares (`alembic/versions/`):
1. `d3396c157df2` — cria as 5 tabelas iniciais + índice único de e-mail.
2. `0267a59d2ee4` — índice + unicidade `(usuario_id, data)` em `registro_diario`.
3. `5d45b487957e` — mesmo padrão em `registro_ciclo`.
4. `efa38d108ef2` — índice + unicidade `(usuario_id, objetivo, etapa)` em `resposta_objetivo`.
5. `8e0d5c14ae10` — índice + unicidade `(usuario_id, versao_termos)` em `consentimento`.

Entidades modeladas (5): `Usuario`, `Consentimento`, `RegistroDiario`, `RegistroCiclo`,
`RespostaObjetivo`, todas em `app/models/`. `MensagemChat` **não existe** — nenhum modelo, tabela
ou referência em código (busca por `MensagemChat`/`mensagem_chat` sem resultado). `fase_calculada`
em `RegistroCiclo` é sempre `null` — nenhum cálculo de fase de ciclo ou previsão de menstruação
existe no backend (decisão de escopo documentada em `CLAUDE.md` seção 7).

### 6.5 Autenticação

`[CONFIRMADO E FUNCIONANDO]` no backend isoladamente (validado por 8 testes em `test_auth.py`,
todos passando — seção 10); `[PARCIAL]` quando considerado o sistema completo, porque
`AppScreen.kt` não faz checagem de sessão ao abrir o app (seção 6.2).

- Hash de senha com `bcrypt` puro (`bcrypt.hashpw`/`bcrypt.checkpw`, `app/core/security.py:9-15`).
- JWT com `PyJWT`, algoritmo `HS256`, expiração padrão de 60 minutos, sem refresh token
  (`security.py:18-29`, `config.py:13-14`). `SECRET_KEY` lida de variável de ambiente via
  `os.getenv("SECRET_KEY", "")` (`config.py:12`) — valor real só em `backend/.env`, não versionado
  (ver seção 8 sobre o incidente histórico de vazamento).
- `app/dependencies.py:20-42` — `get_current_usuario` decodifica o token, busca o usuário, levanta
  401 em qualquer falha.
- No Android: `LoginScreen.kt` chama `viewModel.login(email, senha)` (linha 219), que por sua vez
  chama `POST /auth/login` via Retrofit; `CriarConta.kt` chama `viewModel.registrar(...)` (linha
  214) → `POST /auth/registrar`, encadeando login automático. Token salvo em `SessaoDataStore`
  (DataStore), não em memória — isso **já substitui** parcialmente o que `CLAUDE.md` (seção 9)
  descreve como arquitetura "pretendida" (DataStore para token) — já está implementado.
- Botão "Entrar com Google" em `LoginScreen.kt` (linhas 276–292) tem `onClick = { }` vazio —
  `[NÃO IMPLEMENTADO]`.
- Firebase (`FirebaseAuth`) **não é usado em nenhum lugar do código Android** hoje — busca
  case-insensitive por `firebase|signInWithEmailAndPassword|createUserWithEmailAndPassword|
  Firestore` em todo `app/src/main/java` retorna exatamente **um** resultado, um comentário em
  `viewmodel/AuthViewModel.kt` ("...manter o comportamento já existente com o Firebase..."),
  sem nenhuma chamada real de API do Firebase. Isso contradiz diretamente `CLAUDE.md` seções 3, 6,
  12 e 13, que descrevem `CriarConta.kt` como usuário ativo de `FirebaseAuth.
  createUserWithEmailAndPassword` — ver seção 17 (inconsistências).

### 6.6 Principais funcionalidades

Presentes no código Android (interface completa, navegável): onboarding completo, sub-fluxos por
objetivo, home com chips/cards estáticos, registrar-hoje (humor/sintoma/observação), calendário
(semana fixa), chat baseado em regras, perfil editável, login/criar-conta. Presentes no backend
(implementadas e testadas): CRUD de perfil, registro diário, registro de ciclo, respostas de
objetivo, consentimento, todos vinculados ao usuário autenticado. Funcionalidades incompletas
mapeadas em `CLAUDE.md` seção 4 foram confirmadas ainda presentes por leitura de código nesta
sessão: objetivos "Acompanhar sintomas"/"Melhorar meu bem-estar" levam a tela em branco; calendário
estático; previsões de ciclo são texto fixo (`HomeScreen.kt:347,354,365,374`;
`CalendarioScreen.kt:137,146` — ex.: `"Dia 14"`, `"de 28 dias"`, `"Próxima menstruação em 12
dias"`, `"🌸 Próxima menstruação prevista em 14 dias"`).

### 6.7 Bibliotecas e tecnologias relevantes (com versões)

**Android** (`gradle/libs.versions.toml`, `app/build.gradle.kts`): AGP `8.10.1`, Kotlin `2.2.20`
(nota: `kotlin-compose` referencia `2.0.21` em outro ponto do `libs.versions.toml` — divergência
pré-existente documentada em `docs/artigo-latinscience/instrucoes-build-ufu.md:45-49`), Compose BOM
`2024.10.01`, `compileSdk`/`targetSdk` 36, `minSdk` 24, Retrofit `2.11.0`, OkHttp `4.12.0`,
kotlinx-coroutines `1.9.0`, DataStore `1.1.1`, lifecycle-viewmodel-compose `2.8.7`, Firebase BOM
`33.7.0` (declarada, não usada em código — seção 6.5), plugin `com.google.gms.google-services`
`4.4.2`.

**Backend** (`backend/requirements.txt`): FastAPI `0.139.2`, Starlette `1.3.1`, Uvicorn `0.51.0`,
SQLAlchemy `2.0.51`, Alembic `1.18.5`, Pydantic `2.13.4`, PyJWT `2.13.0`, bcrypt `5.0.0`,
python-dotenv `1.2.2`, pytest `9.1.1`, pytest-cov `7.1.0`, coverage `7.15.4`, httpx `0.28.1`,
cryptography `49.0.0`. `firebase_admin==7.5.0` está listado mas **nunca importado** em nenhum
arquivo (busca confirmada nesta sessão).

---

## 7. Inteligência Artificial — descrição precisa

**Não existe nenhuma forma de inteligência artificial no sistema hoje** — nem chamada a LLM
externo, nem modelo local, nem algoritmo estatístico de predição. `[NÃO IMPLEMENTADO]`.

- O "chat com a Lunete" (`home/ChatLunaScreen.kt`) é inteiramente baseado em regras fixas de
  palavra-chave, função `gerarRespostaSimulada(texto: String)`, linhas 185–209 (lida e confirmada
  nesta sessão). Lógica: `texto.lowercase()` e um `when` que casa substrings — "cólica"/"colica" →
  resposta fixa sobre registrar cólica; "cansada"/"cansaço" → resposta fixa sobre energia;
  "triste"/"ansiosa" → resposta fixa sobre estado emocional; "menstruação"/"menstruacao" → resposta
  fixa sobre ciclo; qualquer outro texto → resposta genérica fixa. Nenhuma chamada de rede, nenhuma
  chamada a API de IA, nenhum uso de contexto de mensagens anteriores para gerar a resposta.
- Não há inferência em lugar nenhum (nem no dispositivo, nem no backend, nem em serviço externo).
- Não há dado do usuário compondo qualquer "prompt" — porque não existe prompt nem modelo.
- Não há prompt de sistema.
- Não existe avaliação de qualidade de resposta de qualquer tipo — nem testes automatizados, nem
  exemplos versionados, nem validação manual registrada — porque não há resposta gerada por IA para
  avaliar.
- No backend, o único artefato relacionado a "IA/nuvem" é a dependência `firebase_admin==7.5.0`
  (mais o stack transitivo Google Cloud), listada em `requirements.txt` mas **nunca importada ou
  inicializada** em nenhum arquivo de `app/`, `tests/` ou `alembic/` — não é IA, e sequer está em
  uso.

Qualquer seção do artigo sobre "papel da IA" deve, portanto, ser escrita explicitamente como visão
futura/proposta, nunca como capacidade atual do sistema.

---

## 8. Privacidade, dados sensíveis e LGPD

- **Armazenamento**: dados de ciclo/humor/sintomas que o Android efetivamente envia hoje
  (registro diário via `POST /registros-diarios`) ficam em SQLite no servidor backend
  (`sqlite:///./luna.db`), sem qualquer configuração de criptografia em repouso no código
  (`app/db/session.py`, `app/core/config.py` — nenhuma menção a criptografia de arquivo/coluna).
  Sessão (token JWT) fica no dispositivo via DataStore (`SessaoDataStore.kt`) — Preferences
  DataStore não é criptografado por padrão, e nenhum uso de `EncryptedFile`/Android Keystore foi
  encontrado no código. `[NÃO IMPLEMENTADO]` criptografia em repouso, tanto no servidor quanto no
  dispositivo.
- **Trânsito**: comunicação Android↔backend é HTTP simples (`BASE_URL =
  "http://10.0.2.2:8000/"`, sem TLS) — adequado apenas para desenvolvimento local via emulador;
  nenhuma configuração de HTTPS/certificado foi encontrada para uso em produção.
  `[NÃO IMPLEMENTADO]` para trânsito seguro em produção; ambiente de desenvolvimento é HTTP puro por
  design.
- **Terceiros**: nenhum dado de ciclo/humor/sintoma é enviado a serviço de terceiros hoje —
  Firebase está declarado mas não é chamado (seção 6.5); `firebase_admin` no backend não é
  inicializado (seção 6.3/6.7). Não há nenhuma chamada de rede a serviço de IA ou analytics externo
  em nenhum ponto do código lido.
- **Política de privacidade**: `[NÃO IMPLEMENTADO]`. Busca por "política", "privacidade", "LGPD",
  "termos" em todo o código Android e backend não encontrou nenhum texto de política de privacidade
  nem link para uma.
- **Consentimento**: `[PARCIAL]`. `onboarding/ConsentScreen.kt` exibe um checkbox obrigatório e um
  diálogo com os tipos de dados coletados (perfil, data de nascimento, peso, objetivos, humor,
  sintomas, observações, dados de ciclo/fertilidade/gestação — linhas 97–157), mas o estado do
  checkbox vive apenas em `remember { mutableStateOf(false) }` (linha 24) — **não é persistido em
  lugar nenhum**, nem localmente nem no backend. O backend **tem** uma entidade e rotas de
  `Consentimento` totalmente implementadas e testadas (`POST`/`GET /atual` em `/consentimentos`,
  seção 6.3), mas **nenhuma tela Android chama essas rotas** — confirmado por ausência de qualquer
  DTO/API Android para consentimento. Ou seja: existe uma peça de consentimento no backend e uma
  peça de UI de consentimento no Android, mas elas não estão conectadas.
- **Exclusão/exportação de conta**: `[NÃO IMPLEMENTADO]`. Não existe endpoint de exclusão de conta
  ou exportação de dados no backend (os 21 endpoints listados na seção 6.3 não incluem nenhuma
  rota `DELETE /usuario` ou de exportação), nem tela correspondente no Android.
- **Tratamento explícito de LGPD**: `[NÃO IMPLEMENTADO]`. Nenhuma menção literal a "LGPD" em código
  ou documentação de engenharia (`CLAUDE.md` e os dois arquivos em `docs/artigo-latinscience/` não
  citam a lei). A classificação de dado de ciclo menstrual como dado sensível de saúde é uma
  afirmação legal correta e independente do código (Lei 13.709/2018, art. 5º, II) — mas se usada no
  artigo deve ser citada como referência legal externa, não atribuída ao repositório.
- **Outros pontos de segurança observados**: CORS totalmente aberto (`allow_origins=["*"]`,
  `main.py:65`); design deliberado de retornar 404 (não 403) para recursos de outro usuário, para
  não revelar existência do registro (seção 6.3) — é uma boa prática de privacidade aplicada, ainda
  que pontual.

---

## 9. Salvaguardas de saúde

Todas `[NÃO IMPLEMENTADO]`. Busca exaustiva (case-insensitive) em todo o código Kotlin por "não
substitui", "profissional de saúde", "médic", "diagnóstic", "contracep", "janela fértil",
"anticoncep" não retornou **nenhum** resultado.

- Não existe aviso de que o app não substitui profissional de saúde, em nenhuma tela.
- Não há qualquer lógica de recusa de diagnóstico — porque não há nenhum componente que produza
  diagnóstico, orientação clínica ou triagem de sintomas (o chat é puramente reativo por
  palavra-chave, seção 7).
- Não há encaminhamento a atendimento médico nem tratamento diferenciado de temas sensíveis (por
  exemplo, o chat responde de forma idêntica e genérica a menções de tristeza/ansiedade, sem
  qualquer escalonamento ou recurso de apoio).
- O app exibe previsão de "período fértil" (`HomeScreen.kt:365`, texto fixo
  "Período fértil previsto", sem datas calculadas) e de "próxima menstruação" (`HomeScreen.kt:374`,
  `CalendarioScreen.kt:137`), mas isso é **texto hardcoded, não um cálculo real** (seção 6.6) — não
  há, portanto, nem o cálculo em si, nem um aviso de que não serve como método contraceptivo.

Este é um ponto que merece destaque honesto no artigo: qualquer expansão futura para previsão real
de janela fértil deve necessariamente vir acompanhada de aviso explícito de que não é método
contraceptivo, o que hoje nem se aplica porque a funcionalidade de cálculo não existe ainda.

---

## 10. Métricas objetivas

| Métrica | Valor | Comando/fonte |
|---|---|---|
| Linhas de código Kotlin (`.kt`, exclui build/.venv) | 6.611 linhas, 46 arquivos | `git ls-files '*.kt' \| grep -v '\.venv/' \| xargs wc -l` |
| Linhas de código Python (`.py`, exclui .venv) | 4.197 linhas, 46 arquivos | `git ls-files '*.py' \| grep -v '\.venv/' \| xargs wc -l` |
| Linhas em Markdown versionado | 1.478 linhas, 3 arquivos | `git ls-files '*.md' \| xargs wc -l` |
| `cloc` disponível nesta máquina | Não | tentativa de execução; ferramenta ausente |
| Endpoints da API (lista completa na seção 6.3) | 21 | `grep -cE "@(router\|app)\.(get\|post\|patch\|delete\|put)"` sobre `backend/app/**/*.py` |
| Entidades/tabelas do banco | 5 (`Usuario`, `Consentimento`, `RegistroDiario`, `RegistroCiclo`, `RespostaObjetivo`) | leitura de `backend/app/models/` |
| Migrations Alembic | 5 | listagem de `backend/alembic/versions/` |
| Telas Android (arquivos com "Screen" no nome, exclui `AppScreen.kt`) | 26 | listagem de `app/src/main/java` |
| Funções `@Composable` | 85, em 33 arquivos `.kt` | `grep -rc "@Composable" *.kt` (soma) |
| Casos de teste — backend Python (`def test_`) | 146, em 8 arquivos | `grep -c '^def test_' backend/tests/*.py` (soma) e confirmado por execução real (ver abaixo) |
| Casos de teste — Android (`@Test`) | 2 (apenas templates padrão do Android Studio: `ExampleUnitTest`, `ExampleInstrumentedTest`) | `grep -rc "@Test"` |
| **Execução real da suíte backend nesta sessão** | **146 passed, 0 failed, em 90,50s** | `backend/.venv/Scripts/python.exe -m pytest -q`, executado nesta sessão |
| **Cobertura de linha (pytest-cov), nesta sessão** | **94% (700 stmts, 43 missed)** | `pytest --cov=app --cov-report=term-missing -q`, executado nesta sessão — detalhe por arquivo abaixo |
| **Build Android debug, nesta sessão** | **BUILD SUCCESSFUL em 48s**, `app-debug.apk` gerado (17.150.179 bytes, `app/build/outputs/apk/debug/app-debug.apk`) | `./gradlew assembleDebug --console=plain`, executado nesta sessão |
| Total de commits | 10 | `git rev-list --count HEAD` |
| Primeiro commit | 2026-06-30 11:58:41 -0300 | `git log --reverse --format=%ci \| head -1` |
| Último commit | 2026-08-10 22:04:00 -0300 | `git log -1 --format=%ci` |
| Dependências diretas — Android | ~14 bibliotecas nomeadas em `libs.versions.toml`/`build.gradle.kts` (Compose BOM, Firebase BOM, Retrofit, OkHttp, DataStore, coroutines, lifecycle-viewmodel-compose etc.) | leitura de `gradle/libs.versions.toml` e `app/build.gradle.kts` |
| Dependências diretas — backend | 68 pacotes listados em `backend/requirements.txt` (a maioria transitivos do stack Google Cloud/Firebase, não usado); dependências diretas de fato usadas: fastapi, uvicorn, sqlalchemy, alembic, pydantic, python-dotenv, PyJWT, bcrypt, pytest, pytest-cov | `backend/requirements.txt` |

### 10.1 Detalhe de cobertura por arquivo (backend, `pytest --cov`, saída literal desta sessão)

```
Name                                Stmts   Miss  Cover   Missing
-----------------------------------------------------------------
app\core\config.py                     11      0   100%
app\core\security.py                   16      0   100%
app\db\session.py                      11      4    64%   15-19
app\dependencies.py                    21      2    90%   34, 40
app\main.py                            18      1    94%   86
app\routers\auth.py                    35      3    91%   46-52
app\routers\consentimentos.py          42      8    81%   74-92
app\routers\perfil.py                  17      0   100%
app\routers\registros_ciclo.py         71      7    90%   56-62
app\routers\registros_diarios.py       77      7    91%   56-62
app\routers\respostas_objetivo.py      43      8    81%   67-85
app\schemas\registro_ciclo.py          61      2    97%   29, 80
app\schemas\usuario.py                 61      1    98%   114
-----------------------------------------------------------------
TOTAL                                 700     43    94%
```
(Modelos e demais schemas: 100% de cobertura; omitidos aqui por brevidade — ver saída completa nos
comandos executados.) As linhas não cobertas concentram-se em ramos de tratamento de corrida
(`IntegrityError`) e no bloco `get_db()`, difíceis de exercitar sem concorrência real.

Nota sobre `cloc`: a ferramenta não está instalada nesta máquina; os totais de linhas acima foram
obtidos por `wc -l` sobre a lista de arquivos rastreados pelo Git (exclui `.venv/`, `build/` e
outros diretórios ignorados), o que é equivalente em precisão para este propósito, mas não separa
comentários/linhas em branco como `cloc` faria.

---

## 11. Licença e abertura do projeto

- **Não existe arquivo `LICENSE`** em nenhum lugar do repositório rastreado pelo Git (`git ls-files
  | grep -i license` retorna vazio, exclui `.venv`). `[NÃO IMPLEMENTADO]`.
- Não há `README.md` no repositório — a documentação primária é `CLAUDE.md`. Nenhum arquivo
  (`build.gradle.kts`, `libs.versions.toml`, `requirements.txt`) declara licença.
- Repositório remoto: `https://github.com/Millenes2/ic-app-kotlin.git` (`git remote -v`). Não é
  possível determinar, por comandos locais, se o repositório GitHub é público ou privado —
  `[NÃO FOI POSSÍVEL VERIFICAR]` (exigiria acesso à API do GitHub ou verificação manual da página
  do repositório, fora do escopo desta análise de código local).

---

## 12. Resultados preliminares

### 12.1 Resultados confirmados

- Suíte de 146 testes automatizados do backend, executada nesta sessão com 0 falhas
  (146 passed em 90,50s) e 94% de cobertura de linha.
- Build de debug do Android compila com sucesso nesta máquina (`BUILD SUCCESSFUL`, APK gerado de
  17,15 MB).
- Endpoints REST de autenticação, perfil, registro diário, registro de ciclo, respostas de
  objetivo e consentimento estão implementados, com autenticação JWT funcionando (validado pelos
  testes de `test_auth.py` e pela dependência aplicada em todas as rotas protegidas).
- Login e cadastro de conta no Android já chamam o backend real via Retrofit (código lido e
  confirmado), com token persistido em DataStore.

### 12.2 Resultados parcialmente confirmados

- A integração ponta a ponta Android↔backend está implementada em código e o backend responde
  corretamente aos testes automatizados, mas **não foi observada em execução real nesta sessão**
  (não havia emulador/dispositivo Android ativo para rodar o app e capturar uma requisição HTTP
  real chegando ao backend). O documento `docs/artigo-latinscience/instrucoes-build-ufu.md`
  descreve o roteiro para esse teste manual e indica que a seção 10.2 de
  `evidencias-testes.md` ainda estava marcada como "não verificado" antes desta sessão.
- Consentimento: UI existe, backend existe, mas não estão conectados — cada lado isoladamente
  "funciona" (a UI mostra o diálogo; o backend aceita e retorna o registro via teste automatizado),
  mas o fluxo completo (usuária marca consentimento → é persistido) nunca ocorre de fato.

### 12.3 Resultados ainda não validados

- Qualquer registro de ciclo, perfil ou resposta de objetivo feito pela usuária real através do
  app — essas telas do Android não chamam o backend ainda.
- Qualidade, utilidade ou segurança de qualquer resposta do chat — não se aplica hoje, pois não há
  IA (seção 7).
- Qualquer aspecto de usabilidade, aceitação ou experiência de usuária — nenhum teste de
  usabilidade foi conduzido ou está documentado no repositório.

**RESULTADOS QUE PODEM ENTRAR NO ARTIGO HOJE:**
- Arquitetura implementada (backend FastAPI + Android Kotlin/Compose) com autenticação JWT.
- Suíte de 146 testes automatizados passando, 94% de cobertura.
- Build Android funcional (evidência de que o código compila e gera um APK real).
- Descrição honesta do estado de integração parcial Android↔backend.

**RESULTADOS QUE AINDA PRECISAM SER PRODUZIDOS:**
- Teste ponta a ponta real com emulador/dispositivo, capturando log do backend recebendo
  requisições do app (roteiro já documentado em `instrucoes-build-ufu.md`, nunca executado com
  evidência registrada até esta sessão).
- Qualquer avaliação de usuária, ainda que informal.
- Qualquer decisão e implementação da camada de IA, se for parte do artigo.
- Conexão das telas de perfil, calendário e objetivos ao backend.

---

## 13. Evidências disponíveis

| Evidência | Como foi obtida | Onde está documentada | Pode entrar no artigo? |
|---|---|---|---|
| 146 testes backend passando | Execução real nesta sessão (`pytest -q`) e execução anterior registrada em `evidencias-testes.md` (2026-08-05/06) | `docs/artigo-latinscience/evidencias-testes.md`; reconfirmado nesta sessão | Sim — é a evidência mais forte do projeto |
| 94% de cobertura de linha do backend | Execução real nesta sessão (`pytest --cov`) | Este documento, seção 10.1 | Sim |
| Build Android gera APK de debug | Execução real nesta sessão (`./gradlew assembleDebug`) | Este documento, seção 10 | Sim, com a ressalva de que não houve execução em emulador |
| 21 endpoints REST implementados | Leitura de código (`backend/app/routers/`) | Este documento, seção 6.3; consistente com `CLAUDE.md` seção 7 | Sim, como descrição de arquitetura |
| Login/Cadastro Android chamando backend real | Leitura de código (`LoginScreen.kt`, `CriarConta.kt`, `AuthViewModel.kt`, `RetrofitClient.kt`) | Este documento, seção 6.5; parcialmente em `CLAUDE.md` seção 7 (mudanças recentes) | Sim, como descrição de arquitetura — não como validação de usuária |
| Ausência total de IA | Busca exaustiva no código, sem resultado de LLM/modelo/estatística | Este documento, seção 7 | Sim — é factual e importante deixar claro |

---

## 14. Evidências que ainda precisamos produzir

Priorizadas por esforço × ganho para um Short Paper de 3–4 páginas:

1. **Teste manual ponta a ponta com emulador/dispositivo real** (baixo esforço, alto ganho): o
   roteiro já existe em `instrucoes-build-ufu.md`; esta sessão já confirmou que a máquina atual TEM
   JDK 17 e Android SDK instalados — ao contrário do que o documento supunha sobre "esta máquina".
   Rodar o roteiro e capturar log do uvicorn + telas seria a evidência de maior impacto por menor
   custo.
2. **Screenshot/diagrama de arquitetura** (baixo esforço, médio ganho): não requer código novo, só
   síntese visual do que já existe.
3. **Tabela de endpoints e resultado de testes formatada para o artigo** (baixo esforço, médio
   ganho): os dados já estão neste documento, só precisam ser condensados em tabela final.
4. **Decisão e execução da camada de IA, mesmo que mínima** (alto esforço, alto ganho se o artigo
   pretende falar de IA): hoje é zero; qualquer afirmação sobre IA no artigo depende disso.
5. **Conexão de ao menos uma tela adicional (perfil ou consentimento) ao backend** (médio esforço,
   médio ganho): fortaleceria a narrativa de "sistema real" além de login/registro diário.

---

## 15. Limitações atuais

- Navegação sem Jetpack Navigation (`when(telaAtual)` manual), sem `Repository`, sem migração de
  Firebase resolvida — decisão pendente entre validar tokens do Firebase no backend ou migrar
  totalmente para JWT próprio (`CLAUDE.md` seções 12–13; hoje, na prática, o código já pende para a
  opção B, pois Firebase não é chamado, mas a decisão formal e a remoção de dependência não foram
  feitas).
- Persistência real cobre apenas login/cadastro/registro diário; perfil, ciclo, respostas de
  objetivo e consentimento têm backend pronto mas Android não os chama ainda.
- Nenhum cálculo real de fase de ciclo ou previsão de fertilidade — dado explicitamente fora de
  escopo até agora.
- Nenhuma salvaguarda de saúde (disclaimers, encaminhamento, aviso de não-contracepção).
- Nenhuma criptografia em repouso; comunicação em desenvolvimento é HTTP sem TLS; CORS totalmente
  aberto.
- Sem LICENSE, sem README.
- Limitação de avaliação desta análise: sem emulador/dispositivo ativo nesta sessão, não foi
  possível observar uma requisição HTTP real do app chegando ao backend — apenas o código de
  ambos os lados e os testes automatizados foram verificados.
- Estas são limitações esperadas de um projeto em etapa intermediária, não falhas graves — não
  devem ser inflacionadas no artigo além do que são.

---

## 16. Trabalhos futuros

Apenas os já documentados no repositório ou necessários para completar funcionalidades já
previstas (nenhum item inventado):

- Conectar Android a `/perfil`, `/registros-ciclo`, `/respostas-objetivo`, `/consentimentos`
  (backend já pronto e testado).
- Migrar `AppScreen.kt` para Navigation Compose, preservando o fluxo de telas atual
  (`CLAUDE.md` seção 11, etapa 8).
- Resolver formalmente a decisão sobre Firebase (seções 12–13 de `CLAUDE.md`): manter, migrar
  tokens, ou remover dependências não usadas — com autorização explícita do usuário antes de
  remover qualquer dependência.
- Adicionar checagem de sessão ao abrir o app (login automático se já houver token válido em
  DataStore) — hoje ausente.
- Corrigir os bugs de navegação já mapeados (`"sintomas1"`/`"bemestar1"` sem branch; branch
  `"chat"` duplicado) como tarefa própria, combinada com o usuário — não como efeito colateral.
- Decidir se/como implementar IA real no chat, e nesse caso desenhar avaliação de qualidade desde
  o início.
- Testes instrumentados Android (`CLAUDE.md` seção 11, etapa 9) — hoje só existem os 2 templates
  padrão.

---

## 17. Inconsistências encontradas

1. **`CLAUDE.md` está desatualizado em relação ao estado real do Android.** Seções 3, 4, 5, 6, 9 e
   10 descrevem o Android como "essencialmente um protótipo de interface" sem ViewModel, sem
   Repository, sem rede, com Firebase como único mecanismo de autenticação real
   (`FirebaseAuth.createUserWithEmailAndPassword` em `CriarConta.kt`). A leitura de código nesta
   sessão mostra o oposto: existem `viewmodel/AuthViewModel.kt` e `RegistroDiarioViewModel.kt` reais
   (com `StateFlow`), uma camada `data/remote/` com Retrofit/OkHttp/Gson, `data/local/
   SessaoDataStore.kt` com DataStore para o token, e `LoginScreen`/`CriarConta` chamando o backend
   FastAPI em vez do Firebase — que hoje só aparece como comentário morto em
   `AuthViewModel.kt:68`. O texto de `CLAUDE.md` provavelmente não foi atualizado após os commits
   recentes (`b91b4db`, `e3b51ab`, `becc4cf`, visíveis no log).
2. **`CLAUDE.md` é internamente contraditório.** A seção 18 ("Nenhuma implementação de backend ou
   refatoração do Android foi iniciada ainda") conflita com as seções 7 e 11, que descrevem em
   detalhe um backend com 21 endpoints e 146 testes já implementados e concluídos.
3. **Incidente de segurança histórico, não presente nesta cópia.** O arquivo
   `docs/artigo-latinscience/instrucoes-build-ufu.md` (seção 0) afirma que o commit `8068aba`
   ("sem janatar") teve `backend/.env` versionado com uma `SECRET_KEY` real vazada, em uma "outra
   cópia" do repositório (referida como a máquina da UFU). Verificação feita nesta sessão:
   - `git log --all --oneline -- backend/.env` → vazio (nenhum commit em nenhum branch local
     jamais versionou `.env`).
   - `git cat-file -t 8068aba` → `fatal: Not a valid object name` — esse commit **não existe** nesta
     cópia do repositório (nem como commit alcançável, nem como objeto solto/dangling).
   - `git reflog --all` mostra que este clone partiu diretamente de `49cbfa7` (clone de
     `origin/main`) e seguiu para `4d71f31` em `develop` — nunca passou por `8068aba`.
   - Conclusão: **esta cópia local está limpa**; o incidente descrito é real (documentado pelo
     próprio projeto) mas ocorreu em outra cópia local do repositório, não nesta. Isso não prova
     que o segredo nunca chegou ao GitHub remoto — verificar isso exigiria inspecionar o histórico
     do repositório remoto diretamente (`git log --all` no servidor/GitHub), o que está
     `[NÃO FOI POSSÍVEL VERIFICAR]` a partir desta cópia local.
   - Nenhum valor de segredo foi visualizado ou é reproduzido aqui.
4. **`README` inexistente vs. documentação extensa em `CLAUDE.md`.** Não é uma inconsistência de
   conteúdo, mas vale registrar para o artigo: um leitor externo do repositório no GitHub não teria
   nenhuma porta de entrada em inglês ou formato README padrão.
5. **Requirements.txt duplicado** (raiz vs. `backend/`), já apontado pelo próprio `CLAUDE.md`
   (seção 2) como pendência a confirmar com o usuário — confirmado nesta sessão que o da raiz é um
   subconjunto estrito e desatualizado do de `backend/`.

---

## 18. Sugestão de figuras e tabelas

Considerando o limite de 4 páginas, sugerir apenas o que já tem base factual sólida:

- **Tabela de endpoints da API** (seção 6.3 deste documento) — já pronta, cabe direto no artigo,
  mostra concretamente a extensão do backend sem precisar de figura.
- **Tabela/resultado de testes** (146 passed, 94% cobertura) — forte candidata a virar uma
  tabela única de "resultados preliminares", é a evidência mais objetiva do projeto.
- **Diagrama simples de arquitetura** (Android Compose ↔ Retrofit/JWT ↔ FastAPI ↔ SQLAlchemy/
  SQLite) — vale a pena porque comunica em uma imagem o que a seção 6.1 descreve em texto; mas deve
  deixar visualmente claro que só login/cadastro/registro diário estão conectados hoje (ex.: linhas
  sólidas para o que funciona, pontilhadas para o que existe nos dois lados mas não está ligado).
  Não incluir "IA" no diagrama, para não sugerir uma capacidade que não existe.
- **Não sugerido:** captura de tela do chat ou do app rodando em emulador — sem esse dado (nenhum
  emulador ativo nesta sessão) e sem valor científico agregado; se produzida depois, uma única
  screenshot do fluxo de login funcionando (tela + log do uvicorn) seria mais valiosa que várias
  screenshots de UI estática.
- **Não sugerido:** gráfico de cobertura por módulo — a tabela de cobertura (seção 10.1) já é
  suficiente e mais compacta que um gráfico, dado o limite de espaço.

---

## 19. Mapa final do artigo

| Seção | O que apresentar | Evidência disponível | O que ainda falta |
|---|---|---|---|
| Introdução | Problema (acompanhamento de ciclo/saúde feminina), contexto de apps de saúde, proposta do Luna como app+backend próprio | Descrição funcional do app (`CLAUDE.md`, código lido) | Toda a fundamentação em literatura (seção 5.6/5.7) — nenhuma referência existe hoje |
| Desenvolvimento | Arquitetura Android(Compose/MVVM parcial)+FastAPI/JWT/SQLAlchemy; 21 endpoints; 5 entidades; autenticação JWT; ausência de IA | Código lido e citado (seções 6 e 7); testes e build executados nesta sessão | Descrição do que ainda não está conectado (perfil/ciclo/objetivo/consentimento no Android) precisa ficar explícita, não subentendida |
| Resultados preliminares | 146 testes passando, 94% cobertura, build Android funcional, login/cadastro reais chamando o backend | Execução real nesta sessão (seção 10) | Teste ponta a ponta com emulador ativo; qualquer validação de usuária |
| Limitações e Trabalhos futuros | Persistência parcial, sem IA, sem salvaguardas de saúde, sem LICENSE/README, decisão de Firebase pendente | Seções 15 e 16 deste documento | Nada a produzir para esta seção — já é honesta com o estado atual |
| Conclusão | Retomar: (1) sistema com backend real e testado, (2) integração Android parcial mas em progresso, (3) ausência total de IA e de validação de usuária como limite central, (4) trabalho em andamento explícito | — | Não escrever a conclusão agora; apenas estes pontos a retomar |

---

## 20. As 5 prioridades

Em ordem de importância para transformar o estado atual em um Short Paper consistente:

1. **Decidir e registrar a contribuição/framing do artigo.** Antes de mais código, decidir se o
   artigo é "relato de arquitetura de um app de saúde em desenvolvimento" (sustentável hoje) ou se
   pretende reivindicar algo sobre IA/avaliação — porque hoje não há nenhuma base para a segunda
   opção. Isso direciona todo o resto.
2. **Rodar o teste ponta a ponta real com emulador** (`instrucoes-build-ufu.md`), já que esta
   sessão confirmou que a máquina atual tem JDK e Android SDK — é a evidência de maior impacto por
   menor esforço restante.
3. **Levantar a literatura mínima** para a Introdução (seção 5.7) — sem isso, a introdução do artigo
   fica sem embasamento e o Short Paper não tem "chão" acadêmico.
4. **Escrever com honestidade a ausência de IA e as lacunas de privacidade/saúde** (seções 7, 8, 9)
   em vez de as minimizar — isso é factualmente mais defensável do que qualquer tentativa de
   sugerir uma capacidade que não existe, e revisores tendem a penalizar mais a incoerência entre
   texto e sistema do que a limitação em si.
5. **Consolidar as tabelas de endpoints, testes e cobertura** (seções 6.3, 10, 10.1 deste
   documento) diretamente no corpo do artigo — é o material mais pronto e objetivo que existe hoje,
   e deve ocupar o espaço central da seção de Resultados.
