from datetime import datetime

from pydantic import BaseModel, ConfigDict, field_validator

from app.schemas.usuario import OBJETIVOS_VALIDOS


class RespostaObjetivoCreate(BaseModel):
    model_config = ConfigDict(
        json_schema_extra={
            "examples": [{"objetivo": "Engravidar", "etapa": 1, "opcao_selecionada": "Sim"}]
        }
    )

    objetivo: str
    etapa: int
    opcao_selecionada: str

    @field_validator("objetivo")
    @classmethod
    def validar_objetivo(cls, valor: str) -> str:
        if valor not in OBJETIVOS_VALIDOS:
            raise ValueError(
                f"objetivo inválido; valores aceitos: {sorted(OBJETIVOS_VALIDOS)}"
            )
        return valor

    @field_validator("etapa")
    @classmethod
    def validar_etapa(cls, valor: int) -> int:
        if valor < 1:
            raise ValueError("etapa deve ser um número inteiro positivo (>= 1)")
        return valor

    @field_validator("opcao_selecionada")
    @classmethod
    def normalizar_opcao_selecionada(cls, valor: str) -> str:
        valor = valor.strip()
        if valor == "":
            raise ValueError(
                "opcao_selecionada não pode ser vazia nem conter apenas espaços em branco"
            )
        return valor


class RespostaObjetivoOut(BaseModel):
    model_config = ConfigDict(
        from_attributes=True,
        json_schema_extra={
            "examples": [
                {
                    "id": 1,
                    "usuario_id": 1,
                    "objetivo": "Engravidar",
                    "etapa": 1,
                    "opcao_selecionada": "Sim",
                    "criado_em": "2026-07-27T12:00:00Z",
                }
            ]
        },
    )

    id: int
    usuario_id: int
    objetivo: str
    etapa: int
    opcao_selecionada: str
    criado_em: datetime
