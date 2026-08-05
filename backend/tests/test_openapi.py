"""Testes estruturais da documentação OpenAPI/Swagger.

Verificam apenas elementos estruturais (chaves, presença de rotas/tags,
códigos de resposta declarados) — nunca o texto completo de descrições, para
não acoplar os testes à redação exata da documentação.
"""

from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)


def _openapi():
    resposta = client.get("/openapi.json")
    assert resposta.status_code == 200
    return resposta.json()


def test_openapi_json_acessivel():
    resposta = client.get("/openapi.json")

    assert resposta.status_code == 200
    corpo = resposta.json()
    assert corpo["info"]["title"] == "Luna API"
    assert "paths" in corpo


def test_docs_acessivel():
    resposta = client.get("/docs")

    assert resposta.status_code == 200


def test_openapi_contem_as_tags_esperadas():
    schema = _openapi()
    nomes_das_tags = {tag["name"] for tag in schema.get("tags", [])}

    esperadas = {
        "Autenticação",
        "Perfil",
        "Registros Diários",
        "Registros do Ciclo",
        "Respostas dos Objetivos",
        "Consentimentos",
        "Sistema",
    }
    assert esperadas.issubset(nomes_das_tags)


def test_openapi_contem_os_endpoints_implementados():
    schema = _openapi()
    caminhos = set(schema["paths"].keys())

    esperados = {
        "/",
        "/health",
        "/auth/registrar",
        "/auth/login",
        "/auth/me",
        "/perfil",
        "/registros-diarios",
        "/registros-diarios/{registro_id}",
        "/registros-ciclo",
        "/registros-ciclo/{registro_id}",
        "/respostas-objetivo",
        "/consentimentos",
        "/consentimentos/atual",
    }
    assert esperados.issubset(caminhos)


def test_openapi_documenta_esquema_bearer_jwt():
    schema = _openapi()
    esquemas_seguranca = schema["components"]["securitySchemes"]

    assert len(esquemas_seguranca) > 0
    descricoes = " ".join(
        str(esquema.get("description", "")) for esquema in esquemas_seguranca.values()
    )
    assert "bearer" in descricoes.lower() or "jwt" in descricoes.lower()


def test_openapi_nao_expoe_senha_hash():
    schema = _openapi()
    schemas = schema["components"]["schemas"]

    for nome, definicao in schemas.items():
        propriedades = definicao.get("properties", {})
        assert "senha_hash" not in propriedades, f"senha_hash exposto em {nome}"
        assert "senha" not in propriedades or nome in {
            "UsuarioCreate",
            "LoginRequest",
        }, f"senha exposta indevidamente em {nome}"


def test_openapi_nao_expoe_secret_key():
    schema = _openapi()

    assert "SECRET_KEY" not in str(schema)


def test_endpoints_principais_documentam_codigos_de_resposta():
    schema = _openapi()
    paths = schema["paths"]

    casos = [
        ("/auth/login", "post", {"401", "422"}),
        ("/auth/registrar", "post", {"409", "422"}),
        ("/perfil", "patch", {"401", "422"}),
        ("/registros-diarios", "post", {"401", "409", "422"}),
        ("/registros-diarios", "get", {"401", "422"}),
        ("/registros-diarios/{registro_id}", "get", {"401", "404"}),
        ("/registros-ciclo", "post", {"401", "409", "422"}),
        ("/respostas-objetivo", "post", {"401", "422"}),
        ("/consentimentos", "post", {"401", "422"}),
        ("/consentimentos/atual", "get", {"401", "404"}),
    ]

    for caminho, metodo, codigos_esperados in casos:
        operacao = paths[caminho][metodo]
        codigos_documentados = set(operacao["responses"].keys())
        faltantes = codigos_esperados - codigos_documentados
        assert not faltantes, f"{metodo.upper()} {caminho} não documenta {faltantes}"


def test_endpoints_protegidos_exigem_security():
    schema = _openapi()
    paths = schema["paths"]

    rotas_protegidas = [
        ("/auth/me", "get"),
        ("/perfil", "get"),
        ("/registros-diarios", "post"),
        ("/registros-ciclo", "get"),
        ("/respostas-objetivo", "get"),
        ("/consentimentos/atual", "get"),
    ]

    for caminho, metodo in rotas_protegidas:
        operacao = paths[caminho][metodo]
        assert operacao.get("security"), f"{metodo.upper()} {caminho} não exige autenticação no schema"


def test_rotas_publicas_nao_exigem_security():
    schema = _openapi()
    paths = schema["paths"]

    rotas_publicas = [
        ("/", "get"),
        ("/health", "get"),
        ("/auth/registrar", "post"),
        ("/auth/login", "post"),
    ]

    for caminho, metodo in rotas_publicas:
        operacao = paths[caminho][metodo]
        assert not operacao.get("security"), f"{metodo.upper()} {caminho} não deveria exigir autenticação"
