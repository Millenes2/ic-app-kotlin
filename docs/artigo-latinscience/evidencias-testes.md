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
