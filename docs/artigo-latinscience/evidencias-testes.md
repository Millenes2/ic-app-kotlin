# Evidências de execução da suíte de testes — Backend Luna

Documento de evidências para o artigo da **Latin.Science 2026**.
Registra a execução real da suíte de testes automatizados do backend FastAPI do projeto Luna.

> **Este documento contém duas execuções.**
> As seções 1 a 7 registram a **execução inicial (2026-08-05)**, feita com um contorno manual
> porque `backend/requirements.txt` não era instalável em Windows. Essa evidência é preservada
> intacta por documentar o problema no estado em que foi encontrado.
> A **seção 8** registra a **execução após a correção de compatibilidade (2026-08-06)**, já com
> instalação normal, sem contorno.

- **Data da execução:** 2026-08-05
- **Repositório:** `C:\Users\Mille\ic-app-kotlin`
- **Branch:** `develop`
- **Commit (HEAD):** `8068aba` — "sem janatar"
- **Diretório de execução:** `backend/`

Nenhum código-fonte do Android ou do backend foi alterado para produzir estes resultados.
Nenhuma operação de Git foi realizada. Os arquivos `.env` e `google-services.json` não foram tocados.

---

## 1. Ambiente

| Item | Valor |
|---|---|
| Sistema operacional | Windows 10 (`Windows-10-10.0.19045-SP0`) |
| Plataforma pytest | `win32` |
| Python | **3.13.3** |
| pytest | **9.1.1** |
| pluggy | 1.6.0 |
| Plugins pytest carregados | `anyio-4.14.2` |
| rootdir | `C:\Users\Mille\ic-app-kotlin\backend` |
| Arquivo de configuração pytest | **nenhum** (não existe `pytest.ini`, `pyproject.toml`, `setup.cfg` nem `tox.ini`) |

### Versões das dependências principais

| Pacote | Versão |
|---|---|
| fastapi | 0.139.2 |
| starlette | 1.3.1 |
| sqlalchemy | 2.0.51 |
| pydantic | 2.13.4 |
| alembic | 1.18.5 |
| PyJWT | 2.13.0 |
| bcrypt | 5.0.0 |
| httpx | 0.28.1 |

---

## 2. Comandos utilizados

Executados a partir de `C:\Users\Mille\ic-app-kotlin\backend`.

### 2.1 Criação do ambiente virtual

```bash
python -m venv .venv
.venv/Scripts/python.exe --version     # Python 3.13.3
```

### 2.2 Instalação das dependências

```bash
.venv/Scripts/python.exe -m pip install --upgrade pip     # pip 25.0.1 -> 26.2.1
.venv/Scripts/python.exe -m pip install -r requirements.txt
```

> ⚠️ **Este comando falhou.** Ver a seção 4 ("Desvio de procedimento") para a causa e o
> contorno aplicado. O comando efetivamente utilizado foi:

```bash
# arquivo filtrado gerado FORA do repositório (diretório temporário da sessão);
# backend/requirements.txt NÃO foi modificado
grep -v -i '^uvloop' requirements.txt > <temp>/requirements-sem-uvloop.txt
.venv/Scripts/python.exe -m pip install -r <temp>/requirements-sem-uvloop.txt
```

Resultado: **61 dos 62 pacotes** de `backend/requirements.txt` instalados com sucesso,
todos nas versões exatas fixadas no arquivo.

### 2.3 Execução da suíte

```bash
.venv/Scripts/python.exe -m pytest --version    # pytest 9.1.1
.venv/Scripts/python.exe -m pytest -v
```

### 2.4 Verificação da coleta

```bash
.venv/Scripts/python.exe -m pytest --collect-only -q
```

---

## 3. Resultados

### 3.1 Linha de resumo (saída literal do pytest)

```
============================= test session starts =============================
platform win32 -- Python 3.13.3, pytest-9.1.1, pluggy-1.6.0
rootdir: C:\Users\Mille\ic-app-kotlin\backend
plugins: anyio-4.14.2
collected 146 items

...

================= 146 passed, 1 warning in 114.23s (0:01:54) ==================
```

### 3.2 Números consolidados

| Métrica | Valor |
|---|---|
| **Testes coletados** | **146** |
| **Aprovados (passed)** | **146** |
| **Falhas (failed)** | **0** |
| **Erros (errors)** | **0** |
| **Ignorados (skipped)** | **0** |
| xfail / xpass | 0 |
| Avisos (warnings) | 1 |
| **Tempo total de execução** | **114,23 s (1 min 54 s)** |
| Tempo de coleta | 0,10 s |
| **Taxa de aprovação** | **100,0 %** |

### 3.3 Distribuição por arquivo de teste

| Arquivo | Testes | Aprovados | Domínio coberto |
|---|---:|---:|---|
| `tests/test_registros_ciclo.py` | 38 | 38 | CRUD de registros do ciclo, unicidade por data, filtros, isolamento |
| `tests/test_registros_diarios.py` | 37 | 37 | CRUD de registros diários, invariante de campo preenchido, conflitos |
| `tests/test_respostas_objetivo.py` | 20 | 20 | Upsert de respostas de objetivo, filtros, isolamento |
| `tests/test_consentimentos.py` | 17 | 17 | Registro e reafirmação de consentimento, `GET /atual` |
| `tests/test_perfil.py` | 13 | 13 | Leitura e atualização parcial de perfil, validações |
| `tests/test_openapi.py` | 10 | 10 | Contrato OpenAPI, tags, segurança, não-vazamento de `senha_hash` |
| `tests/test_auth.py` | 8 | 8 | Registro, login, token inválido/expirado, `GET /me` |
| `tests/test_health.py` | 3 | 3 | Rotas de sistema e conexão com o banco |
| **Total** | **146** | **146** | |

Todos os 146 são funções de teste em nível de módulo — não há classes de teste na suíte.

### 3.4 Aviso emitido (1)

```
StarletteDeprecationWarning: Using `httpx` with `starlette.testclient` is deprecated;
install `httpx2` instead.
  .venv\Lib\site-packages\fastapi\testclient.py:1
```

Aviso de descontinuação originado na própria biblioteca FastAPI/Starlette, **não no código
do projeto**. Não afeta o resultado dos testes. Registrado aqui por completude.

---

## 4. Desvio de procedimento: falha na instalação de `uvloop`

> **Status:** problema **corrigido em 2026-08-06**. Esta seção é preservada como registro do
> estado original. A correção e sua verificação estão na **seção 8**.

### O que aconteceu

O comando `pip install -r requirements.txt` **falhou** com:

```
RuntimeError: uvloop does not support Windows at the moment
ERROR: Failed to build 'uvloop' when getting requirements to build wheel
```

### Causa

`backend/requirements.txt` fixa `uvloop==0.22.1`. O pacote `uvloop` **não possui suporte a
Windows** e não distribui wheel para a plataforma, falhando na etapa de build. O pacote entrou
no arquivo porque este foi gerado por `pip freeze` em um ambiente que instalou
`uvicorn[standard]`, cuja lista de extras inclui `uvloop`.

Consequência prática: **`backend/requirements.txt` não é instalável em Windows no estado atual**,
que é a plataforma de desenvolvimento deste projeto.

### Por que o contorno é seguro

`uvloop` é um *event loop* alternativo, de uso opcional pelo Uvicorn em produção em sistemas
Unix. Ele **não é utilizado pela suíte de testes**: os testes exercitam a aplicação através do
`TestClient` do FastAPI (baseado em `httpx`), sem subir um servidor ASGI. Nenhum módulo do
projeto importa `uvloop` — a única ocorrência do nome em todo o repositório é a linha de pin
em `backend/requirements.txt` (e a cópia em `requirements.txt` na raiz).

Portanto, a ausência de `uvloop` **não influencia os resultados registrados acima**.

### O que NÃO foi feito

`backend/requirements.txt` **não foi modificado**. O arquivo filtrado foi gravado no diretório
temporário da sessão, fora do repositório. Conforme instrução, nenhuma correção foi aplicada
sem autorização prévia.

### Correção sugerida (à época, pendente de autorização — aplicada em 2026-08-06)

Substituir o pin incondicional por um marcador de ambiente, preservando o comportamento em
Linux (onde o backend rodará em produção) e tornando o arquivo instalável em Windows:

```
uvloop==0.22.1 ; sys_platform != "win32"
```

---

## 5. Verificações de integridade do repositório

Confirmações executadas após os testes:

| Verificação | Resultado |
|---|---|
| `git status --porcelain -uall` (antes de criar este documento) | **vazio** — árvore de trabalho limpa |
| Código-fonte Android alterado | não |
| Código-fonte do backend alterado | não |
| `backend/requirements.txt` alterado | não |
| `backend/.env` alterado | não |
| `app/google-services.json` alterado | não |
| Commits ou push realizados | não |
| `backend/.venv` versionado | não — ignorado por `backend/.gitignore:1` |
| `backend/.pytest_cache` versionado | não — ignorado por `backend/.gitignore:6` |
| `backend/luna.db` criado | **não** — a suíte usa SQLite em memória (`conftest.py`) |

Os testes não tocam em banco em disco: `tests/conftest.py` cria um engine
`sqlite:///:memory:` por teste (fixture `db_session`) e um engine em memória com `StaticPool`
com override de `get_db` (fixture `client`), com `create_all`/`drop_all` a cada teste.

---

## 6. O que estes números comprovam — e o que não comprovam

### Comprovam

- A suíte de **146 testes automatizados do backend executa integralmente e passa (100 %)**
  em Python 3.13.3 / pytest 9.1.1, em 114,23 s.
- Cobertura funcional dos **6 módulos de domínio** (autenticação, perfil, registros diários,
  registros do ciclo, respostas de objetivo, consentimentos) mais o contrato OpenAPI.
- Verificação automatizada de propriedades relevantes de segurança e integridade:
  isolamento entre usuárias, retorno 404 (em vez de 403) para recurso de outra conta,
  rejeição de `usuario_id` enviado pelo cliente, conflitos de unicidade (409) e
  ausência de `senha_hash` nos schemas expostos.

### Não comprovam

- **Cobertura de código.** `pytest-cov` não está em `backend/requirements.txt`; nenhuma
  métrica de cobertura foi medida. Os 146 testes são contagem de casos, não percentual de
  linhas exercitadas.
- **Desempenho.** Os 114,23 s são o tempo da suíte, não latência de endpoint. Nenhuma medição
  de latência ou throughput foi feita.
- **Integração com o aplicativo Android.** Inexistente: nenhuma requisição HTTP parte do app.
- **Uso real.** Zero usuárias, zero dados de produção.
- **Estabilidade em outras plataformas.** Execução verificada apenas em Windows 10 / Python 3.13.3.

---

## 7. Reprodução (procedimento vigente à época — anterior à correção)

> Obsoleto desde 2026-08-06. O procedimento atual, sem contorno, está na **seção 8.7**.

```bash
cd backend
python -m venv .venv
.venv/Scripts/python.exe -m pip install --upgrade pip

# Em Linux/macOS:
.venv/bin/python -m pip install -r requirements.txt

# Em Windows, enquanto o pin de uvloop não for corrigido:
grep -v -i '^uvloop' requirements.txt > /tmp/req-sem-uvloop.txt
.venv/Scripts/python.exe -m pip install -r /tmp/req-sem-uvloop.txt

.venv/Scripts/python.exe -m pytest -v
```

Saída esperada: `146 passed`.

---

# 8. Execução após a correção de compatibilidade

- **Data:** 2026-08-06
- **Branch:** `develop` · **Commit (HEAD):** `8068aba` (inalterado — nenhum commit foi feito)
- **Objetivo:** eliminar o contorno manual da seção 4 e comprovar que `backend/requirements.txt`
  instala normalmente em Windows, mantendo os 146 testes aprovados.

## 8.1 Alteração aplicada

Dois arquivos, **uma linha em cada**, alterando exclusivamente o pin do `uvloop`:

| Arquivo | Linha | Antes | Depois |
|---|---:|---|---|
| `backend/requirements.txt` | 60 | `uvloop==0.22.1` | `uvloop==0.22.1 ; sys_platform != "win32"` |
| `requirements.txt` (raiz) | 17 | `uvloop==0.22.1` | `uvloop==0.22.1 ; sys_platform != "win32"` |

Nenhuma outra dependência foi adicionada, removida ou teve sua versão alterada. Nenhum
arquivo Kotlin, nenhum código Python do backend, `backend/.env`, `app/google-services.json`
ou `AndroidManifest.xml` foi tocado.

O arquivo da raiz recebeu a mesma alteração para manter os dois arquivos consistentes — ele é
um subconjunto estrito do arquivo do backend e continha o mesmo pin problemático.

## 8.2 Justificativa técnica

`uvloop` é uma implementação alternativa de *event loop* baseada em libuv, usada opcionalmente
pelo Uvicorn para aumentar throughput em produção. O pacote **não oferece suporte a Windows** e
não publica wheel para a plataforma: seu `setup.py` aborta com
`RuntimeError: uvloop does not support Windows at the moment`, o que fazia o `pip install`
inteiro falhar antes de instalar qualquer pacote.

O marcador de ambiente PEP 508 `; sys_platform != "win32"` instrui o pip a **avaliar a condição
por plataforma**: o pacote continua sendo instalado normalmente em Linux e macOS — onde o
backend rodará em produção e onde o ganho de desempenho é real — e é simplesmente ignorado em
Windows, plataforma de desenvolvimento deste projeto.

A correção é segura porque `uvloop` não é dependência funcional do código nem da suíte de
testes: nenhum módulo do projeto o importa (a única ocorrência do nome no repositório é a
própria linha de pin), e os testes exercitam a aplicação via `TestClient`/`httpx`, sem subir um
servidor ASGI. A alteração **não muda a versão instalada em nenhuma plataforma** — apenas torna
a instalação condicional onde ela era impossível.

## 8.3 Confirmação da instalação normal em Windows

O ambiente virtual anterior foi **excluído integralmente** (`rm -rf backend/.venv`) e recriado
do zero, para garantir que o resultado não dependesse de estado residual.

```bash
cd backend
python -m venv .venv
.venv/Scripts/python.exe -m pip install --upgrade pip
.venv/Scripts/python.exe -m pip install -r requirements.txt
```

**Resultado: instalação concluída com sucesso, código de saída `0`, sem erros e sem contorno.**
Nenhum arquivo filtrado foi gerado; o comando leu diretamente `backend/requirements.txt`.

## 8.4 Pacotes instalados

| Métrica | Valor |
|---|---:|
| Pins declarados em `backend/requirements.txt` | 62 |
| Pins instalados | **61** |
| Pin corretamente ignorado pelo marcador (`uvloop`, Windows) | 1 |
| Dependência transitiva adicional (`colorama`, exclusiva de Windows) | 1 |
| **Total de pacotes no ambiente virtual** | **62** |

Verificação de que o marcador foi respeitado:

```
$ .venv/Scripts/python.exe -m pip show uvloop
WARNING: Package(s) not found: uvloop
```

Comparação conjunto a conjunto entre os pins declarados e os pacotes instalados:
o único item declarado e não instalado é `uvloop`; o único item instalado e não declarado é
`colorama` (dependência transitiva de `click`/`pytest` em Windows). Todos os demais 61 pacotes
foram instalados nas versões exatas fixadas.

## 8.5 Resultados da nova execução

```
============================= test session starts =============================
platform win32 -- Python 3.13.3, pytest-9.1.1, pluggy-1.6.0
rootdir: C:\Users\Mille\ic-app-kotlin\backend
plugins: anyio-4.14.2
collected 146 items

...

================= 146 passed, 1 warning in 113.23s (0:01:53) ==================
```

| Métrica | Valor |
|---|---|
| **Testes coletados** | **146** |
| **Aprovados (passed)** | **146** |
| **Falhas (failed)** | **0** |
| **Erros (errors)** | **0** |
| **Ignorados (skipped)** | **0** |
| xfail / xpass | 0 |
| **Avisos (warnings)** | **1** |
| **Tempo total de execução** | **113,23 s (1 min 53 s)** |
| Tempo de coleta (`--collect-only -q`) | 0,10 s — `146 tests collected` |
| **Taxa de aprovação** | **100,0 %** |

### Ambiente da nova execução

| Item | Valor |
|---|---|
| Python | **3.13.3** |
| pip | **26.2.1** (atualizado a partir de 25.0.1) |
| pytest | **9.1.1** |
| pluggy | 1.6.0 |
| Plugins | `anyio-4.14.2` |
| Plataforma | `win32` — Windows 10 (`Windows-10-10.0.19045-SP0`) |

### Distribuição por arquivo (inalterada)

| Arquivo | Testes | Aprovados |
|---|---:|---:|
| `tests/test_registros_ciclo.py` | 38 | 38 |
| `tests/test_registros_diarios.py` | 37 | 37 |
| `tests/test_respostas_objetivo.py` | 20 | 20 |
| `tests/test_consentimentos.py` | 17 | 17 |
| `tests/test_perfil.py` | 13 | 13 |
| `tests/test_openapi.py` | 10 | 10 |
| `tests/test_auth.py` | 8 | 8 |
| `tests/test_health.py` | 3 | 3 |
| **Total** | **146** | **146** |

### Aviso emitido (1 — idêntico ao da execução inicial)

```
StarletteDeprecationWarning: Using `httpx` with `starlette.testclient` is deprecated;
install `httpx2` instead.
  .venv\Lib\site-packages\fastapi\testclient.py:1
```

Originado na biblioteca FastAPI/Starlette, não no código do projeto. Não afeta o resultado.

## 8.6 Comparação entre as duas execuções

| Métrica | Execução inicial (2026-08-05) | Após a correção (2026-08-06) | Variação |
|---|---|---|---|
| **Instalação via `pip install -r requirements.txt`** | ❌ **falhou** (`uvloop`) | ✅ **sucesso**, código 0 | **corrigido** |
| Contorno manual necessário | sim (requirements filtrado) | **não** | eliminado |
| Pins instalados | 61 de 62 | 61 de 62 | igual |
| Total de pacotes no venv | 62 | 62 | igual |
| Testes coletados | 146 | 146 | 0 |
| Aprovados | 146 | 146 | 0 |
| Falhas | 0 | 0 | 0 |
| Erros | 0 | 0 | 0 |
| Ignorados | 0 | 0 | 0 |
| Avisos | 1 | 1 | 0 |
| Taxa de aprovação | 100,0 % | 100,0 % | 0 |
| **Tempo total** | 114,23 s | **113,23 s** | **−1,00 s (−0,88 %)** |
| Python | 3.13.3 | 3.13.3 | igual |
| pytest | 9.1.1 | 9.1.1 | igual |
| pip | 26.2.1 | 26.2.1 | igual |

**Leitura do resultado:** a correção resolveu integralmente a falha de instalação **sem
qualquer efeito sobre o comportamento da suíte** — mesmos 146 testes, mesmo resultado, mesmo
aviso. A diferença de 1,00 s no tempo total (0,88 %) é ruído de medição entre execuções, não
efeito da alteração: o `uvloop` não estava instalado em nenhuma das duas execuções.

O valor da correção não é de desempenho, e sim de **reprodutibilidade**: o procedimento
documentado na seção 8.7 agora funciona em Windows sem intervenção manual, o que é requisito
para que terceiros reproduzam os resultados relatados no artigo.

## 8.7 Reprodução (procedimento atual)

Idêntico em Windows, Linux e macOS:

```bash
cd backend
python -m venv .venv

# Windows
.venv/Scripts/python.exe -m pip install --upgrade pip
.venv/Scripts/python.exe -m pip install -r requirements.txt
.venv/Scripts/python.exe -m pytest -v

# Linux / macOS
.venv/bin/python -m pip install --upgrade pip
.venv/bin/python -m pip install -r requirements.txt
.venv/bin/python -m pytest -v
```

Saída esperada: `146 passed`.
Em Linux/macOS o `uvloop` é instalado normalmente; em Windows é ignorado pelo marcador.

## 8.8 Verificações de integridade após a correção

| Verificação | Resultado |
|---|---|
| Arquivos alterados | **apenas 2**: `backend/requirements.txt` e `requirements.txt` (1 linha cada) |
| Arquivos Kotlin alterados | não |
| Código Python do backend alterado | não |
| `backend/.env` alterado | não |
| `app/google-services.json` alterado | não |
| `AndroidManifest.xml` alterado | não |
| Outras dependências alteradas | não |
| Commits ou push realizados | **não** |
| `backend/luna.db` criado | não — suíte roda em SQLite em memória |
| `backend/.venv` / `.pytest_cache` versionados | não — ignorados por `backend/.gitignore` |

---

# 9. Cobertura de código (`pytest-cov`)

- **Data:** 2026-08-09
- **Branch:** `develop` · **Commit base:** `8baeff7`
- **Objetivo:** medir a métrica que a seção 6 registrava como ausente ("`pytest-cov` não está em
  `backend/requirements.txt`; nenhuma métrica de cobertura foi medida").

## 9.1 Instalação

`pytest-cov` não estava instalado nem declarado. Instalado no `.venv` já existente:

```bash
.venv/Scripts/python.exe -m pip install pytest-cov
```

```
Successfully installed coverage-7.15.4 pytest-cov-7.1.0
```

## 9.2 Comando executado

```bash
.venv/Scripts/python.exe -m pytest --cov=app --cov-report=term-missing -v
```

## 9.3 Resultado consolidado

```
=============================== tests coverage ================================
_______________ coverage: platform win32, python 3.13.3-final-0 _______________
TOTAL                                 700     43    94%
================ 146 passed, 124 warnings in 117.42s (0:01:57) ================
```

| Métrica | Valor |
|---|---:|
| Testes aprovados | **146** (inalterado) |
| Linhas de código (`app/`) | 700 |
| Linhas não cobertas | 43 |
| **Cobertura total** | **94 %** |

## 9.4 Cobertura por módulo

| Arquivo | Statements | Miss | Cobertura | Linhas não cobertas |
|---|---:|---:|---:|---|
| `app/db/session.py` | 11 | 4 | 64 % | 15–19 |
| `app/dependencies.py` | 21 | 2 | 90 % | 34, 40 |
| `app/main.py` | 18 | 1 | 94 % | 86 |
| `app/routers/auth.py` | 35 | 3 | 91 % | 46–52 |
| `app/routers/consentimentos.py` | 42 | 8 | 81 % | 74–92 |
| `app/routers/registros_ciclo.py` | 71 | 7 | 90 % | 56–62 |
| `app/routers/registros_diarios.py` | 77 | 7 | 91 % | 56–62 |
| `app/routers/respostas_objetivo.py` | 43 | 8 | 81 % | 67–85 |
| `app/schemas/registro_ciclo.py` | 61 | 2 | 97 % | 29, 80 |
| `app/schemas/usuario.py` | 61 | 1 | 98 % | 114 |
| Demais 20 arquivos (models, schemas restantes, `core/`, `db/base.py`) | — | 0 | **100 %** | — |

## 9.5 Análise das lacunas

As 43 linhas não cobertas se concentram em três categorias, todas explicáveis pela natureza do
código, não por ausência de teste correspondente:

**(a) `get_db()` real nunca é executado (`app/db/session.py`, linhas 15–19).**
`backend/tests/conftest.py` substitui essa dependência inteira via `dependency_overrides` por uma
versão que usa SQLite em memória. O gerador de produção (que abre uma sessão contra o banco
configurado em `DATABASE_URL` e a fecha no `finally`) nunca chega a rodar sob teste — é o
comportamento esperado de uma suíte que isola o banco real.

**(b) Cinco tratamentos de corrida de `IntegrityError`, idênticos em padrão** — `auth.py:46-52`,
`consentimentos.py:74-92`, `respostas_objetivo.py:67-85`, `registros_ciclo.py:56-62`,
`registros_diarios.py:56-62`. Cada um trata o caso de duas requisições concorrentes tentando criar
o mesmo registro (mesmo e-mail, mesma data, mesma combinação de chaves de negócio): a requisição
que perde a corrida do `commit()` cai nesse bloco, faz `rollback()` e responde 409 em vez de deixar
propagar um 500. Um `TestClient` síncrono processa uma requisição de cada vez, então não há como
duas baterem no `commit()` simultaneamente nesse tipo de teste — cobrir esse ramo exigiria
infraestrutura de concorrência real (threads ou processos disparando requisições ao mesmo tempo),
fora do escopo desta suíte.

**(c) Ramos de borda de baixo impacto** — `dependencies.py:34,40` (token com payload sem `sub`, ou
usuário apagado entre a emissão do token e o uso), `main.py:86` (o corpo de `GET /` nunca é
chamado por um teste HTTP real — só `GET /health` tem teste dedicado), e duas linhas de validação
em `schemas/registro_ciclo.py` e `schemas/usuario.py`.

**Leitura:** a suíte cobre integralmente a lógica de negócio síncrona — modelos, schemas,
validações e o fluxo de erro tratável em uma única requisição. A lacuna de 6 % é concentrada,
explicada linha a linha, e não indica ausência de teste para comportamento alcançável em produção
por uma requisição isolada.

## 9.6 Aumento de avisos (1 → 124)

A execução com `--cov` reportou 124 avisos (`ResourceWarning: unclosed database in <sqlite3.Connection ...>`),
contra o único aviso do Starlette registrado nas seções 3.4 e 8.5. A causa não é o `pytest-cov` em
si: o plugin aciona `gc.collect()` durante a coleta de cobertura, o que expõe conexões SQLite que já
não eram fechadas explicitamente antes (visíveis agora, não introduzidas agora). Não afeta o
resultado — os 146 testes continuam passando — e fica registrado aqui como um achado a
investigar separadamente (fechamento explícito de conexão nas fixtures de teste), não como
regressão desta medição.

## 9.7 Verificações de integridade

| Verificação | Resultado |
|---|---|
| Arquivos alterados | `backend/requirements.txt` (2 linhas: `coverage`, `pytest-cov`) |
| `requirements.txt` da raiz alterado | **não** — não contém `pytest` nem nenhuma dependência de teste; adicionar `pytest-cov` isoladamente ali criaria inconsistência nova |
| Código-fonte do backend alterado | não |
| Resultado dos 146 testes | inalterado (146 passed) |
| Commits ou push realizados | não |

---

# 10. Integração mínima Android ↔ FastAPI

- **Data:** 2026-08-09/10
- **Branch:** `develop` · **Commit base:** `3cd0f11`
- **Objetivo:** conectar `LoginScreen` e `CriarConta` (Android) a `POST /auth/login` e
  `POST /auth/registrar` (backend), substituindo em `CriarConta` a chamada ao `FirebaseAuth` pela
  chamada ao backend próprio — decisão de arquitetura registrada nas seções 12–13 do `CLAUDE.md`,
  autorizada explicitamente nesta etapa.

> ⚠️ **Restrição de ambiente, verificada por investigação direta nesta máquina:** não há Java,
> Android SDK, Android Studio nem emulador instalados (`java`, `ANDROID_HOME`,
> `Program Files\Android Studio` — todos ausentes). Como consequência, **esta seção documenta
> duas evidências de natureza diferente**, e elas não devem ser confundidas: a verificação do
> backend (10.1) é execução real, com resultado observado; o código Android (10.2) é código
> escrito e revisado por leitura, **não compilado nem executado nesta máquina**. A verificação
> real do app — `./gradlew assembleDebug`, instalação no emulador/dispositivo, teste manual —
> está pendente e será feita na máquina da UFU.

## 10.1 Backend: verificação ponta a ponta com HTTP real (executado e observado)

Diferente da suíte `pytest` (que usa `TestClient` in-process, sem sockets de rede) e diferente da
seção 8.3 (que testa só a instalação), este teste sobe o `uvicorn` de verdade e faz requisições
HTTP reais — o mesmo caminho que o app Android vai exercitar na UFU.

### Achado antes da verificação: banco sem schema

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000
```
respondeu `200` em `GET /` e `GET /health`, mas a primeira chamada real de negócio
(`POST /auth/registrar`) devolveu **500**, com o traceback terminando em:
```
sqlite3.OperationalError: no such table: usuario
```

**Causa:** a suíte `pytest` nunca precisa de migrations — `tests/conftest.py` cria as tabelas via
`Base.metadata.create_all()` num SQLite em memória a cada teste. Rodar a aplicação "de verdade"
contra um `luna.db` em arquivo, pela primeira vez, não passa por esse atalho: o schema só existe
depois de `alembic upgrade head`. Isso nunca apareceu nos 146 testes nem nas seções 1–9 porque
nenhuma delas sobe o servidor de fato.

**Correção:** rodar as migrations pendentes (procedimento já documentado no `CLAUDE.md`, seção 7 —
não é uma alteração de código, só um passo de setup que faltou):
```bash
.venv/Scripts/python.exe -m alembic upgrade head
```
Criou as 5 tabelas de negócio + `alembic_version`. Nenhum arquivo de código foi alterado para essa
correção.

**Leitura para o artigo:** este é um achado genuíno da etapa de integração — uma lacuna real entre
"os testes passam" e "a aplicação roda", que só aparece ao operar o sistema fora do arnês de
teste. Vale registrar como achado, não esconder.

### Sequência executada (após as migrations), contra `http://127.0.0.1:8000`

| # | Requisição | Esperado | Obtido |
|---|---|---|---|
| 1 | `POST /auth/registrar` (e-mail novo) | 201 | **201**, `UsuarioOut` completo, `senha_hash` ausente |
| 2 | `POST /auth/registrar` (mesmo e-mail) | 409 | **409**, `{"detail":"Já existe uma conta com este e-mail"}` |
| 3 | `POST /auth/login` (senha certa) | 200 | **200**, `access_token` presente, `token_type":"bearer"` |
| 4 | `POST /auth/login` (senha errada) | 401 | **401**, `{"detail":"E-mail ou senha inválidos"}` |
| 5 | `GET /auth/me` (com token) | 200 | **200**, `email` correspondente |
| 6 | `GET /auth/me` (sem header) | 401 | **401**, `{"detail":"Not authenticated"}` |
| 7 | `GET /` | 200 | **200**, `{"mensagem":"Bem-vindo à API do Luna!"}` |
| 8 | `GET /health` | 200 | **200**, `{"status":"ok"}` |

Todos os 8 resultados bateram com o contrato documentado em `backend/app/routers/auth.py` e com
os testes já existentes em `backend/tests/test_auth.py` — nenhuma divergência de comportamento
entre `TestClient` e HTTP real, fora do achado do schema acima.

### Limpeza após o teste

Servidor encerrado; `backend/luna.db` (gerado só para este teste manual) removido — já é ignorado
por `backend/.gitignore:4` (`*.db`), não haveria risco de commit acidental mesmo sem a remoção.
`git status` confirmado limpo antes de seguir.

## 10.2 Android: código escrito, revisado por leitura — não compilado nesta máquina

Escopo: `LoginScreen` e `CriarConta` passam a chamar o backend via Retrofit, com um `ViewModel`
único de autenticação e o token salvo em DataStore. Nenhuma outra tela foi tocada; `AppScreen.kt`
não mudou (os branches `"login"`/`"criar"` continuam com a mesma assinatura de callback).

### Arquivos novos
- `app/src/main/java/com/example/ic_app/data/remote/dto/AuthDtos.kt` — DTOs espelhando
  `UsuarioCreate`, `LoginRequest`, `Token`, `UsuarioOut` do backend.
- `app/src/main/java/com/example/ic_app/data/remote/AuthApi.kt` — interface Retrofit
  (`POST auth/registrar`, `POST auth/login`).
- `app/src/main/java/com/example/ic_app/data/remote/RetrofitClient.kt` — cliente HTTP,
  `BASE_URL = "http://10.0.2.2:8000/"` (endereço do host visto de dentro do emulador Android).
- `app/src/main/java/com/example/ic_app/data/local/SessaoDataStore.kt` — DataStore de
  preferências para o token de sessão (único estado persistente novo do app).
- `app/src/main/java/com/example/ic_app/viewmodel/AuthViewModel.kt` — `AuthUiState`
  (`Idle`/`Carregando`/`Sucesso`/`Erro`), funções `login`/`registrar`; `registrar` encadeia um
  login automático (o endpoint de registro não devolve token).
- `app/src/main/res/xml/network_security_config.xml` — libera cleartext (HTTP) apenas para
  `10.0.2.2`/`localhost`/`127.0.0.1`, não globalmente (`targetSdk=36` bloqueia HTTP por padrão).

### Arquivos alterados
- `gradle/libs.versions.toml`, `app/build.gradle.kts` — dependências novas: Retrofit +
  converter-gson, OkHttp logging interceptor, `kotlinx-coroutines-android`,
  `datastore-preferences`, `lifecycle-viewmodel-compose`. Nenhuma dependência existente foi
  removida ou teve versão alterada; Firebase (`firebase-auth`, `firebase-firestore`) permanece
  declarado.
- `app/src/main/AndroidManifest.xml` — `<uses-permission android:name="android.permission.INTERNET" />`
  (ausente antes) e `android:networkSecurityConfig` apontando para o XML acima.
- `app/src/main/java/com/example/ic_app/auth/LoginScreen.kt` — o botão "Entrar" agora chama
  `viewModel.login(email, senha)` em vez de avançar sem verificar nada; adicionado indicador de
  carregamento e desabilitação do botão durante a chamada. Validação de campo vazio já existente
  preservada sem alteração.
- `app/src/main/java/com/example/ic_app/auth/CriarConta.kt` — removida a única referência a
  `FirebaseAuth` do arquivo (`import` e `FirebaseAuth.getInstance()`); o botão "Criar conta" agora
  chama `viewModel.registrar(email, senha, nome)`. As 6 validações client-side existentes
  (nome/e-mail/senha vazios, confirmação de senha, tamanho mínimo) foram preservadas sem alteração.
  **A dependência Firebase em si não foi removida** — só a chamada deste arquivo mudou de alvo.

### O que este código NÃO prova

- Não prova que o app compila (`./gradlew assembleDebug` nunca rodou nesta sessão).
- Não prova que a tela renderiza, que o clique funciona, ou que o token é de fato salvo e
  reaproveitado — nada disso pode ser observado sem emulador/dispositivo.
- Não prova que `10.0.2.2` alcança o backend de um emulador real (a seção 10.1 prova que o
  backend responde por HTTP; não prova que o Android o alcança).
- Revisão feita foi: leitura linha a linha das quatro telas/camadas afetadas, verificação manual
  de que os nomes de campo dos DTOs batem com os schemas Pydantic (seção 2 desta etapa, contrato
  em `backend/app/schemas/`), e verificação de que a assinatura de `LoginScreen`/`CriarConta`
  manteve compatibilidade com as chamadas existentes em `AppScreen.kt`.

**Próximo passo, na UFU:** `./gradlew assembleDebug`, resolver eventuais erros de compilação,
subir o backend na mesma rede do emulador (ou `adb reverse tcp:8000 tcp:8000` para dispositivo
físico), e repetir manualmente a sequência de login/cadastro descrita em 10.1 através da
interface real — essa é a etapa 4 do roteiro do artigo (teste ponta a ponta).

## 10.3 Verificações de integridade

| Verificação | Resultado |
|---|---|
| Backend: 8 requisições HTTP reais | executadas e observadas nesta máquina (seção 10.1) |
| Android: compilado/executado nesta máquina | **não** — sem toolchain disponível; código pendente de verificação na UFU |
| `AppScreen.kt` alterado | não |
| Dependências Firebase removidas | não — só a chamada em `CriarConta.kt` mudou de alvo |
| `backend/luna.db` de teste removido | sim, antes de qualquer commit |
| Commits ou push realizados | não |

---

# 11. Primeiro build Android real + evidências de `/auth/me` e registros diários

- **Data:** 2026-08-10
- **Máquina:** terceira máquina distinta das anteriores (identificada aqui como "máquina do
  escritório", caminho `C:\Users\ZARRO ADV\ic-app-kotlin`) — **não** é a máquina "Mille" das
  seções 1–10 nem a "máquina da UFU" citada em `instrucoes-build-ufu.md`. No início desta sessão
  não havia JDK, Android SDK, Android Studio nem `local.properties`; tudo foi instalado durante
  esta etapa.
- **Branch:** `develop` · **Commits desta etapa:** `b61a3dd` (versões de build) e `e3b51ab`
  (código de `/auth/me` e integração de `RegistrarHojeScreen`) — ambos enviados ao `origin`.
- **Objetivo:** destravar a compilação real do app (bloqueada por incompatibilidades de versão) e
  produzir o código/evidências que faltavam para a autenticação e a fatia
  `RegistrarHojeScreen → POST → persistência → GET`.

## 11.1 Nota de segurança verificada (sem incidente novo)

`instrucoes-build-ufu.md`, seção 0, registra que a máquina "Mille" teve um commit
(`8068aba`, "sem janatar") com `backend/.env`/`SECRET_KEY` vazado, enviado ao GitHub, com
remediação (Fase 6) descrita como pendente. Verificação feita nesta etapa, antes de qualquer
`push`:

```bash
git cat-file -t 8068aba        # fatal: Not a valid object name
git log --all --oneline -- backend/.env   # vazio
```

O commit equivalente nesta máquina (`612eb63`, mesma mensagem "sem janatar") tem **hash
diferente** de `8068aba` — evidência de que o histórico já havia sido reescrito/limpo no GitHub
antes deste clone existir. `backend/.env` nunca esteve rastreado no histórico atual. Os dois
commits desta etapa (`b61a3dd`, `e3b51ab`) foram construídos e enviados sobre esse histórico já
limpo — sem reintrodução do vazamento.

## 11.2 Correção de ambiente: `gradle-daemon-jvm.properties` fixando JDK 21

Arquivo pré-existente e já commitado (não criado nesta sessão) continha
`toolchainVersion=21` (feature "Daemon JVM criteria" do Gradle), forçando o daemon a exigir JDK 21
via auto-provisionamento (foojay), independente do `JAVA_HOME`. Nenhum outro arquivo do projeto
declarava bloco de toolchain — a configuração não era necessária. **Removido** (não regenerado
para JDK 17, para evitar depender de rede/Gradle antes da autorização), desbloqueando o build com
o JDK 17 real (JBR do Android Studio).

## 11.3 Alinhamento de versões (matriz conservadora)

| Componente | Antes | Depois |
|---|---|---|
| Gradle | 9.3.1 (incompatível com AGP 8.7.2) | **8.11.1** |
| AGP | 8.7.2 (não suporta `compileSdk 36`) | **8.10.1** |
| Kotlin (`kotlin` / plugin Compose) | 2.0.21 (divergente do `kotlin-android`) | **2.2.20** (unificado) |
| compileSdk / targetSdk | 36 | **36** (inalterado) |
| Compose BOM / demais dependências | 2024.10.01 / inalteradas | **inalteradas** |

## 11.4 Evidência 1 — Build Android validado (resultado real, relatado pelo usuário)

| Campo | Valor |
|---|---|
| Comando | `.\gradlew.bat assembleDebug` |
| Resultado | **BUILD SUCCESSFUL in 5m 27s** — 35 actionable tasks: 35 executed |
| Efeito colateral | Instalação automática do **Android SDK Build-Tools 35.0.0** durante o build |
| Warnings (não corrigidos, não bloqueantes) | (1) `Unable to strip libandroidx.graphics.path.so` / `libdatastore_shared_counter.so`; (2) `Duplicate branch condition` em `AppScreen.kt:316` (o branch `"chat" -> ChatLunaScreen(...)` está duplicado no `when`, linhas 289 e 316 — bug pré-existente, não introduzido nesta etapa, não corrigido por decisão de escopo) |

**Este é o primeiro `BUILD SUCCESSFUL` real do projeto nesta linha de investigação** — as
seções 1–10 nunca tiveram toolchain disponível para compilar.

## 11.5 Código adicionado (Evidências 2 e 3 — lado Android)

Sem alterar a assinatura de `RegistrarHojeScreen` usada por `AppScreen.kt` e sem tocar em nenhum
código funcional já existente (`AuthApi`/`AuthViewModel`/`RetrofitClient` originais preservados):

- `AuthApi.kt` / `AuthViewModel.kt`: `+me()` (`GET auth/me`) e `+buscarUsuarioAutenticado()` — só
  para comprovar que o JWT salvo no `SessaoDataStore` é aceito por uma rota protegida; não chamado
  por nenhuma tela ainda.
- `RegistroDiarioApi.kt`, `RegistroDiarioDtos.kt`, `RegistroDiarioViewModel.kt` (novos): mesmo
  padrão do `AuthViewModel`, para `POST`/`GET /registros-diarios`.
- `RegistrarHojeScreen.kt`: o botão "Salvar" agora chama a API e só navega após sucesso real
  (antes, `AppScreen.kt` descartava os dados digitados — bug já mapeado no `CLAUDE.md` §4).

**Compilado com sucesso junto do restante do app (seção 11.4). Execução real no
emulador/dispositivo (renderização, clique, token de fato persistido) ainda não foi observada
nesta etapa** — ver 11.7.

## 11.6 Backend: verificação ponta a ponta com HTTP real (mesmo padrão da seção 10.1, agora cobrindo também registros diários)

Ambiente recriado nesta máquina (`backend/.venv`, `backend/.env` com `SECRET_KEY` gerado via
`secrets.token_hex(32)`, `alembic upgrade head`):

| # | Comando/Requisição | Esperado | Obtido |
|---|---|---|---|
| 1 | `pytest -q` | 146 passed | **146 passed, 1 warning em 63,23s** — bate com a seção 3.2/8.5 |
| 2 | `POST /auth/registrar` | 201 | **201**, `UsuarioOut` completo |
| 3 | `POST /auth/login` | 200 | **200**, `access_token` JWT real |
| 4 | `GET /auth/me` com token | 200 | **200**, usuário autenticado retornado |
| 5 | `GET /auth/me` sem token | 401 | **401**, `{"detail":"Not authenticated"}` |
| 6 | `POST /registros-diarios` com token | 201 | **201**, registro criado (`id:1`) |
| 7 | `GET /registros-diarios` com token | 200 | **200**, lista com o mesmo registro |

**Achado descartado (falso positivo):** a primeira tentativa do item 6 via `curl`/Git Bash
retornou `400 "There was an error parsing the body"` ao enviar o acento em "Cansaço" — não é bug
do backend, é code page do terminal Windows/Git Bash montando o corpo da requisição. Repetido via
`urllib` do Python (UTF-8 nativo): sucesso confirmado; bytes da resposta gravados em arquivo e
lidos com encoding correto para confirmar que `"Cansaço"` chega intacto.

## 11.7 O que ainda não está provado

- **Execução real do app** (emulador ou dispositivo): nenhuma disponível nesta máquina —
  `adb devices` retornou lista vazia e `emulator -list-avds` não mostrou nenhum AVD configurado.
  Sem isso, não é possível observar visualmente login/cadastro, o token sendo salvo de fato, nem
  `RegistrarHojeScreen` sendo usada de ponta a ponta pela interface real.
- **Evidência 2 (auth ponta a ponta) e Evidência 3 (RegistrarHoje ponta a ponta)** continuam,
  então, no mesmo status já registrado na seção 10.3 para o lado Android: código escrito,
  revisado e (agora também) compilado com sucesso — execução real pendente.

## 11.8 Próximo passo

Criar um AVD (Android Studio → Device Manager, ou `avdmanager`), iniciá-lo, subir o backend
(`uvicorn app.main:app --host 0.0.0.0 --port 8000` a partir de `backend/`) e repetir manualmente a
sequência de login/cadastro/registro diário através da interface real — mesmo roteiro de
`instrucoes-build-ufu.md`, seção 4, agora aplicável a esta máquina também (já tem toolchain).

## 11.9 Verificações de integridade

| Verificação | Resultado |
|---|---|
| `backend/.env` alterado/commitado | criado nesta máquina, **não rastreado** (`.gitignore`), confirmado antes de qualquer commit |
| `AppScreen.kt` alterado | não |
| Dependências Firebase removidas | não |
| Warnings do build corrigidos | não — registrados, fora de escopo desta etapa |
| Commits realizados | `b61a3dd`, `e3b51ab` |
| Push realizado | sim, para `origin/develop` |
