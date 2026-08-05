USUARIO_TESTE = {
    "email": "millene@example.com",
    "senha": "senha123",
    "nome": "Millene",
}


def registrar_usuario_teste(client):
    return client.post("/auth/registrar", json=USUARIO_TESTE)


def test_registrar_usuario_com_sucesso(client):
    resposta = registrar_usuario_teste(client)

    assert resposta.status_code == 201
    corpo = resposta.json()
    assert corpo["email"] == USUARIO_TESTE["email"]
    assert corpo["nome"] == USUARIO_TESTE["nome"]
    assert "senha_hash" not in corpo
    assert "senha" not in corpo


def test_registrar_usuario_com_email_duplicado(client):
    registrar_usuario_teste(client)
    resposta = registrar_usuario_teste(client)

    assert resposta.status_code == 409


def test_login_com_credenciais_corretas(client):
    registrar_usuario_teste(client)

    resposta = client.post(
        "/auth/login",
        json={"email": USUARIO_TESTE["email"], "senha": USUARIO_TESTE["senha"]},
    )

    assert resposta.status_code == 200
    corpo = resposta.json()
    assert corpo["token_type"] == "bearer"
    assert corpo["access_token"]


def test_login_com_senha_errada(client):
    registrar_usuario_teste(client)

    resposta = client.post(
        "/auth/login",
        json={"email": USUARIO_TESTE["email"], "senha": "senha-errada"},
    )

    assert resposta.status_code == 401


def test_me_sem_token(client):
    resposta = client.get("/auth/me")

    assert resposta.status_code == 401


def test_me_com_token_valido(client):
    registrar_usuario_teste(client)
    login = client.post(
        "/auth/login",
        json={"email": USUARIO_TESTE["email"], "senha": USUARIO_TESTE["senha"]},
    )
    token = login.json()["access_token"]

    resposta = client.get("/auth/me", headers={"Authorization": f"Bearer {token}"})

    assert resposta.status_code == 200
    assert resposta.json()["email"] == USUARIO_TESTE["email"]


def test_me_com_token_invalido(client):
    resposta = client.get(
        "/auth/me", headers={"Authorization": "Bearer token-completamente-invalido"}
    )

    assert resposta.status_code == 401


def test_me_com_token_expirado(client):
    from datetime import timedelta

    from app.core.security import criar_access_token, decodificar_access_token

    registrar_usuario_teste(client)
    login = client.post(
        "/auth/login",
        json={"email": USUARIO_TESTE["email"], "senha": USUARIO_TESTE["senha"]},
    )
    sub = decodificar_access_token(login.json()["access_token"])["sub"]
    token_expirado = criar_access_token(dados={"sub": sub}, expires_delta=timedelta(seconds=-1))

    resposta = client.get("/auth/me", headers={"Authorization": f"Bearer {token_expirado}"})

    assert resposta.status_code == 401
