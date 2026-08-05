# CLAUDE.md

Este arquivo fornece orientações ao Claude Code (claude.ai/code) para trabalhar com o código deste repositório.

## 1. Visão geral e objetivo do projeto

Luna é um aplicativo Android (Kotlin + Jetpack Compose, package `com.example.ic_app`, nome do módulo `ic_app`) voltado ao acompanhamento do ciclo menstrual e da saúde feminina: humor, sintomas, fertilidade, gestação e bem-estar. Todo texto de interface, nomes de tela e identificadores de código estão em português (pt-BR); código novo deve seguir o mesmo padrão (ex.: `nomeUsuario`, `onContinuarClick`, `telaAtual`).

Hoje o projeto é essencialmente um protótipo de interface: todas as telas do fluxo existem e navegam entre si, mas nenhum dado é persistido de forma real — tudo vive em `remember { mutableStateOf(...) }` e se perde ao fechar o app.

O objetivo do trabalho em andamento é evoluir esse protótipo para um app com dados reais e multiusuário, por meio de:
- Um backend próprio em FastAPI + SQLAlchemy, usando SQLite em desenvolvimento e PostgreSQL futuramente.
- Autenticação via JWT emitido pelo backend.
- Reorganização do Android em MVVM, consumindo o backend via Retrofit e armazenando o token de sessão com DataStore.
- Substituição futura da navegação por `when`/`telaAtual` por Navigation Compose.
- Uma decisão explícita sobre o papel do Firebase (hoje usado parcialmente para autenticação) diante da nova autenticação JWT (ver seções 12 e 13).

Nenhuma implementação de backend ou refatoração do Android foi iniciada ainda — este arquivo documenta o estado atual do código e o planejamento, não um trabalho já em curso.

## 2. Comandos

### Android
Executar a partir da raiz do repositório com o Gradle wrapper:
- Compilar APK de debug: `./gradlew assembleDebug`
- Instalar no dispositivo/emulador conectado: `./gradlew installDebug`
- Rodar testes unitários (JVM, `app/src/test`): `./gradlew test`
- Rodar um único teste unitário: `./gradlew test --tests "com.example.ic_app.ExampleUnitTest"`
- Rodar testes instrumentados (`app/src/androidTest`, requer dispositivo/emulador): `./gradlew connectedAndroidTest`
- Lint: `./gradlew lint`

### Backend (FastAPI, em `backend/`)
O backend tem seu próprio virtualenv em `backend/.venv`.
- Instalar dependências: `pip install -r backend/requirements.txt` (dentro do virtualenv)
- Rodar o servidor de desenvolvimento: `uvicorn app.main:app --reload` (executar a partir de `backend/`)
- Existe um `requirements.txt` duplicado na raiz do repositório, quase idêntico ao de `backend/`; o de `backend/` é o realmente usado pela aplicação FastAPI. Confirmar com o usuário se o da raiz ainda é necessário antes de consolidar dependências.

## 3. Funcionalidades Android já implementadas

Fluxo completo de telas, todas navegáveis e visualmente prontas, mas sem persistência real (ver seção 4):

- **Onboarding**: `BemVindoScreen` (splash) → `ConsentScreen` (checkbox de consentimento com diálogo de política de dados) → `DataNascimentoScreen` (valida idade mínima de 12 anos) → `PesoScreen` → `NomeScreen` → `ObjetivoScreen` (escolha de um entre 6 objetivos).
- **Sub-fluxos por objetivo** (`objetivos/<objetivo>/Screen1..3`): `regularidade`, `entender_corpo` (uma única tela), `engravidar`, `gestacao`, `saude_mental` — cada tela coleta uma opção selecionada localmente por meio de cards clicáveis.
- **Home** (`HomeScreen`): saudação conforme o horário do dia, chips de status (humor/energia/sintomas, com valores fixos), card de ciclo (valores fixos, ex. "Dia 14 de 28"), atalhos para chat, calendário, perfil e registro do dia.
- **Registrar hoje** (`RegistrarHojeScreen`): seleção de humor (emoji), sintoma principal e campo de observação em texto livre.
- **Calendário** (`CalendarioScreen`): seleção de dia dentro de uma lista fixa de dias da semana, com checkbox de "menstruação registrada" para o dia selecionado.
- **Chat com a Lunete** (`ChatLunaScreen`): interface de chat funcional, com respostas geradas localmente por regras de palavra-chave (`gerarRespostaSimulada`), sem qualquer IA real por trás.
- **Perfil** (`PerfilScreen`): campos editáveis de nome, data de nascimento, peso e objetivo, inicializados com o estado global de `AppScreen`.
- **Autenticação** (`CriarConta`): cadastro de conta chamando `FirebaseAuth.createUserWithEmailAndPassword` de fato.

## 4. Funcionalidades incompletas ou com problemas

- Nenhum dado é persistido: perfil, humor, sintomas, observações, registros de calendário, mensagens de chat e respostas dos sub-fluxos de objetivo existem apenas em memória durante a sessão do Compose.
- As opções selecionadas nas telas `objetivos/<objetivo>/Screen1..3` são coletadas na interface, mas o callback correspondente em `AppScreen.kt` ignora o valor recebido (ex.: `onContinuarClick = { telaAtual = "engravidar2" }`) — a informação nunca é armazenada, nem no estado global.
- Os objetivos "Acompanhar sintomas" e "Melhorar meu bem-estar" resolvem para as chaves `"sintomas1"` e `"bemestar1"` em `ObjetivoScreen`/`AppScreen.kt`, mas não existe nenhum branch no `when` para essas chaves — selecionar um desses objetivos hoje resulta em tela em branco.
- `LoginScreen` não chama `FirebaseAuth.signInWithEmailAndPassword` nem qualquer outro serviço: o botão "Entrar" avança independente do que for digitado.
- Os botões "Esqueci minha senha" e "Entrar com Google" em `LoginScreen` não têm ação implementada.
- O botão "Salvar alterações" em `PerfilScreen` apenas retorna para a home, sem persistir as edições feitas nos campos.
- O calendário é estático (uma única semana fixa, sem lógica real de mês/ano), e as previsões de ciclo exibidas na home e no calendário são texto fixo, não calculado.
- O backend FastAPI só tem as rotas `/` e `/health` — nenhuma entidade, rota de negócio ou autenticação implementada.
- Há dois arquivos `requirements.txt` (raiz e `backend/`) quase idênticos, sem que fique claro qual deve continuar existindo.
- Não existe camada de ViewModel ou Repository em nenhuma tela — toda lógica está dentro dos próprios `@Composable`.
- `HumorItem.kt` e `RegistroOpcaoCard.kt` estão soltos em `app/src/main/java/`, sem pacote, e duplicam composables homônimos já definidos dentro de `home/RegistrarHojeScreen.kt`.
- Não há testes além dos templates padrão gerados pelo Android Studio (`ExampleUnitTest`, `ExampleInstrumentedTest`).

## 5. Arquitetura atual do Android

Não existe `NavHost` nem Jetpack Navigation. Toda a navegação está centralizada em `app/src/main/java/com/example/ic_app/navigation/AppScreen.kt`:
- `telaAtual: String` guarda a chave da tela atual (ex.: `"home"`, `"regularidade1"`, `"engravidar2"`) e controla um bloco `when` que compõe a tela correspondente.
- Um pequeno conjunto de variáveis (`nomeUsuario`, `objetivoUsuario`, `dataNascimentoUsuario`, `pesoUsuario`), todas `remember { mutableStateOf(...) }`, é o único estado compartilhado entre telas. As telas recebem esses valores como parâmetros e os alteram por meio de callbacks (`onContinuarClick = { valor -> ... }`).
- Não existe ViewModel nem Repository hoje: toda lógica de estado e navegação vive nos próprios composables.

Fluxo de telas: `boas-vindas` → `consentimento` → `data_nascimento` → `peso` → `nome` → `objetivo`, que se ramifica em um dos sub-fluxos por objetivo (`regularidade1..3`, `entender1`, `engravidar1..3`, `gestacao1..3`, `saudemental1..3`), todos terminando em `"home"`. A partir da home é possível chegar a `chat`, `calendario`, `perfil`, `registrar_hoje` e à autenticação (`login` → `criar`).

Estrutura de pacotes em `app/src/main/java/com/example/ic_app/`:
- `onboarding/` — telas exibidas antes do primeiro acesso à home.
- `objetivos/<objetivo>/` — sub-fluxos de telas por objetivo escolhido.
- `home/` — `HomeScreen`, `ChatLunaScreen`, `CalendarioScreen`, `PerfilScreen`, `RegistrarHojeScreen`.
- `auth/` — `LoginScreen`, `CriarConta`.
- `navigation/` — `AppScreen`, a state machine descrita acima.
- `components/` — composables reutilizáveis (ex.: `ObjetivoCard`).
- `ui/theme/` — tema Compose (`Ic_appTheme`, `Color.kt`, `Type.kt`); a maioria das telas define paletas de cor locais (`Color(0xFF...)`) em vez de usar `MaterialTheme.colorScheme` — ao editar uma tela, seguir a paleta local dela.
- `HumorItem.kt` e `RegistroOpcaoCard.kt` ficam diretamente em `java/`, sem pacote (ver seção 4 sobre a duplicação com `RegistrarHojeScreen.kt`).

## 6. Situação atual do Firebase

- Dependências presentes no Android: `firebase-auth` e `firebase-firestore`, via `firebase-bom:33.7.0`, além do `google-services.json` versionado em `app/`.
- Uso real: apenas `FirebaseAuth` em `CriarConta.kt`, para cadastro por e-mail/senha (`createUserWithEmailAndPassword`). `LoginScreen.kt` não chama nenhum método do Firebase. Firestore não é usado em nenhum ponto do código, apesar de estar como dependência.
- No lado Python, `backend/requirements.txt` inclui `firebase_admin` e bibliotecas do Firestore, mas `backend/app/main.py` não importa nem inicializa nada disso.
- Resumo: o Firebase está integrado apenas parcialmente (só cadastro, só no cliente Android), sem verificação de sessão, sem uso de Firestore e sem qualquer integração com o backend Python.

## 7. Situação atual do FastAPI

- `backend/app/main.py` inclui o router de autenticação e CORS (`allow_origins=["*"]`, `allow_credentials=False` — sem cookies, a autenticação é via header `Authorization: Bearer`), além das rotas originais `GET /` e `GET /health`. Nenhuma rota de domínio (perfil, registro diário, ciclo, objetivo) existe ainda — isso é a etapa 4.
- A etapa "Fundação do backend" (etapa 2 do plano) está implementada: `app/core/config.py` lê `DATABASE_URL` do `.env` (`sqlite:///./luna.db`); `app/db/base.py` e `app/db/session.py` configuram `Base`, `engine`, `SessionLocal` e `get_db()`; `app/models/` tem as 5 entidades da seção 8 (exceto `MensagemChat`, ainda pendente de decisão) mais `app/models/enums.py` com `HumorEnum` e `SintomaEnum`.
- A etapa "Autenticação JWT no backend" (etapa 3) também está implementada:
  - `app/core/security.py` — hash de senha com `bcrypt` (`hash_senha`/`verificar_senha`) e criação/decodificação do JWT com `PyJWT` (`criar_access_token`/`decodificar_access_token`), usando `SECRET_KEY`, `ALGORITHM=HS256` e `ACCESS_TOKEN_EXPIRE_MINUTES=60` (sem refresh token por enquanto), configurados em `app/core/config.py` e no `.env` (a `SECRET_KEY` real só existe no `.env`, não versionado; `.env.example` tem um placeholder).
  - `app/schemas/usuario.py` (`UsuarioCreate`, `UsuarioOut`) e `app/schemas/auth.py` (`LoginRequest`, `Token`, `TokenPayload`).
  - `app/dependencies.py` — `get_current_usuario`, dependência que valida o `Bearer` token e carrega o `Usuario` correspondente; usada para proteger rotas.
  - `app/routers/auth.py` — `POST /auth/registrar` (recebe só `email`/`senha`/`nome`, os mesmos campos que `CriarConta.kt` já coleta hoje; `data_nascimento`/`peso`/`objetivo_atual` ficam para os endpoints de perfil da etapa 4), `POST /auth/login` (recebe `email`/`senha` em JSON, não o formulário OAuth2 padrão) e `GET /auth/me` (rota protegida de referência).
  - `backend/tests/test_auth.py` cobre registro, e-mail duplicado, login correto/incorreto e acesso a `/auth/me` com e sem token; `conftest.py` ganhou a fixture `client` (banco SQLite em memória isolado por teste, sobrepondo `get_db` do app real). Suíte completa: 9 testes, `pytest` a partir de `backend/`.
- A etapa "Endpoints de domínio" (etapa 4) está **parcialmente implementada**: o módulo de perfil (primeiro da ordem combinada na seção 11) está concluído.
  - `app/schemas/usuario.py` ganhou `UsuarioPerfilUpdate` (todos os campos opcionais, exceto validação de conteúdo): `nome` (não pode ser `null` nem vazio — um `model_validator(mode="before")` rejeita `null` explícito antes da validação de campo, que por sua vez rejeita string vazia/só espaços), `data_nascimento` (não pode ser no futuro; idade mínima de `IDADE_MINIMA_ANOS` = 12 anos), `peso` (0 < peso ≤ 500), `objetivo_atual` (deve estar em `OBJETIVOS_VALIDOS`, os 5 objetivos de `ObjetivoScreen.kt` mais `"Geral"`, usado pelo botão "Pular"). `email` e `senha` não fazem parte deste schema — não são alteráveis por essas rotas. Campos ausentes no corpo da requisição permanecem inalterados (`model_dump(exclude_unset=True)`); campos enviados explicitamente como `null` limpam o valor, exceto `nome`.
  - `app/routers/perfil.py` — `GET /perfil` (retorna o usuário autenticado) e `PATCH /perfil` (atualização parcial), ambos protegidos por `get_current_usuario`; incluído em `main.py`.
  - `backend/tests/test_perfil.py` cobre autenticação obrigatória, leitura, atualização total/parcial, cada validação (objetivo inválido, idade abaixo do mínimo, data futura, peso inválido, nome vazio, nome `null`), limpeza de campo com `null` e isolamento entre usuários diferentes.
  - O módulo de registro diário (segundo da ordem combinada) também está concluído.
    - `app/models/registro_diario.py` — `usuario_id` agora tem `index=True`, e há `UniqueConstraint("usuario_id", "data")`: no máximo um `RegistroDiario` por usuário por data. Migration `alembic/versions/0267a59d2ee4_indice_e_unicidade_em_registro_diario.py` (aplicada via `op.batch_alter_table`, necessário para adicionar índice/constraint em uma tabela SQLite já existente) cria o índice e a constraint em cima da migration inicial.
    - `app/schemas/registro_diario.py` — `RegistroDiarioCreate` (`data`, `humor: Optional[HumorEnum]`, `sintoma_principal: Optional[SintomaEnum]`, `observacao: Optional[str]`; valida `data` não futura, remove espaços das pontas de `observacao` antes de validar/salvar, e exige ao menos um entre `humor`/`sintoma_principal`/`observacao` preenchido — `observacao` só em espaços não conta como preenchida), `RegistroDiarioUpdate` (mesmos campos, todos opcionais para PATCH parcial, com a mesma normalização de `observacao`; `data` não pode ser `null`, mas pode ser trocada; `humor`/`sintoma_principal`/`observacao` podem ser limpos com `null` desde que ao menos um dos três continue preenchido após a atualização — essa verificação do estado combinado é feita no router, que conhece o registro atual) e `RegistroDiarioOut`.
    - `app/routers/registros_diarios.py`, prefixo `/registros-diarios`, todas as rotas protegidas por `get_current_usuario`: `POST` (409 se já existir registro do usuário na mesma data — a mensagem indica o `id` do registro existente e orienta a usar `PATCH /registros-diarios/{id}` para atualizá-lo em vez de criar outro), `GET` (lista apenas os registros do usuário autenticado, com filtros opcionais `data_inicio`/`data_fim` por query string — 422 se `data_inicio` for maior que `data_fim` —, ordenado por `data` decrescente, da mais recente para a mais antiga), `GET /{id}`, `PATCH /{id}` (parcial; 409 também ao mover para uma data já usada por outro registro do mesmo usuário) e `DELETE /{id}` (204). Em todas as rotas por id, um registro que existe mas pertence a outro usuário retorna **404** (não 403), para não revelar a um usuário que o id pertence a outra conta. O `_commit_ou_conflito` centraliza o commit de `POST`/`PATCH`: além da verificação prévia feita em cada rota, ele também captura `IntegrityError` (violação da constraint de unicidade por uma corrida entre requisições concorrentes), faz `rollback()` e responde 409 em vez de deixar propagar um 500.
    - `backend/tests/test_registros_diarios.py` cobre criação (todos os campos, cada campo isoladamente, ausência de todos os três, `observacao` em branco, `observacao` com espaços nas pontas removidos ao salvar, data futura, data duplicada para o mesmo usuário com a mensagem de orientação ao `PATCH`, mesma data permitida entre usuários diferentes, enum inválido), listagem (isolamento entre usuários, filtro por intervalo de datas, sem filtro, ordenação decrescente por data, intervalo `data_inicio` > `data_fim` → 422), leitura/atualização/exclusão por id (sem token, inexistente, pertencente a outro usuário → 404, sucesso) e as regras de `PATCH` (parcial, limpar campo mantendo outro preenchido, limpar todos viola a invariante, `data: null` rejeitado, data futura, conflito de data ao trocar a data, manter a mesma data não gera conflito, normalização de `observacao`).
  - O módulo de registro de ciclo (terceiro da ordem combinada) também está concluído, seguindo o mesmo padrão do registro diário.
    - `app/models/registro_ciclo.py` — `usuario_id` com `index=True`, `UniqueConstraint("usuario_id", "data")`. Migration `alembic/versions/5d45b487957e_indice_e_unicidade_em_registro_ciclo.py` (mesmo padrão `batch_alter_table` da migration equivalente de `RegistroDiario`) cria o índice e a constraint em cima da migration anterior.
    - `app/schemas/registro_ciclo.py` — `RegistroCicloCreate` (`data`, `menstruacao: bool` obrigatório, `observacao: Optional[str]`; valida `data` não futura, remove espaços das pontas de `observacao` e rejeita `observacao` só com espaços — aqui incondicionalmente, diferente do registro diário, pois não há um "ao menos um entre vários campos" para `RegistroCiclo`: `menstruacao` é sempre um booleano presente), `RegistroCicloUpdate` (mesmos campos, todos opcionais para PATCH parcial; `data` e `menstruacao` não podem ser `null` — a primeira é obrigatória para o registro existir, a segunda deve sempre ser um booleano válido —, mas `observacao` pode ser limpa com `null` por ser opcional) e `RegistroCicloOut` (inclui `fase_calculada`, sempre `None` nesta etapa — nenhuma lógica de cálculo de fase ou previsão de ciclo foi implementada, por decisão explícita de escopo).
    - `app/routers/registros_ciclo.py`, prefixo `/registros-ciclo`, mesma estrutura do router de registro diário: `usuario_id` obtido exclusivamente de `get_current_usuario` (nunca aceito do cliente, mesmo que enviado no corpo — o schema o ignora), `POST`/`PATCH` com 409 + orientação a usar `PATCH /registros-ciclo/{id}` em caso de duplicidade de data (inclusive ao mover a data no `PATCH`), `_commit_ou_conflito` tratando `IntegrityError` de corrida com rollback + 409, `GET` com filtros opcionais `data_inicio`/`data_fim` (combináveis ou isolados, 422 se `data_inicio` > `data_fim`) ordenado por `data` decrescente, e 404 (não 403) em `GET`/`PATCH`/`DELETE` por id quando o registro pertence a outro usuário.
    - `backend/tests/test_registros_ciclo.py` cobre os mesmos casos do registro diário adaptados a `menstruacao` (criação com `true`/`false`, ausente, não booleana), duplicidade de data com mensagem de orientação ao `PATCH`, `usuario_id` enviado pelo cliente sendo ignorado, listagem com filtro só por `data_inicio`, só por `data_fim`, por período e intervalo inválido, ordenação decrescente, isolamento entre usuários, e as regras de `PATCH` (parcial, `observacao` limpa com `null`, `menstruacao`/`data` rejeitando `null`, data futura, `observacao` só com espaços, conflito de data ao trocar a data, mesma data não gera conflito).
  - O módulo de respostas de objetivo (quarto e último da ordem combinada) também está concluído, com apenas dois endpoints (por decisão explícita de escopo: sem `PATCH`, `DELETE` ou consulta por id nesta etapa — a atualização acontece via `POST`, ver abaixo).
    - `app/models/resposta_objetivo.py` — `usuario_id` com `index=True`; `UniqueConstraint("usuario_id", "objetivo", "etapa")` (no máximo uma resposta por usuário, por objetivo e por etapa). Migration `alembic/versions/efa38d108ef2_indice_e_unicidade_em_resposta_objetivo.py` (mesmo padrão `batch_alter_table` das duas migrations anteriores) cria o índice e a constraint em cima da migration anterior.
    - `app/schemas/resposta_objetivo.py` — `RespostaObjetivoCreate` (`objetivo: str`, `etapa: int`, `opcao_selecionada: str`, todos obrigatórios; único schema, pois não há `PATCH`). `objetivo` reaproveita a constante `OBJETIVOS_VALIDOS` já definida em `app/schemas/usuario.py` (os 5 cards de `ObjetivoScreen.kt` mais `"Geral"`) — o mesmo valor gravado em `objetivoUsuario` em `AppScreen.kt` antes de entrar no sub-fluxo correspondente, então nenhum valor novo foi inventado. `etapa` deve ser um inteiro positivo (`>= 1`); o model em si não impõe um intervalo (não há `CheckConstraint`), então essa é a única regra de validação além do tipo — decisão documentada no relatório desta etapa, já que os sub-fluxos têm números de etapas diferentes entre si (`entender1` tem só 1 etapa; os demais têm 3) e essa variação por objetivo não está formalizada em nenhum lugar do código ou deste arquivo, então não foi codificada como regra. `opcao_selecionada` tem espaços das pontas removidos e não pode ficar vazia após a remoção. `RespostaObjetivoOut` inclui `usuario_id`, mas **nunca o aceita como entrada** — o schema de criação não declara esse campo, então qualquer `usuario_id` enviado no corpo é ignorado pelo Pydantic; o valor usado é sempre o do usuário autenticado.
    - `app/routers/respostas_objetivo.py`, prefixo `/respostas-objetivo`, ambas as rotas exigem `get_current_usuario`. `POST` funciona como criação-ou-atualização (upsert): busca uma resposta existente pela combinação `usuario_id` + `objetivo` + `etapa` do usuário autenticado; se não existir, cria e responde **201**; se existir, atualiza `opcao_selecionada` e responde **200** (status dinâmico via injeção do parâmetro `Response` do FastAPI e `response.status_code = ...`, mutado antes do retorno — compatível com `response_model`). Condição de corrida entre duas criações concorrentes da mesma combinação: `IntegrityError` no commit é capturado, a sessão sofre `rollback()`, o registro criado pela requisição concorrente é localizado e atualizado com a nova `opcao_selecionada` em vez de propagar um 500. `GET` lista apenas as respostas do usuário autenticado, com filtros opcionais e combináveis `objetivo`/`etapa` por query string (sem validação de valor — um filtro que não corresponde a nada apenas retorna lista vazia), ordenada deterministicamente por `objetivo` e depois por `etapa`, ambos ascendentes.
    - `backend/tests/test_respostas_objetivo.py` cobre criação, atualização ao reenviar a mesma combinação (sem duplicar — confirmado via contagem na listagem), mesma etapa em objetivos diferentes, etapas diferentes no mesmo objetivo, mesma combinação entre usuários diferentes, `opcao_selecionada` vazia/só espaços/com espaços nas pontas, `objetivo` e `etapa` inválidos, `usuario_id` enviado no corpo sendo ignorado, listagem (vazia, isolada entre usuários, filtrada por `objetivo`, por `etapa`, por ambos) e acesso sem token.
  - O módulo de consentimento, embora não fizesse parte da "ordem combinada" original da etapa 4 (ver observação abaixo), também está concluído, seguindo o mesmo padrão de criação-ou-atualização de respostas de objetivo, mas com apenas dois endpoints, sem filtros e sem listagem geral (só o consentimento atual).
    - `app/models/consentimento.py` — `usuario_id` com `index=True`; `UniqueConstraint("usuario_id", "versao_termos")` (no máximo um consentimento por usuário e por versão dos termos). Migration `alembic/versions/8e0d5c14ae10_indice_e_unicidade_em_consentimento.py` (mesmo padrão `batch_alter_table` das três migrations anteriores) cria o índice e a constraint em cima da migration anterior.
    - `app/schemas/consentimento.py` — `ConsentimentoCreate` (`versao_termos: str`, obrigatória; espaços das pontas removidos, vazia ou só espaços rejeitada — mesmo padrão de `opcao_selecionada` em respostas de objetivo). Sem enum de versões válidas: `ConsentScreen.kt` hoje só tem um checkbox local, sem nenhum identificador de versão dos termos no código Android, então não havia nenhum valor já aceito pelo backend ou documentado no CLAUDE.md para reaproveitar (diferente de `objetivo`, que reaproveitou `OBJETIVOS_VALIDOS`). `ConsentimentoOut` inclui `usuario_id` e `aceito_em`, mas **nenhum dos dois é aceito como entrada** — `ConsentimentoCreate` não declara esses campos, então qualquer valor enviado no corpo para eles é ignorado pelo Pydantic; `usuario_id` vem sempre do usuário autenticado, `aceito_em` é sempre preenchido pelo servidor (na criação, pelo `server_default` da coluna; na reafirmação, ver abaixo).
    - `app/routers/consentimentos.py`, prefixo `/consentimentos`, ambas as rotas exigem `get_current_usuario`. `POST` funciona como criação-ou-reafirmação: busca um consentimento existente pela combinação `usuario_id` + `versao_termos` do usuário autenticado; se não existir, cria e responde **201**; se existir, **atualiza `aceito_em` para o instante atual** (via `datetime.now(timezone.utc)`, já que o valor de coluna com `server_default` só se aplica em `INSERT`, não em `UPDATE`) e responde **200** — decisão registrada aqui: reenviar a mesma versão é tratado como uma reafirmação do consentimento (mais coerente com o nome do campo, "aceito em", do que deixá-lo travado na primeira aceitação), sem criar linha duplicada e sem apagar consentimentos de outras versões, que continuam intactos como histórico. Mesma proteção de corrida das demais entidades: `IntegrityError` no commit é capturado, a sessão sofre `rollback()`, o registro criado pela requisição concorrente é localizado e reafirmado (mesmo comportamento do reenvio normal) em vez de propagar um 500. `GET /atual` retorna o consentimento mais recente do usuário autenticado, ordenado por `aceito_em` decrescente e, em caso de empate, por `id` decrescente; **404** se a usuária ainda não tiver nenhum consentimento.
    - `backend/tests/test_consentimentos.py` cobre criação, `aceito_em` preenchido pelo servidor e nunca substituído por um valor enviado no corpo, `versao_termos` vazia/só espaços/com espaços nas pontas removidos, reenvio da mesma versão sem duplicar (e atualizando `aceito_em`, o que faz uma versão antiga voltar a ser "a mais recente"), versões diferentes na mesma usuária, mesma versão em usuárias diferentes, `usuario_id` enviado no corpo sendo ignorado, `GET /atual` (sem token, sem consentimentos → 404, com um consentimento, retornando a versão mais recente entre várias, isolado entre usuários).
- Revisão geral de tratamento de erros e documentação Swagger/OpenAPI *(concluída, cross-cutting — não é uma etapa própria do plano da seção 11)*: preservou todo comportamento público e regra de negócio já validados; nenhuma migration foi necessária.
  - Duas lacunas reais de tratamento de erro foram corrigidas: `POST /auth/registrar` não tinha proteção contra `IntegrityError` numa corrida de e-mail duplicado (só a verificação prévia, sem commit protegido) — agora usa o mesmo padrão `try/commit/except IntegrityError/rollback` das demais entidades. Em `respostas_objetivo.py` e `consentimentos.py`, o ramo de corrida que não encontra o registro concorrente fazia um `raise` puro (podendo, em tese, propagar um 500) — agora responde 409 explicitamente, como já fazem `registros_diarios.py`/`registros_ciclo.py`.
  - Tags dos routers renomeadas para nomes em português (`Autenticação`, `Perfil`, `Registros Diários`, `Registros do Ciclo`, `Respostas dos Objetivos`, `Consentimentos`, `Sistema` para `/` e `/health`), com `openapi_tags` descrevendo cada uma em `app/main.py`. Todos os 21 endpoints ganharam `summary`, `description`, `response_description` e `responses={...}` com os códigos não-2xx aplicáveis (401/404/409/422, conforme o caso); os filtros de query (`data_inicio`, `data_fim`, `objetivo`, `etapa`) ganharam descrição via `Query(..., description=...)`.
  - Todos os schemas de request/response ganharam exemplos via `json_schema_extra={"examples": [...]}` (Pydantic v2), sem alterar nenhuma validação ou tipo. `oauth2_scheme` em `app/dependencies.py` ganhou `description`/`scheme_name` explicando o Bearer JWT no Swagger — mudança só de metadado, o dependency `get_current_usuario` continua idêntico.
  - `backend/tests/test_openapi.py` (novo) cobre estruturalmente: `/openapi.json`/`/docs` acessíveis, presença das 7 tags e dos 13 paths implementados, esquema de segurança documentando "Bearer"/"JWT", ausência de `senha_hash`/`SECRET_KEY` em qualquer schema exposto, códigos de resposta documentados nos endpoints principais e presença de `security` nas rotas protegidas (e ausência nas públicas). `backend/tests/test_auth.py` ganhou dois testes que faltavam: token inválido e token expirado retornando 401.
  - Suíte completa do backend: **146 testes** (3 health + 8 auth + 13 perfil + 37 registro diário + 38 registro de ciclo + 20 respostas de objetivo + 18 consentimento + 10 openapi), `pytest` a partir de `backend/`.
- A etapa 4 (endpoints de domínio), **como originalmente delimitada na seção 11 — perfil, registro diário, registro de ciclo e respostas de objetivo —, está concluída em suas quatro partes**, e o módulo de `Consentimento` (que não fazia parte dessa lista original, mas é um domínio próprio desde a etapa 2, ver seção 8) também foi implementado, então **todas as entidades de domínio hoje modeladas têm endpoint**, com exceção de `MensagemChat` (ainda pendente de decisão, ver seção 8) e de qualquer cálculo de fase do ciclo ou previsão da próxima menstruação (`fase_calculada` permanece sempre `null`, por decisão explícita de escopo). Ainda não há: integração com o Android (`LoginScreen.kt`/`CriarConta.kt` continuam falando só com o Firebase, e `PerfilScreen.kt`/`RegistrarHojeScreen.kt`/`CalendarioScreen.kt`/telas de `objetivos/<objetivo>/`/`ConsentScreen.kt` ainda não chamam o backend — isso é a etapa 7/6) e nenhuma camada de rede no Android (etapa 5).

## 8. Entidades sugeridas para o banco de dados

Com base no que as telas hoje coletam ou exibem:

| Entidade | Campos sugeridos | Origem na interface |
|---|---|---|
| `Usuario` | id, email, senha_hash, nome, data_nascimento, peso, objetivo_atual, criado_em | `NomeScreen`, `PesoScreen`, `Data_nascimento.kt`, `ObjetivoScreen`, `CriarConta`/`LoginScreen` |
| `Consentimento` | id, usuario_id, aceito_em, versao_termos | `ConsentScreen` (hoje apenas um boolean local, nunca persistido) |
| `RegistroDiario` | id, usuario_id, data, humor, sintoma_principal, observacao, criado_em | `RegistrarHojeScreen` |
| `RegistroCiclo` | id, usuario_id, data, menstruacao (boolean), fase_calculada, observacao | `CalendarioScreen` (hoje um único boolean fixo por dia selecionado) |
| `RespostaObjetivo` | id, usuario_id, objetivo, etapa, opcao_selecionada | Telas `objetivos/<objetivo>/Screen1..3` (hoje descartadas, ver seção 4) |
| `MensagemChat` | id, usuario_id, texto, enviada_pelo_usuario, criado_em | `ChatLunaScreen`, se decidido persistir o histórico |

Pontos a decidir junto com o usuário antes de modelar o schema definitivo:
- Se `RegistroDiario` e `RegistroCiclo` devem ser uma única tabela (um registro por dia com todos os campos) ou tabelas separadas.
- Se `MensagemChat` deve ser persistida desde já ou só quando houver uma IA real por trás do chat.
- Se `humor` e `sintoma_principal` devem ser enums fixos no banco ou texto livre — hoje a interface usa um conjunto pequeno e fixo de opções, o que favorece enum.

## 9. Arquitetura pretendida para backend e Android

### Backend
- SQLAlchemy como ORM, com `DATABASE_URL` configurada via `.env` (SQLite em desenvolvimento, ex. `sqlite:///./luna.db`; PostgreSQL em produção trocando apenas a URL de conexão).
- Autenticação por JWT: endpoint de registro (hash de senha, ex. `passlib`/`bcrypt`) e de login (emissão de access token), com uma dependência do FastAPI (`Depends`) para proteger rotas validando o token.
- Organização de diretórios já implementada na etapa 2 (ver seção 7): `app/models/` (SQLAlchemy), `app/schemas/` (Pydantic, ainda vazio), `app/routers/` (rotas por entidade, ainda vazio), `app/core/` (config já criada; segurança/JWT entra na etapa 3), `app/db/` (`base.py` e `session.py`, no lugar de um único `db.py`).
- Migrations com Alembic já configuradas desde a fundação (e não só "quando o schema estabilizar" — decisão tomada para evitar recriar o banco a cada mudança de schema).

### Android (MVVM)
- Camadas: `ui/` (Compose, já existente) → `viewmodel/` (um `ViewModel` por tela ou fluxo, expondo `StateFlow`/`State`) → `repository/` (fonte única de verdade) → `data/remote/` (serviços Retrofit e DTOs) e `data/local/` (DataStore para o token, e persistência local futura se necessário).
- Retrofit para consumir o backend FastAPI, com um interceptor OkHttp que anexa o JWT armazenado no DataStore às requisições autenticadas.
- DataStore (Preferences DataStore) substituindo o estado hoje mantido apenas em memória em `AppScreen` para o token de sessão.
- Navigation Compose no futuro, substituindo o `when (telaAtual)` em `AppScreen.kt` por rotas equivalentes às mesmas chaves de tela, preservando o fluxo descrito na seção 5.

## 10. Regras para preservar o código existente

- Não reescrever telas inteiras ao introduzir ViewModel/Repository: extrair estado e lógica de negócio da tela para a nova camada, mantendo os composables e a experiência visual como estão, a menos que o usuário peça mudança de interface explicitamente.
- Migração incremental, não "big bang": `AppScreen.kt` e o `when (telaAtual)` devem continuar funcionando enquanto backend e MVVM são introduzidos tela por tela. A troca para Navigation Compose é uma etapa própria (ver seção 11), só depois que o restante estiver estável.
- Nenhuma dependência (Firebase ou qualquer outra) deve ser removida sem autorização explícita do usuário — mesmo depois de uma decisão de arquitetura tomada (ver seções 12 e 13), a remoção efetiva do código é um passo separado que precisa ser confirmado antes de ser executado.
- Manter os nomes e textos em português e o padrão de callbacks (`onContinuarClick`, `onPularClick`, `onXClick`) já usado em todas as telas — código novo de rede/ViewModel deve seguir a mesma convenção.
- Não corrigir bugs de UI/fluxo já mapeados (como as chaves `sintomas1`/`bemestar1` sem branch) como efeito colateral de uma tarefa de backend — são achados a resolver como tarefa própria, combinada com o usuário.
- Não introduzir Room ou outra persistência local sem necessidade explícita — o plano atual é ter o backend remoto como fonte de verdade; cache local só se for pedido depois.
- Antes de alterar `objetivos/<objetivo>/Screen1..3`, `HumorItem.kt` ou `RegistroOpcaoCard.kt`, verificar as duplicações já mapeadas na seção 4 para não piorar a inconsistência existente.

## 11. Plano de implementação por etapas

1. **Planejamento** *(concluído)* — mapear entidades, decidir o schema definitivo e a estratégia de autenticação antes de escrever código.
2. **Fundação do backend** *(concluído)* — estrutura de projeto FastAPI, SQLAlchemy + SQLite via `.env`, Alembic configurado e com a migration inicial aplicada, tabelas `Usuario`, `Consentimento`, `RegistroDiario`, `RegistroCiclo` e `RespostaObjetivo` criadas (`MensagemChat` segue pendente, ver seção 8), testes de conexão com o banco em `backend/tests/`. Detalhes em `backend/` e na seção 7.
3. **Autenticação JWT no backend** *(concluído)* — hash de senha com `bcrypt`, JWT com `PyJWT` (`HS256`, expiração de 60 min, sem refresh token), endpoints `POST /auth/registrar`, `POST /auth/login` e `GET /auth/me`, dependência `get_current_usuario` para proteger rotas, CORS configurado em `main.py`. Detalhes na seção 7.
4. **Endpoints de domínio** *(concluída)* — CRUD de perfil, registro diário, registro de ciclo e respostas de objetivo, vinculados ao usuário autenticado. Ordem de implementação combinada: **(1) perfil → (2) registros diários → (3) registros do ciclo → (4) respostas dos objetivos**. O módulo de consentimento (não fazia parte desta ordem original, mas é um domínio próprio desde a etapa 2) também foi implementado — ver seção 7.
   - **(1) Perfil** *(concluído)* — `GET`/`PATCH /perfil`. Detalhes na seção 7.
   - **(2) Registros diários** *(concluído)* — `POST`/`GET`/`GET {id}`/`PATCH {id}`/`DELETE {id}` em `/registros-diarios`, com `index=True` em `usuario_id` e unicidade `(usuario_id, data)` já aplicados. Detalhes na seção 7.
   - **(3) Registros do ciclo** *(concluído)* — `POST`/`GET`/`GET {id}`/`PATCH {id}`/`DELETE {id}` em `/registros-ciclo`, com `index=True` em `usuario_id` e unicidade `(usuario_id, data)` já aplicados (mesmo padrão de `RegistroDiario`). Cálculo de fase e previsão de ciclo (`fase_calculada`) foram deliberadamente deixados fora do escopo. Detalhes na seção 7.
   - **(4) Respostas dos objetivos** *(concluído)* — apenas `POST`/`GET` em `/respostas-objetivo` (sem `PATCH`/`DELETE`/consulta por id nesta etapa — o `POST` funciona como criação-ou-atualização), com `index=True` em `usuario_id` e unicidade `(usuario_id, objetivo, etapa)` já aplicados. Detalhes na seção 7.
   - **Consentimento** *(concluído, fora da ordem combinada original)* — apenas `POST`/`GET /atual` em `/consentimentos` (mesmo espírito de escopo reduzido de respostas de objetivo), com `index=True` em `usuario_id` e unicidade `(usuario_id, versao_termos)` já aplicados. Detalhes na seção 7.
5. **Camada de rede no Android** — Retrofit + OkHttp, DTOs espelhando os schemas Pydantic, DataStore para o token JWT, interceptor de autenticação.
6. **Refatoração para MVVM, tela por tela** — introduzir ViewModel e Repository por fluxo (autenticação primeiro, depois perfil, registro diário, calendário), migrando o estado hoje compartilhado via `AppScreen.kt`, sem quebrar a navegação existente.
7. **Resolução do Firebase** — implementar a abordagem escolhida (ver seções 12 e 13) para `LoginScreen`/`CriarConta`, validar com testes manuais, e só então avaliar a remoção de dependências Firebase não usadas, com autorização explícita do usuário.
8. **Migração para Navigation Compose** — trocar o `when (telaAtual)` por um `NavHost`, preservando as mesmas transições de tela já mapeadas.
9. **Testes e revisão** — testes unitários de ViewModel/Repository, testes instrumentados dos fluxos críticos (cadastro/login, registro diário), tratamento de erro de rede e estados de carregamento nas telas migradas.

Cada etapa deve ser confirmada com o usuário antes de avançar para a próxima.

## 12. Comparação entre Firebase Auth e autenticação própria com JWT

Hoje `CriarConta.kt` já usa `FirebaseAuth` de fato para cadastro; `LoginScreen.kt` não usa nada de fato (ver seção 6). O plano de backend em FastAPI + JWT + SQLAlchemy exige uma tabela `Usuario` própria, para relacionar com `RegistroDiario`, `RegistroCiclo` etc., e um jeito de emitir/validar sessão — o que entra em conflito com manter o Firebase Auth como única fonte de identidade.

**Opção A — Backend valida tokens do Firebase.**
Mantém `FirebaseAuth` no Android como está; o backend usa `firebase_admin` (já presente em `requirements.txt`, hoje sem uso) para verificar o ID token do Firebase em cada requisição e resolve/cria o `Usuario` correspondente localmente.
- Vantagem: reaproveita o `CriarConta.kt` que já funciona, evita implementar hash de senha e emissão de token no backend.
- Desvantagem: mantém duas dependências de identidade (Firebase e backend), complica testes locais sem depender de um serviço externo, e ainda exige lógica própria de `Usuario` no backend de qualquer forma.

**Opção B — Migrar totalmente para autenticação própria (JWT + SQLAlchemy).**
`CriarConta`/`LoginScreen` passam a chamar endpoints do próprio backend via Retrofit; o backend guarda `senha_hash` e emite o JWT. As dependências Firebase (`firebase-auth`, `firebase-firestore`, `google-services.json`, `firebase_admin` no backend) só seriam removidas depois de validada a migração e com autorização explícita do usuário (ver seção 10).
- Vantagem: fonte única de verdade para usuário e dados, alinhada ao restante do plano (SQLAlchemy, JWT, MVVM, Retrofit), sem depender de infraestrutura externa.
- Desvantagem: exige reescrever a lógica de `CriarConta.kt`, hoje funcional, para chamar o backend em vez do Firebase.

## 13. Recomendação da estratégia de autenticação

Recomenda-se a Opção B (autenticação própria com JWT). Como o plano já prevê um backend próprio com SQLAlchemy para todas as demais entidades (perfil, registros diários, ciclo, respostas de objetivo), manter o Firebase apenas para autenticação criaria duas fontes de identidade (uid do Firebase vs. id interno de `Usuario`) sem necessidade real: o Firestore já não é usado, e o cadastro via Firebase hoje é a única peça funcional que se perderia, contra o ganho de ter um único fluxo de autenticação consistente com o restante do sistema.

Importante: esta é uma recomendação, não uma decisão executada. Nenhuma dependência do Firebase deve ser removida, nem `CriarConta.kt`/`LoginScreen.kt` alterados para deixar de usar o Firebase, sem autorização explícita do usuário no momento da implementação (ver seção 10).
