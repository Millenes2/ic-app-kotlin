from datetime import date, datetime
from typing import Optional

from pydantic import BaseModel, ConfigDict, field_validator, model_validator

from app.models.enums import HumorEnum, SintomaEnum


def _preenchido(valor: Optional[str]) -> bool:
    return valor is not None and valor.strip() != ""


class RegistroDiarioCreate(BaseModel):
    model_config = ConfigDict(
        json_schema_extra={
            "examples": [
                {
                    "data": "2026-07-27",
                    "humor": "Bem",
                    "sintoma_principal": "Cansaço",
                    "observacao": "Dormi bem e me senti disposta.",
                }
            ]
        }
    )

    data: date
    humor: Optional[HumorEnum] = None
    sintoma_principal: Optional[SintomaEnum] = None
    observacao: Optional[str] = None

    @field_validator("data")
    @classmethod
    def validar_data_nao_futura(cls, valor: date) -> date:
        if valor > date.today():
            raise ValueError("data não pode ser no futuro")
        return valor

    @field_validator("observacao")
    @classmethod
    def normalizar_observacao(cls, valor: Optional[str]) -> Optional[str]:
        return valor.strip() if valor is not None else valor

    @model_validator(mode="after")
    def validar_ao_menos_um_campo_preenchido(self) -> "RegistroDiarioCreate":
        if self.humor is None and self.sintoma_principal is None and not _preenchido(self.observacao):
            raise ValueError(
                "ao menos um entre humor, sintoma_principal e observacao deve estar preenchido"
            )
        return self


class RegistroDiarioUpdate(BaseModel):
    """Atualização parcial do registro diário.

    Campos ausentes no corpo da requisição permanecem inalterados; `humor`,
    `sintoma_principal` e `observacao` podem ser limpos com `null` explícito,
    desde que ao menos um dos três continue preenchido após a atualização
    (verificado no router, que conhece o estado atual do registro). `data`
    não pode ser limpa com `null`, pois é obrigatória para o registro existir.
    """

    model_config = ConfigDict(
        json_schema_extra={"examples": [{"humor": "Cansada", "observacao": "Dia puxado no trabalho."}]}
    )

    data: Optional[date] = None
    humor: Optional[HumorEnum] = None
    sintoma_principal: Optional[SintomaEnum] = None
    observacao: Optional[str] = None

    @model_validator(mode="before")
    @classmethod
    def rejeitar_data_null(cls, dados):
        if isinstance(dados, dict) and "data" in dados and dados["data"] is None:
            raise ValueError("data não pode ser null")
        return dados

    @field_validator("data")
    @classmethod
    def validar_data_nao_futura(cls, valor: Optional[date]) -> Optional[date]:
        if valor is not None and valor > date.today():
            raise ValueError("data não pode ser no futuro")
        return valor

    @field_validator("observacao")
    @classmethod
    def normalizar_observacao(cls, valor: Optional[str]) -> Optional[str]:
        return valor.strip() if valor is not None else valor


class RegistroDiarioOut(BaseModel):
    model_config = ConfigDict(
        from_attributes=True,
        json_schema_extra={
            "examples": [
                {
                    "id": 1,
                    "usuario_id": 1,
                    "data": "2026-07-27",
                    "humor": "Bem",
                    "sintoma_principal": "Cansaço",
                    "observacao": "Dormi bem e me senti disposta.",
                    "criado_em": "2026-07-27T12:00:00Z",
                }
            ]
        },
    )

    id: int
    usuario_id: int
    data: date
    humor: Optional[HumorEnum] = None
    sintoma_principal: Optional[SintomaEnum] = None
    observacao: Optional[str] = None
    criado_em: datetime
