from datetime import date, timedelta

USUARIO_TESTE = {
    "email": "ciclo@example.com",
    "senha": "senha123",
    "nome": "Ciclo Teste",
}

OUTRO_USUARIO = {
    "email": "outro-ciclo@example.com",
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


def dia(deslocamento=0):
    return (date.today() + timedelta(days=deslocamento)).isoformat()


# --- POST /registros-ciclo ---------------------------------------------------


def test_post_registro_sem_token(client):
    resposta = client.post("/registros-ciclo", json={"data": dia(), "menstruacao": True})

    assert resposta.status_code == 401


def test_post_registro_com_menstruacao_true(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/registros-ciclo", headers=headers, json={"data": dia(), "menstruacao": True}
    )

    assert resposta.status_code == 201
    corpo = resposta.json()
    assert corpo["data"] == dia()
    assert corpo["menstruacao"] is True
    assert corpo["fase_calculada"] is None
    assert corpo["observacao"] is None
    assert "id" in corpo
    assert "criado_em" in corpo


def test_post_registro_com_menstruacao_false(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/registros-ciclo", headers=headers, json={"data": dia(), "menstruacao": False}
    )

    assert resposta.status_code == 201
    assert resposta.json()["menstruacao"] is False


def test_post_registro_data_futura(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/registros-ciclo", headers=headers, json={"data": dia(1), "menstruacao": True}
    )

    assert resposta.status_code == 422


def test_post_registro_data_duplicada_para_o_mesmo_usuario(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-ciclo", headers=headers, json={"data": dia(), "menstruacao": True}
    ).json()

    resposta = client.post(
        "/registros-ciclo", headers=headers, json={"data": dia(), "menstruacao": False}
    )

    assert resposta.status_code == 409
    detalhe = resposta.json()["detail"]
    assert "PATCH" in detalhe
    assert f"/registros-ciclo/{criado['id']}" in detalhe


def test_post_registro_mesma_data_para_usuarios_diferentes_e_permitido(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    client.post("/registros-ciclo", headers=headers_a, json={"data": dia(), "menstruacao": True})

    resposta = client.post(
        "/registros-ciclo", headers=headers_b, json={"data": dia(), "menstruacao": False}
    )

    assert resposta.status_code == 201


def test_post_registro_remove_espacos_das_pontas_da_observacao(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/registros-ciclo",
        headers=headers,
        json={"data": dia(), "menstruacao": True, "observacao": "  fluxo leve  "},
    )

    assert resposta.status_code == 201
    assert resposta.json()["observacao"] == "fluxo leve"


def test_post_registro_observacao_apenas_espacos(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/registros-ciclo",
        headers=headers,
        json={"data": dia(), "menstruacao": True, "observacao": "   "},
    )

    assert resposta.status_code == 422


def test_post_registro_menstruacao_ausente(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post("/registros-ciclo", headers=headers, json={"data": dia()})

    assert resposta.status_code == 422


def test_post_registro_menstruacao_nao_booleana(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/registros-ciclo", headers=headers, json={"data": dia(), "menstruacao": "talvez"}
    )

    assert resposta.status_code == 422


def test_post_registro_ignora_usuario_id_enviado_pelo_cliente(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    outro = client.post(
        "/registros-ciclo", headers=headers_b, json={"data": dia(-1), "menstruacao": True}
    ).json()

    resposta = client.post(
        "/registros-ciclo",
        headers=headers_a,
        json={"data": dia(), "menstruacao": True, "usuario_id": outro["usuario_id"]},
    )

    assert resposta.status_code == 201
    assert resposta.json()["usuario_id"] != outro["usuario_id"]


# --- GET /registros-ciclo (listagem) -----------------------------------------


def test_listar_registros_sem_token(client):
    resposta = client.get("/registros-ciclo")

    assert resposta.status_code == 401


def test_listar_registros_isola_usuarios_diferentes(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    client.post("/registros-ciclo", headers=headers_a, json={"data": dia(), "menstruacao": True})
    client.post("/registros-ciclo", headers=headers_b, json={"data": dia(-1), "menstruacao": False})

    resposta_a = client.get("/registros-ciclo", headers=headers_a)

    assert resposta_a.status_code == 200
    corpo = resposta_a.json()
    assert len(corpo) == 1
    assert corpo[0]["data"] == dia()


def test_listar_registros_sem_filtro_retorna_todos(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post("/registros-ciclo", headers=headers, json={"data": dia(-2), "menstruacao": True})
    client.post("/registros-ciclo", headers=headers, json={"data": dia(-1), "menstruacao": False})

    resposta = client.get("/registros-ciclo", headers=headers)

    assert resposta.status_code == 200
    assert len(resposta.json()) == 2


def test_listar_registros_ordena_da_data_mais_recente_para_a_mais_antiga(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post("/registros-ciclo", headers=headers, json={"data": dia(-5), "menstruacao": True})
    client.post("/registros-ciclo", headers=headers, json={"data": dia(), "menstruacao": False})
    client.post("/registros-ciclo", headers=headers, json={"data": dia(-2), "menstruacao": True})

    resposta = client.get("/registros-ciclo", headers=headers)

    assert resposta.status_code == 200
    datas = [item["data"] for item in resposta.json()]
    assert datas == [dia(), dia(-2), dia(-5)]


def test_listar_registros_filtra_somente_por_data_inicio(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post("/registros-ciclo", headers=headers, json={"data": dia(-10), "menstruacao": True})
    client.post("/registros-ciclo", headers=headers, json={"data": dia(-3), "menstruacao": False})
    client.post("/registros-ciclo", headers=headers, json={"data": dia(), "menstruacao": True})

    resposta = client.get(
        "/registros-ciclo", headers=headers, params={"data_inicio": dia(-5)}
    )

    assert resposta.status_code == 200
    datas = [item["data"] for item in resposta.json()]
    assert datas == [dia(), dia(-3)]


def test_listar_registros_filtra_somente_por_data_fim(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post("/registros-ciclo", headers=headers, json={"data": dia(-10), "menstruacao": True})
    client.post("/registros-ciclo", headers=headers, json={"data": dia(-3), "menstruacao": False})
    client.post("/registros-ciclo", headers=headers, json={"data": dia(), "menstruacao": True})

    resposta = client.get("/registros-ciclo", headers=headers, params={"data_fim": dia(-3)})

    assert resposta.status_code == 200
    datas = [item["data"] for item in resposta.json()]
    assert datas == [dia(-3), dia(-10)]


def test_listar_registros_filtra_por_periodo(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post("/registros-ciclo", headers=headers, json={"data": dia(-10), "menstruacao": True})
    client.post("/registros-ciclo", headers=headers, json={"data": dia(-5), "menstruacao": False})
    client.post("/registros-ciclo", headers=headers, json={"data": dia(), "menstruacao": True})

    resposta = client.get(
        "/registros-ciclo",
        headers=headers,
        params={"data_inicio": dia(-6), "data_fim": dia(-1)},
    )

    assert resposta.status_code == 200
    corpo = resposta.json()
    assert len(corpo) == 1
    assert corpo[0]["data"] == dia(-5)


def test_listar_registros_com_intervalo_de_datas_invalido(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.get(
        "/registros-ciclo",
        headers=headers,
        params={"data_inicio": dia(), "data_fim": dia(-5)},
    )

    assert resposta.status_code == 422


# --- GET /registros-ciclo/{id} -----------------------------------------------


def test_obter_registro_sem_token(client):
    resposta = client.get("/registros-ciclo/1")

    assert resposta.status_code == 401


def test_obter_registro_inexistente(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.get("/registros-ciclo/999", headers=headers)

    assert resposta.status_code == 404


def test_obter_registro_de_outro_usuario_retorna_404(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    criado = client.post(
        "/registros-ciclo", headers=headers_a, json={"data": dia(), "menstruacao": True}
    ).json()

    resposta = client.get(f"/registros-ciclo/{criado['id']}", headers=headers_b)

    assert resposta.status_code == 404


def test_obter_registro_com_sucesso(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-ciclo", headers=headers, json={"data": dia(), "menstruacao": True}
    ).json()

    resposta = client.get(f"/registros-ciclo/{criado['id']}", headers=headers)

    assert resposta.status_code == 200
    assert resposta.json()["id"] == criado["id"]


# --- PATCH /registros-ciclo/{id} ---------------------------------------------


def test_patch_registro_sem_token(client):
    resposta = client.patch("/registros-ciclo/1", json={"menstruacao": False})

    assert resposta.status_code == 401


def test_patch_registro_inexistente(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.patch("/registros-ciclo/999", headers=headers, json={"menstruacao": False})

    assert resposta.status_code == 404


def test_patch_registro_de_outro_usuario_retorna_404(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    criado = client.post(
        "/registros-ciclo", headers=headers_a, json={"data": dia(), "menstruacao": True}
    ).json()

    resposta = client.patch(
        f"/registros-ciclo/{criado['id']}", headers=headers_b, json={"menstruacao": False}
    )

    assert resposta.status_code == 404


def test_patch_registro_atualizacao_parcial_preserva_outros_campos(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-ciclo",
        headers=headers,
        json={"data": dia(), "menstruacao": True, "observacao": "Original"},
    ).json()

    resposta = client.patch(
        f"/registros-ciclo/{criado['id']}", headers=headers, json={"menstruacao": False}
    )

    assert resposta.status_code == 200
    corpo = resposta.json()
    assert corpo["menstruacao"] is False
    assert corpo["observacao"] == "Original"


def test_patch_registro_limpa_observacao_com_null(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-ciclo",
        headers=headers,
        json={"data": dia(), "menstruacao": True, "observacao": "Original"},
    ).json()

    resposta = client.patch(
        f"/registros-ciclo/{criado['id']}", headers=headers, json={"observacao": None}
    )

    assert resposta.status_code == 200
    assert resposta.json()["observacao"] is None


def test_patch_registro_menstruacao_null(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-ciclo", headers=headers, json={"data": dia(), "menstruacao": True}
    ).json()

    resposta = client.patch(
        f"/registros-ciclo/{criado['id']}", headers=headers, json={"menstruacao": None}
    )

    assert resposta.status_code == 422


def test_patch_registro_data_null(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-ciclo", headers=headers, json={"data": dia(), "menstruacao": True}
    ).json()

    resposta = client.patch(f"/registros-ciclo/{criado['id']}", headers=headers, json={"data": None})

    assert resposta.status_code == 422


def test_patch_registro_data_futura(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-ciclo", headers=headers, json={"data": dia(), "menstruacao": True}
    ).json()

    resposta = client.patch(
        f"/registros-ciclo/{criado['id']}", headers=headers, json={"data": dia(1)}
    )

    assert resposta.status_code == 422


def test_patch_registro_observacao_apenas_espacos(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-ciclo", headers=headers, json={"data": dia(), "menstruacao": True}
    ).json()

    resposta = client.patch(
        f"/registros-ciclo/{criado['id']}", headers=headers, json={"observacao": "   "}
    )

    assert resposta.status_code == 422


def test_patch_registro_para_data_ja_usada_pelo_mesmo_usuario(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post("/registros-ciclo", headers=headers, json={"data": dia(-1), "menstruacao": True})
    criado = client.post(
        "/registros-ciclo", headers=headers, json={"data": dia(), "menstruacao": False}
    ).json()

    resposta = client.patch(
        f"/registros-ciclo/{criado['id']}", headers=headers, json={"data": dia(-1)}
    )

    assert resposta.status_code == 409


def test_patch_registro_mantendo_a_mesma_data_nao_gera_conflito(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-ciclo", headers=headers, json={"data": dia(), "menstruacao": True}
    ).json()

    resposta = client.patch(
        f"/registros-ciclo/{criado['id']}",
        headers=headers,
        json={"data": dia(), "menstruacao": False},
    )

    assert resposta.status_code == 200
    assert resposta.json()["menstruacao"] is False


# --- DELETE /registros-ciclo/{id} --------------------------------------------


def test_delete_registro_sem_token(client):
    resposta = client.delete("/registros-ciclo/1")

    assert resposta.status_code == 401


def test_delete_registro_inexistente(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.delete("/registros-ciclo/999", headers=headers)

    assert resposta.status_code == 404


def test_delete_registro_de_outro_usuario_retorna_404(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    criado = client.post(
        "/registros-ciclo", headers=headers_a, json={"data": dia(), "menstruacao": True}
    ).json()

    resposta = client.delete(f"/registros-ciclo/{criado['id']}", headers=headers_b)

    assert resposta.status_code == 404


def test_delete_registro_com_sucesso(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-ciclo", headers=headers, json={"data": dia(), "menstruacao": True}
    ).json()

    resposta = client.delete(f"/registros-ciclo/{criado['id']}", headers=headers)
    assert resposta.status_code == 204

    confirmacao = client.get(f"/registros-ciclo/{criado['id']}", headers=headers)
    assert confirmacao.status_code == 404
