# Instruções de build e verificação — máquina da UFU

Guia para compilar, rodar e testar manualmente a integração Android ↔ FastAPI (etapa 3) na
máquina que tem Java, Android SDK e emulador/dispositivo disponíveis. Esta máquina (Windows, sem
JDK/SDK) só permitiu escrever o código e verificar o backend por HTTP real — ver
`evidencias-testes.md`, seção 10. Este documento é o roteiro da etapa 4 (teste ponta a ponta).

---

## 0. Antes de tudo: esta cópia do repositório está comprometida

Esta é a "outra cópia" identificada na remediação de segurança (Fase 6, ainda pendente): o commit
`8068aba`, com `backend/.env` rastreado e a `SECRET_KEY` antiga vazada, foi criado e enviado
**desta máquina**. Enquanto isso não for tratado, um `git pull`/`push` daqui pode reintroduzir o
vazamento no GitHub.

**Fazer isto primeiro, antes de qualquer build:**

1. Salvar fora do repositório qualquer trabalho não commitado que exista aqui (`git status` para
   checar).
2. Apagar a pasta do repositório inteira (não só o `.git`).
3. Re-clonar do zero:
   ```bash
   git clone https://github.com/Millenes2/ic-app-kotlin.git
   cd ic-app-kotlin
   git checkout develop
   git log -1 --oneline
   ```
   Deve mostrar `b91b4db` ou um commit mais recente. O histórico limpo já não contém
   `backend/.env` em nenhum commit.
4. **Não reaproveitar o `backend/.env` antigo desta máquina** — ele contém a chave comprometida.
   Ele será recriado do zero no passo 2 abaixo.

---

## 1. Pré-requisitos

| Item | Necessário | Como verificar |
|---|---|---|
| JDK | 17+ (compatível com AGP 8.7.2 / Gradle 9.3.1) | `java -version` |
| Android Studio (recomendado) ou SDK standalone | SDK Platform 36, Build-Tools, Platform-Tools, um emulador (AVD) ou dispositivo físico com depuração USB habilitada | Abrir o projeto no Android Studio deixa o Gradle configurar `local.properties` (`sdk.dir`) automaticamente |
| Python | já usado no backend (3.13 nesta base) | `python --version` |
| Conexão à internet | necessária na primeira sincronização do Gradle — este commit adiciona Retrofit, OkHttp, `kotlinx-coroutines-android`, DataStore e `lifecycle-viewmodel-compose`, nenhum baixado antes | — |

**Ponto de atrito conhecido, não introduzido por esta etapa:** `gradle/libs.versions.toml` tem o
plugin `kotlin-android` fixado em `2.2.20` (hardcoded, linha 38) enquanto `kotlin-compose` usa a
versão `2.0.21` do bloco `[versions]`. Normalmente as duas devem coincidir. Se a sincronização do
Gradle falhar por incompatibilidade do compilador Compose, este é o primeiro lugar a olhar — é
uma divergência pré-existente, anterior a esta etapa.

---

## 2. Backend

```bash
cd backend
python -m venv .venv          # se ainda não existir
# Windows:
.venv/Scripts/python.exe -m pip install --upgrade pip
.venv/Scripts/python.exe -m pip install -r requirements.txt
# Linux/macOS:
.venv/bin/python -m pip install --upgrade pip
.venv/bin/python -m pip install -r requirements.txt
```

### 2.1 Recriar `backend/.env` (não versionado, precisa existir localmente)

Copiar a estrutura de `backend/.env.example` e gerar uma `SECRET_KEY` **nova**, nunca a antiga:

```bash
.venv/Scripts/python.exe -c "import secrets; print(secrets.token_hex(32))"
```

`backend/.env`:
```
DATABASE_URL=sqlite:///./luna.db
SECRET_KEY=<colar o valor gerado acima>
ALGORITHM=HS256
ACCESS_TOKEN_EXPIRE_MINUTES=60
```

### 2.2 Criar o schema do banco — passo que faltou na verificação anterior

**Achado da etapa 3 (seção 10.1 de `evidencias-testes.md`):** rodar a aplicação contra um banco
em arquivo pela primeira vez, sem isto, resulta em `500` / `no such table: usuario` em qualquer
rota de negócio. A suíte `pytest` não expõe esse problema porque usa banco em memória. **Não
pular este passo:**

```bash
.venv/Scripts/python.exe -m alembic upgrade head
```

### 2.3 Subir o servidor acessível pelo emulador

```bash
.venv/Scripts/python.exe -m uvicorn app.main:app --host 0.0.0.0 --port 8000
```

- **Emulador Android:** já alcança `http://10.0.2.2:8000/` automaticamente — é o valor de
  `BASE_URL` já configurado em `RetrofitClient.kt`, nada a mudar.
- **Dispositivo físico via USB:** `adb reverse tcp:8000 tcp:8000` antes de abrir o app — isso
  redireciona `10.0.2.2:8000` do dispositivo para `127.0.0.1:8000` da máquina, sem precisar trocar
  o `BASE_URL` nem descobrir o IP da rede.
- **Dispositivo físico na mesma rede Wi-Fi (sem `adb reverse`):** trocar temporariamente o
  `BASE_URL` em `RetrofitClient.kt` para o IP da máquina na rede local (ex.: `http://192.168.x.x:8000/`)
  — não commitar essa troca; é só para o teste manual.

Deixar este terminal aberto durante o teste manual: o log do uvicorn mostra cada requisição
chegando do app, útil para confirmar que a chamada saiu do Android e chegou ao backend.

---

## 3. Android

```bash
git status   # confirmar que é o clone novo, limpo, em develop
```

Abrir a raiz do repositório no Android Studio e deixar o Gradle sincronizar (baixa as
dependências novas listadas no pré-requisito). Alternativa por linha de comando, como checagem
rápida de que compila antes de abrir a IDE:

```bash
./gradlew assembleDebug
```

Rodar em um emulador (qualquer AVD com Google APIs, não precisa Play Store) ou em um dispositivo
físico com depuração USB habilitada e autorizada.

---

## 4. Teste manual — o próprio teste ponta a ponta (etapa 4)

Com o backend do passo 2.3 rodando e o app instalado:

1. Abrir o app → avançar pelo onboarding até a Home → tocar em "Entrar" (ou o caminho equivalente
   até `LoginScreen`).
2. Em `LoginScreen`, tocar em "Criar conta" → preencher nome/e-mail/senha novos → tocar em "Criar
   conta". Esperado: indicador de carregamento no botão, depois navegação para a Home (login
   automático encadeado após o cadastro).
3. **Confirmar no terminal do uvicorn** que apareceram as linhas `POST /auth/registrar` (201) e
   `POST /auth/login` (200) — evidência de que a chamada saiu do Android e chegou ao backend.
4. Tentar criar conta de novo com o mesmo e-mail → esperado: mensagem de erro "Já existe uma conta
   com este e-mail" (409), exibida no lugar do campo de erro já existente na tela.
5. Voltar para `LoginScreen` e tentar logar com a senha errada → esperado: mensagem "E-mail ou
   senha inválidos" (401).
6. Logar com a senha certa → esperado: navegação para a Home.

### O que este teste NÃO cobre (limitação conhecida, não é bug a corrigir agora)

Não há verificação de sessão ao abrir o app: `AppScreen.kt` não foi alterado nesta etapa, então
mesmo com o token salvo no `SessaoDataStore`, reabrir o app sempre volta para o fluxo de
onboarding — não há "continuar logada automaticamente" nem tela de logout. Isso é esperado pelo
escopo combinado ("integração mínima"), não uma falha a investigar durante este teste.

### Se algo falhar

- **Erro de rede imediato ao tocar no botão:** confirmar que o uvicorn está de fato escutando em
  `0.0.0.0:8000` (não `127.0.0.1`) e que `adb reverse`/IP da rede foi configurado se for
  dispositivo físico.
- **`CLEARTEXT communication not permitted`:** revisar se `network_security_config.xml` foi
  empacotado (checar `android:networkSecurityConfig` em `AndroidManifest.xml` e o arquivo em
  `res/xml/`).
- **Logs do lado Android:** filtrar o Logcat pela tag `OkHttp` — o `HttpLoggingInterceptor`
  configurado em `RetrofitClient.kt` imprime corpo de requisição/resposta de cada chamada.

---

## 5. Depois do teste

Atualizar `docs/artigo-latinscience/evidencias-testes.md`, seção 10.2, com o resultado real:
capturas de tela das telas de sucesso/erro, trecho do log do uvicorn confirmando as requisições, e
qualquer divergência encontrada em relação ao que foi revisado por leitura nesta etapa. Essa
atualização é o que transforma a seção 10.2 de "não verificado" em evidência real — é a etapa 4
do roteiro do artigo.
