USUARIO_TESTE = {
    "email": "consentimento@example.com",
    "senha": "senha123",
    "nome": "Consentimento Teste",
}

OUTRO_USUARIO = {
    "email": "outro-consentimento@example.com",
    "senha": "senha456",
    "nome": "Outro Usuario",
}


def registrar_e_logar(client, dados):
    client.post("/auth/registrar", json=dados)
    login = client.post(
        "/auth/login", json={"email": dados["email"], "senha": dados["senha"]}
    )
    token = login.json()["access_token"]
    return {"Authorization": f"Bearer {token}"}


# --- POST /consentimentos -----------------------------------------------------


def test_post_consentimento_sem_token(client):
    resposta = client.post("/consentimentos", json={"versao_termos": "1.0"})

    assert resposta.status_code == 401


def test_post_consentimento_criacao_valida(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/consentimentos", headers=headers, json={"versao_termos": "1.0"}
    )

    assert resposta.status_code == 201
    corpo = resposta.json()
    assert corpo["versao_termos"] == "1.0"
    assert "id" in corpo
    assert "usuario_id" in corpo
    assert "aceito_em" in corpo


def test_post_consentimento_aceito_em_preenchido_pelo_servidor(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/consentimentos", headers=headers, json={"versao_termos": "1.0"}
    )

    assert resposta.status_code == 201
    assert resposta.json()["aceito_em"] is not None


def test_post_consentimento_ignora_aceito_em_enviado_pelo_cliente(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/consentimentos",
        headers=headers,
        json={"versao_termos": "1.0", "aceito_em": "2000-01-01T00:00:00Z"},
    )

    assert resposta.status_code == 201
    assert not resposta.json()["aceito_em"].startswith("2000-01-01")


def test_post_consentimento_versao_termos_vazia(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/consentimentos", headers=headers, json={"versao_termos": ""}
    )

    assert resposta.status_code == 422


def test_post_consentimento_versao_termos_somente_espacos(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/consentimentos", headers=headers, json={"versao_termos": "   "}
    )

    assert resposta.status_code == 422


def test_post_consentimento_remove_espacos_das_pontas(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/consentimentos", headers=headers, json={"versao_termos": "  1.0  "}
    )

    assert resposta.status_code == 201
    assert resposta.json()["versao_termos"] == "1.0"


def test_post_consentimento_reenvio_da_mesma_versao_nao_cria_duplicidade(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    primeiro = client.post(
        "/consentimentos", headers=headers, json={"versao_termos": "1.0"}
    ).json()

    resposta = client.post(
        "/consentimentos", headers=headers, json={"versao_termos": "1.0"}
    )

    assert resposta.status_code == 200
    corpo = resposta.json()
    assert corpo["id"] == primeiro["id"]


def test_post_consentimento_reenvio_atualiza_aceito_em(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    primeiro = client.post(
        "/consentimentos", headers=headers, json={"versao_termos": "1.0"}
    ).json()
    client.post(
        "/consentimentos", headers=headers, json={"versao_termos": "2.0"}
    )

    reenvio = client.post(
        "/consentimentos", headers=headers, json={"versao_termos": "1.0"}
    ).json()

    assert reenvio["aceito_em"] >= primeiro["aceito_em"]

    atual = client.get("/consentimentos/atual", headers=headers).json()
    assert atual["versao_termos"] == "1.0"


def test_post_consentimento_versoes_diferentes_permitidas_para_a_mesma_usuaria(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post("/consentimentos", headers=headers, json={"versao_termos": "1.0"})

    resposta = client.post(
        "/consentimentos", headers=headers, json={"versao_termos": "2.0"}
    )

    assert resposta.status_code == 201


def test_post_consentimento_mesma_versao_permitida_para_usuarias_diferentes(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    client.post("/consentimentos", headers=headers_a, json={"versao_termos": "1.0"})

    resposta = client.post(
        "/consentimentos", headers=headers_b, json={"versao_termos": "1.0"}
    )

    assert resposta.status_code == 201


def test_post_consentimento_ignora_usuario_id_enviado_pelo_cliente(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    de_outro_usuario = client.post(
        "/consentimentos", headers=headers_b, json={"versao_termos": "1.0"}
    ).json()

    resposta = client.post(
        "/consentimentos",
        headers=headers_a,
        json={"versao_termos": "1.0", "usuario_id": de_outro_usuario["usuario_id"]},
    )

    assert resposta.status_code == 201
    corpo = resposta.json()
    assert corpo["usuario_id"] != de_outro_usuario["usuario_id"]
    assert corpo["id"] != de_outro_usuario["id"]

    atual_do_outro = client.get("/consentimentos/atual", headers=headers_b).json()
    assert atual_do_outro["id"] == de_outro_usuario["id"]


# --- GET /consentimentos/atual -------------------------------------------------


def test_get_consentimento_atual_sem_token(client):
    resposta = client.get("/consentimentos/atual")

    assert resposta.status_code == 401


def test_get_consentimento_atual_sem_consentimentos(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.get("/consentimentos/atual", headers=headers)

    assert resposta.status_code == 404


def test_get_consentimento_atual_com_um_consentimento(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/consentimentos", headers=headers, json={"versao_termos": "1.0"}
    ).json()

    resposta = client.get("/consentimentos/atual", headers=headers)

    assert resposta.status_code == 200
    assert resposta.json()["id"] == criado["id"]


def test_get_consentimento_atual_retorna_a_versao_mais_recente(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post("/consentimentos", headers=headers, json={"versao_termos": "1.0"})
    mais_recente = client.post(
        "/consentimentos", headers=headers, json={"versao_termos": "2.0"}
    ).json()

    resposta = client.get("/consentimentos/atual", headers=headers)

    assert resposta.status_code == 200
    corpo = resposta.json()
    assert corpo["id"] == mais_recente["id"]
    assert corpo["versao_termos"] == "2.0"


def test_get_consentimento_atual_isola_usuarios_diferentes(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    client.post("/consentimentos", headers=headers_a, json={"versao_termos": "1.0"})
    esperado_b = client.post(
        "/consentimentos", headers=headers_b, json={"versao_termos": "2.0"}
    ).json()

    resposta_b = client.get("/consentimentos/atual", headers=headers_b)

    assert resposta_b.status_code == 200
    corpo = resposta_b.json()
    assert corpo["id"] == esperado_b["id"]
    assert corpo["usuario_id"] == esperado_b["usuario_id"]
