from datetime import date, timedelta

USUARIO_TESTE = {
    "email": "perfil@example.com",
    "senha": "senha123",
    "nome": "Perfil Teste",
}

OUTRO_USUARIO = {
    "email": "outro@example.com",
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


def data_com_idade(anos):
    hoje = date.today()
    try:
        return hoje.replace(year=hoje.year - anos)
    except ValueError:
        # 29 de fevereiro em ano não bissexto.
        return hoje.replace(year=hoje.year - anos, day=28)


def test_get_perfil_sem_token(client):
    resposta = client.get("/perfil")

    assert resposta.status_code == 401


def test_get_perfil_com_token(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.get("/perfil", headers=headers)

    assert resposta.status_code == 200
    corpo = resposta.json()
    assert corpo["email"] == USUARIO_TESTE["email"]
    assert corpo["nome"] == USUARIO_TESTE["nome"]
    assert corpo["data_nascimento"] is None
    assert corpo["peso"] is None
    assert corpo["objetivo_atual"] is None


def test_patch_perfil_atualiza_todos_os_campos(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    nascimento = data_com_idade(20).isoformat()

    resposta = client.patch(
        "/perfil",
        headers=headers,
        json={
            "nome": "Novo Nome",
            "data_nascimento": nascimento,
            "peso": 65.5,
            "objetivo_atual": "Engravidar",
        },
    )

    assert resposta.status_code == 200
    corpo = resposta.json()
    assert corpo["nome"] == "Novo Nome"
    assert corpo["data_nascimento"] == nascimento
    assert corpo["peso"] == 65.5
    assert corpo["objetivo_atual"] == "Engravidar"

    confirmacao = client.get("/perfil", headers=headers)
    assert confirmacao.json() == corpo


def test_patch_perfil_atualizacao_parcial_preserva_outros_campos(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    nascimento = data_com_idade(25).isoformat()

    client.patch(
        "/perfil",
        headers=headers,
        json={
            "data_nascimento": nascimento,
            "peso": 70.0,
            "objetivo_atual": "Monitorar meu ciclo",
        },
    )

    resposta = client.patch("/perfil", headers=headers, json={"peso": 72.0})

    assert resposta.status_code == 200
    corpo = resposta.json()
    assert corpo["peso"] == 72.0
    assert corpo["data_nascimento"] == nascimento
    assert corpo["objetivo_atual"] == "Monitorar meu ciclo"
    assert corpo["nome"] == USUARIO_TESTE["nome"]


def test_patch_perfil_objetivo_invalido(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.patch(
        "/perfil", headers=headers, json={"objetivo_atual": "Objetivo Inexistente"}
    )

    assert resposta.status_code == 422


def test_patch_perfil_idade_abaixo_do_minimo(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    nascimento = data_com_idade(11).isoformat()

    resposta = client.patch(
        "/perfil", headers=headers, json={"data_nascimento": nascimento}
    )

    assert resposta.status_code == 422


def test_patch_perfil_data_nascimento_no_futuro(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    futuro = (date.today() + timedelta(days=1)).isoformat()

    resposta = client.patch(
        "/perfil", headers=headers, json={"data_nascimento": futuro}
    )

    assert resposta.status_code == 422


def test_patch_perfil_peso_invalido(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.patch("/perfil", headers=headers, json={"peso": 0})

    assert resposta.status_code == 422


def test_patch_perfil_nome_vazio(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.patch("/perfil", headers=headers, json={"nome": "   "})

    assert resposta.status_code == 422


def test_patch_perfil_nome_null(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)

    resposta = client.patch("/perfil", headers=headers, json={"nome": None})

    assert resposta.status_code == 422


def test_patch_perfil_limpa_campo_com_null(client):
    headers = registrar_e_logar(client, USUARIO_TESTE)
    client.patch("/perfil", headers=headers, json={"objetivo_atual": "Geral"})

    resposta = client.patch("/perfil", headers=headers, json={"objetivo_atual": None})

    assert resposta.status_code == 200
    assert resposta.json()["objetivo_atual"] is None


def test_get_perfil_isola_usuarios_diferentes(client):
    headers_a = registrar_e_logar(client, USUARIO_TESTE)
    headers_b = registrar_e_logar(client, OUTRO_USUARIO)

    client.patch("/perfil", headers=headers_a, json={"nome": "Nome A"})

    resposta_b = client.get("/perfil", headers=headers_b)

    assert resposta_b.status_code == 200
    assert resposta_b.json()["email"] == OUTRO_USUARIO["email"]
    assert resposta_b.json()["nome"] == OUTRO_USUARIO["nome"]


def test_patch_perfil_sem_token(client):
    resposta = client.patch("/perfil", json={"nome": "Sem Token"})

    assert resposta.status_code == 401
