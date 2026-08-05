from sqlalchemy import text

from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)


def test_health_endpoint_responde_ok():
    resposta = client.get("/health")

    assert resposta.status_code == 200
    assert resposta.json() == {"status": "ok"}


def test_conexao_com_banco_funciona(db_session):
    resultado = db_session.execute(text("SELECT 1")).scalar()

    assert resultado == 1


def test_tabelas_de_dominio_foram_criadas(db_session):
    from app.models import (
        Consentimento,
        RegistroCiclo,
        RegistroDiario,
        RespostaObjetivo,
        Usuario,
    )

    for modelo in (Usuario, Consentimento, RegistroDiario, RegistroCiclo, RespostaObjetivo):
        assert db_session.query(modelo).count() == 0
