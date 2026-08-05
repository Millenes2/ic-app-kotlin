from datetime import date, timedelta

USUARIO_TESTE = {
    "email": "diario@example.com",
    "senha": "senha123",
    "nome": "Diario Teste",
}

OUTRO_USUARIO = {
    "email": "outro-diario@example.com",
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


# --- POST /registros-diarios -------------------------------------------------


def test_post_registro_sem_token(client):
    resposta = client.post("/registros-diarios", json={"data": dia(), "humor": "Bem"})

    assert resposta.status_code == 401


def test_post_registro_com_todos_os_campos(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/registros-diarios",
        headers=headers,
        json={
            "data": dia(),
            "humor": "Bem",
            "sintoma_principal": "Cansaço",
            "observacao": "Dia tranquilo",
        },
    )

    assert resposta.status_code == 201
    corpo = resposta.json()
    assert corpo["data"] == dia()
    assert corpo["humor"] == "Bem"
    assert corpo["sintoma_principal"] == "Cansaço"
    assert corpo["observacao"] == "Dia tranquilo"
    assert "id" in corpo
    assert "criado_em" in corpo


def test_post_registro_somente_humor(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/registros-diarios", headers=headers, json={"data": dia(), "humor": "Triste"}
    )

    assert resposta.status_code == 201


def test_post_registro_somente_sintoma(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/registros-diarios",
        headers=headers,
        json={"data": dia(), "sintoma_principal": "Cólicas e dor"},
    )

    assert resposta.status_code == 201


def test_post_registro_somente_observacao(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/registros-diarios", headers=headers, json={"data": dia(), "observacao": "Só observação"}
    )

    assert resposta.status_code == 201


def test_post_registro_sem_nenhum_campo_preenchido(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post("/registros-diarios", headers=headers, json={"data": dia()})

    assert resposta.status_code == 422


def test_post_registro_observacao_em_branco_nao_conta_como_preenchido(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/registros-diarios", headers=headers, json={"data": dia(), "observacao": "   "}
    )

    assert resposta.status_code == 422


def test_post_registro_remove_espacos_das_pontas_da_observacao(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/registros-diarios",
        headers=headers,
        json={"data": dia(), "observacao": "  Dia difícil  "},
    )

    assert resposta.status_code == 201
    assert resposta.json()["observacao"] == "Dia difícil"


def test_post_registro_data_futura(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/registros-diarios", headers=headers, json={"data": dia(1), "humor": "Bem"}
    )

    assert resposta.status_code == 422


def test_post_registro_data_duplicada_para_o_mesmo_usuario(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-diarios", headers=headers, json={"data": dia(), "humor": "Bem"}
    ).json()

    resposta = client.post(
        "/registros-diarios", headers=headers, json={"data": dia(), "humor": "Triste"}
    )

    assert resposta.status_code == 409
    detalhe = resposta.json()["detail"]
    assert "PATCH" in detalhe
    assert f"/registros-diarios/{criado['id']}" in detalhe


def test_post_registro_mesma_data_para_usuarios_diferentes_e_permitido(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    client.post("/registros-diarios", headers=headers_a, json={"data": dia(), "humor": "Bem"})

    resposta = client.post(
        "/registros-diarios", headers=headers_b, json={"data": dia(), "humor": "Triste"}
    )

    assert resposta.status_code == 201


def test_post_registro_humor_invalido(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.post(
        "/registros-diarios", headers=headers, json={"data": dia(), "humor": "Furiosa"}
    )

    assert resposta.status_code == 422


# --- GET /registros-diarios (listagem) ---------------------------------------


def test_listar_registros_sem_token(client):
    resposta = client.get("/registros-diarios")

    assert resposta.status_code == 401


def test_listar_registros_isola_usuarios_diferentes(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    client.post("/registros-diarios", headers=headers_a, json={"data": dia(), "humor": "Bem"})
    client.post("/registros-diarios", headers=headers_b, json={"data": dia(-1), "humor": "Triste"})

    resposta_a = client.get("/registros-diarios", headers=headers_a)

    assert resposta_a.status_code == 200
    corpo = resposta_a.json()
    assert len(corpo) == 1
    assert corpo[0]["data"] == dia()


def test_listar_registros_filtra_por_data_inicio_e_fim(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post("/registros-diarios", headers=headers, json={"data": dia(-10), "humor": "Bem"})
    client.post("/registros-diarios", headers=headers, json={"data": dia(-5), "humor": "Triste"})
    client.post("/registros-diarios", headers=headers, json={"data": dia(), "humor": "Cansada"})

    resposta = client.get(
        "/registros-diarios",
        headers=headers,
        params={"data_inicio": dia(-6), "data_fim": dia(-1)},
    )

    assert resposta.status_code == 200
    corpo = resposta.json()
    assert len(corpo) == 1
    assert corpo[0]["data"] == dia(-5)


def test_listar_registros_sem_filtro_retorna_todos(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post("/registros-diarios", headers=headers, json={"data": dia(-2), "humor": "Bem"})
    client.post("/registros-diarios", headers=headers, json={"data": dia(-1), "humor": "Triste"})

    resposta = client.get("/registros-diarios", headers=headers)

    assert resposta.status_code == 200
    assert len(resposta.json()) == 2


def test_listar_registros_ordena_da_data_mais_recente_para_a_mais_antiga(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post("/registros-diarios", headers=headers, json={"data": dia(-5), "humor": "Bem"})
    client.post("/registros-diarios", headers=headers, json={"data": dia(), "humor": "Triste"})
    client.post("/registros-diarios", headers=headers, json={"data": dia(-2), "humor": "Cansada"})

    resposta = client.get("/registros-diarios", headers=headers)

    assert resposta.status_code == 200
    datas = [item["data"] for item in resposta.json()]
    assert datas == [dia(), dia(-2), dia(-5)]


def test_listar_registros_com_intervalo_de_datas_invalido(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.get(
        "/registros-diarios",
        headers=headers,
        params={"data_inicio": dia(), "data_fim": dia(-5)},
    )

    assert resposta.status_code == 422


# --- GET /registros-diarios/{id} ---------------------------------------------


def test_obter_registro_sem_token(client):
    resposta = client.get("/registros-diarios/1")

    assert resposta.status_code == 401


def test_obter_registro_inexistente(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.get("/registros-diarios/999", headers=headers)

    assert resposta.status_code == 404


def test_obter_registro_de_outro_usuario_retorna_404(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    criado = client.post(
        "/registros-diarios", headers=headers_a, json={"data": dia(), "humor": "Bem"}
    ).json()

    resposta = client.get(f"/registros-diarios/{criado['id']}", headers=headers_b)

    assert resposta.status_code == 404


def test_obter_registro_com_sucesso(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-diarios", headers=headers, json={"data": dia(), "humor": "Bem"}
    ).json()

    resposta = client.get(f"/registros-diarios/{criado['id']}", headers=headers)

    assert resposta.status_code == 200
    assert resposta.json()["id"] == criado["id"]


# --- PATCH /registros-diarios/{id} -------------------------------------------


def test_patch_registro_sem_token(client):
    resposta = client.patch("/registros-diarios/1", json={"humor": "Bem"})

    assert resposta.status_code == 401


def test_patch_registro_inexistente(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.patch("/registros-diarios/999", headers=headers, json={"humor": "Bem"})

    assert resposta.status_code == 404


def test_patch_registro_de_outro_usuario_retorna_404(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    criado = client.post(
        "/registros-diarios", headers=headers_a, json={"data": dia(), "humor": "Bem"}
    ).json()

    resposta = client.patch(
        f"/registros-diarios/{criado['id']}", headers=headers_b, json={"humor": "Triste"}
    )

    assert resposta.status_code == 404


def test_patch_registro_atualizacao_parcial_preserva_outros_campos(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-diarios",
        headers=headers,
        json={"data": dia(), "humor": "Bem", "observacao": "Original"},
    ).json()

    resposta = client.patch(
        f"/registros-diarios/{criado['id']}", headers=headers, json={"humor": "Cansada"}
    )

    assert resposta.status_code == 200
    corpo = resposta.json()
    assert corpo["humor"] == "Cansada"
    assert corpo["observacao"] == "Original"


def test_patch_registro_limpa_campo_com_null_mantendo_outro_preenchido(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-diarios",
        headers=headers,
        json={"data": dia(), "humor": "Bem", "observacao": "Original"},
    ).json()

    resposta = client.patch(
        f"/registros-diarios/{criado['id']}", headers=headers, json={"humor": None}
    )

    assert resposta.status_code == 200
    assert resposta.json()["humor"] is None
    assert resposta.json()["observacao"] == "Original"


def test_patch_registro_limpar_todos_os_campos_viola_invariante(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-diarios", headers=headers, json={"data": dia(), "humor": "Bem"}
    ).json()

    resposta = client.patch(
        f"/registros-diarios/{criado['id']}", headers=headers, json={"humor": None}
    )

    assert resposta.status_code == 422


def test_patch_registro_data_null(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-diarios", headers=headers, json={"data": dia(), "humor": "Bem"}
    ).json()

    resposta = client.patch(f"/registros-diarios/{criado['id']}", headers=headers, json={"data": None})

    assert resposta.status_code == 422


def test_patch_registro_data_futura(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-diarios", headers=headers, json={"data": dia(), "humor": "Bem"}
    ).json()

    resposta = client.patch(
        f"/registros-diarios/{criado['id']}", headers=headers, json={"data": dia(1)}
    )

    assert resposta.status_code == 422


def test_patch_registro_para_data_ja_usada_pelo_mesmo_usuario(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.post("/registros-diarios", headers=headers, json={"data": dia(-1), "humor": "Bem"})
    criado = client.post(
        "/registros-diarios", headers=headers, json={"data": dia(), "humor": "Triste"}
    ).json()

    resposta = client.patch(
        f"/registros-diarios/{criado['id']}", headers=headers, json={"data": dia(-1)}
    )

    assert resposta.status_code == 409


def test_patch_registro_remove_espacos_das_pontas_da_observacao(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-diarios", headers=headers, json={"data": dia(), "humor": "Bem"}
    ).json()

    resposta = client.patch(
        f"/registros-diarios/{criado['id']}",
        headers=headers,
        json={"observacao": "  nova observação  "},
    )

    assert resposta.status_code == 200
    assert resposta.json()["observacao"] == "nova observação"


def test_patch_registro_mantendo_a_mesma_data_nao_gera_conflito(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-diarios", headers=headers, json={"data": dia(), "humor": "Bem"}
    ).json()

    resposta = client.patch(
        f"/registros-diarios/{criado['id']}", headers=headers, json={"data": dia(), "humor": "Triste"}
    )

    assert resposta.status_code == 200
    assert resposta.json()["humor"] == "Triste"


# --- DELETE /registros-diarios/{id} ------------------------------------------


def test_delete_registro_sem_token(client):
    resposta = client.delete("/registros-diarios/1")

    assert resposta.status_code == 401


def test_delete_registro_inexistente(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.delete("/registros-diarios/999", headers=headers)

    assert resposta.status_code == 404


def test_delete_registro_de_outro_usuario_retorna_404(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)
    criado = client.post(
        "/registros-diarios", headers=headers_a, json={"data": dia(), "humor": "Bem"}
    ).json()

    resposta = client.delete(f"/registros-diarios/{criado['id']}", headers=headers_b)

    assert resposta.status_code == 404


def test_delete_registro_com_sucesso(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    criado = client.post(
        "/registros-diarios", headers=headers, json={"data": dia(), "humor": "Bem"}
    ).json()

    resposta = client.delete(f"/registros-diarios/{criado['id']}", headers=headers)
    assert resposta.status_code == 204

    confirmacao = client.get(f"/registros-diarios/{criado['id']}", headers=headers)
    assert confirmacao.status_code == 404
