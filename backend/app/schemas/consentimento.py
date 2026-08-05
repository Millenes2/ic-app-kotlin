from datetime import datetime

from pydantic import BaseModel, ConfigDict, field_validator


class ConsentimentoCreate(BaseModel):
    model_config = ConfigDict(json_schema_extra={"examples": [{"versao_termos": "1.0"}]})

    versao_termos: str

    @field_validator("versao_termos")
    @classmethod
    def normalizar_versao_termos(cls, valor: str) -> str:
        valor = valor.strip()
        if valor == "":
            raise ValueError(
                "versao_termos não pode ser vazia nem conter apenas espaços em branco"
            )
        return valor


class ConsentimentoOut(BaseModel):
    model_config = ConfigDict(
        from_attributes=True,
        json_schema_extra={
            "examples": [
                {
                    "id": 1,
                    "usuario_id": 1,
                    "versao_termos": "1.0",
                    "aceito_em": "2026-07-27T12:00:00Z",
                }
            ]
        },
    )

    id: int
    usuario_id: int
    versao_termos: str
    aceito_em: datetime
