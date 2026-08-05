USUARIO_TESTE = {
    "email": "objetivo@example.com",
    "senha": "senha123",
    "nome": "Objetivo Teste",
}

OUTRO_USUARIO = {
    "email": "outro-objetivo@example.com",
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


# --- POST /respostas-objetivo -------------------------------------------------


def test_post_resposta_sem_token(client):
    resposta = client.post(
        "/respostas-objetivo",
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "Sim"},
    )

    assert resposta.status_code == 401


def test_post_resposta_criacao_valida(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "Sim"},
    )

    assert resposta.status_code == 201
    corpo = resposta.json()
    assert corpo["objetivo"] == "Engravidar"
    assert corpo["etapa"] == 1
    assert corpo["opcao_selecionada"] == "Sim"
    assert "id" in corpo
    assert "usuario_id" in corpo
    assert "criado_em" in corpo


def test_post_resposta_reenvio_atualiza_em_vez_de_duplicar(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    primeira = client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "Sim"},
    ).json()

    resposta = client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "Não"},
    )

    assert resposta.status_code == 200
    corpo = resposta.json()
    assert corpo["id"] == primeira["id"]
    assert corpo["opcao_selecionada"] == "Não"


def test_post_resposta_reenvio_nao_cria_registro_duplicado(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "Sim"},
    )
    client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "Não"},
    )

    listagem = client.get("/respostas-objetivo", headers=headers)

    assert len(listagem.json()) == 1


def test_post_resposta_mesma_etapa_permitida_para_objetivos_diferentes(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "Sim"},
    )

    resposta = client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Monitorar meu ciclo", "etapa": 1, "opcao_selecionada": "Irregular"},
    )

    assert resposta.status_code == 201
    listagem = client.get("/respostas-objetivo", headers=headers)
    assert len(listagem.json()) == 2


def test_post_resposta_etapas_diferentes_permitidas_para_o_mesmo_objetivo(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "Sim"},
    )

    resposta = client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Engravidar", "etapa": 2, "opcao_selecionada": "Não"},
    )

    assert resposta.status_code == 201
    listagem = client.get("/respostas-objetivo", headers=headers)
    assert len(listagem.json()) == 2


def test_post_resposta_mesma_combinacao_permitida_para_usuarios_diferentes(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    client.post(
        "/respostas-objetivo",
        headers=headers_a,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "Sim"},
    )

    resposta = client.post(
        "/respostas-objetivo",
        headers=headers_b,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "Não"},
    )

    assert resposta.status_code == 201


def test_post_resposta_opcao_selecionada_vazia(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": ""},
    )

    assert resposta.status_code == 422


def test_post_resposta_opcao_selecionada_somente_espacos(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "   "},
    )

    assert resposta.status_code == 422


def test_post_resposta_remove_espacos_das_pontas_da_opcao_selecionada(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "  Sim  "},
    )

    assert resposta.status_code == 201
    assert resposta.json()["opcao_selecionada"] == "Sim"


def test_post_resposta_objetivo_invalido(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Perder peso", "etapa": 1, "opcao_selecionada": "Sim"},
    )

    assert resposta.status_code == 422


def test_post_resposta_etapa_invalida(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Engravidar", "etapa": 0, "opcao_selecionada": "Sim"},
    )

    assert resposta.status_code == 422


def test_post_resposta_ignora_usuario_id_enviado_pelo_cliente(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    de_outro_usuario = client.post(
        "/respostas-objetivo",
        headers=headers_b,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "Sim"},
    ).json()

    resposta = client.post(
        "/respostas-objetivo",
        headers=headers_a,
        json={
            "objetivo": "Engravidar",
            "etapa": 1,
            "opcao_selecionada": "Não",
            "usuario_id": de_outro_usuario["usuario_id"],
        },
    )

    assert resposta.status_code == 201
    corpo = resposta.json()
    assert corpo["usuario_id"] != de_outro_usuario["usuario_id"]
    assert corpo["id"] != de_outro_usuario["id"]

    resposta_do_outro = client.get("/respostas-objetivo", headers=headers_b).json()
    assert len(resposta_do_outro) == 1
    assert resposta_do_outro[0]["opcao_selecionada"] == "Sim"


# --- GET /respostas-objetivo (listagem) --------------------------------------


def test_listar_respostas_sem_token(client):
    resposta = client.get("/respostas-objetivo")

    assert resposta.status_code == 401


def test_listar_respostas_vazia(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.get("/respostas-objetivo", headers=headers)

    assert resposta.status_code == 200
    assert resposta.json() == []


def test_listar_respostas_isola_usuarios_diferentes(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    client.post(
        "/respostas-objetivo",
        headers=headers_a,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "Sim"},
    )
    client.post(
        "/respostas-objetivo",
        headers=headers_b,
        json={"objetivo": "Monitorar meu ciclo", "etapa": 1, "opcao_selecionada": "Regular"},
    )

    resposta_a = client.get("/respostas-objetivo", headers=headers_a)

    assert resposta_a.status_code == 200
    corpo = resposta_a.json()
    assert len(corpo) == 1
    assert corpo[0]["objetivo"] == "Engravidar"


def test_listar_respostas_nunca_retorna_respostas_de_outra_usuaria(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    client.post(
        "/respostas-objetivo",
        headers=headers_a,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "Sim"},
    )
    client.post(
        "/respostas-objetivo",
        headers=headers_a,
        json={"objetivo": "Engravidar", "etapa": 2, "opcao_selecionada": "Não"},
    )

    resposta_b = client.get("/respostas-objetivo", headers=headers_b)

    assert resposta_b.status_code == 200
    assert resposta_b.json() == []


def test_listar_respostas_filtro_por_objetivo(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "Sim"},
    )
    client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Monitorar meu ciclo", "etapa": 1, "opcao_selecionada": "Regular"},
    )

    resposta = client.get(
        "/respostas-objetivo", headers=headers, params={"objetivo": "Engravidar"}
    )

    assert resposta.status_code == 200
    corpo = resposta.json()
    assert len(corpo) == 1
    assert corpo[0]["objetivo"] == "Engravidar"


def test_listar_respostas_filtro_por_etapa(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "Sim"},
    )
    client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Engravidar", "etapa": 2, "opcao_selecionada": "Não"},
    )

    resposta = client.get("/respostas-objetivo", headers=headers, params={"etapa": 2})

    assert resposta.status_code == 200
    corpo = resposta.json()
    assert len(corpo) == 1
    assert corpo[0]["etapa"] == 2


def test_listar_respostas_filtro_por_objetivo_e_etapa(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "Sim"},
    )
    client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Engravidar", "etapa": 2, "opcao_selecionada": "Não"},
    )
    client.post(
        "/respostas-objetivo",
        headers=headers,
        json={"objetivo": "Monitorar meu ciclo", "etapa": 2, "opcao_selecionada": "Regular"},
    )

    resposta = client.get(
        "/respostas-objetivo",
        headers=headers,
        params={"objetivo": "Engravidar", "etapa": 2},
    )

    assert resposta.status_code == 200
    corpo = resposta.json()
    assert len(corpo) == 1
    assert corpo[0]["objetivo"] == "Engravidar"
    assert corpo[0]["etapa"] == 2
