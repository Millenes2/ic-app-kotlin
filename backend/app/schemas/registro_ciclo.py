from datetime import date, datetime
from typing import Optional

from pydantic import BaseModel, ConfigDict, field_validator, model_validator


class RegistroCicloCreate(BaseModel):
    model_config = ConfigDict(
        json_schema_extra={
            "examples": [{"data": "2026-07-27", "menstruacao": True, "observacao": "Fluxo leve."}]
        }
    )

    data: date
    menstruacao: bool
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
        if valor is None:
            return valor
        valor = valor.strip()
        if valor == "":
            raise ValueError("observacao não pode conter apenas espaços em branco")
        return valor


class RegistroCicloUpdate(BaseModel):
    """Atualização parcial do registro de ciclo.

    Campos ausentes no corpo da requisição permanecem inalterados. `data` e
    `menstruacao` não podem ser limpos com `null` — a primeira é obrigatória
    para o registro existir, e a segunda deve sempre ser um booleano válido.
    `observacao` pode ser limpa com `null` explícito, pois é opcional.
    `fase_calculada` não faz parte deste schema: seu preenchimento é uma
    etapa futura (cálculo/previsão do ciclo), fora do escopo deste módulo.
    """

    model_config = ConfigDict(
        json_schema_extra={"examples": [{"menstruacao": False, "observacao": "Sem fluxo hoje."}]}
    )

    data: Optional[date] = None
    menstruacao: Optional[bool] = None
    observacao: Optional[str] = None

    @model_validator(mode="before")
    @classmethod
    def rejeitar_data_ou_menstruacao_null(cls, dados):
        if isinstance(dados, dict):
            if "data" in dados and dados["data"] is None:
                raise ValueError("data não pode ser null")
            if "menstruacao" in dados and dados["menstruacao"] is None:
                raise ValueError("menstruacao não pode ser null")
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
        if valor is None:
            return valor
        valor = valor.strip()
        if valor == "":
            raise ValueError("observacao não pode conter apenas espaços em branco")
        return valor


class RegistroCicloOut(BaseModel):
    model_config = ConfigDict(
        from_attributes=True,
        json_schema_extra={
            "examples": [
                {
                    "id": 1,
                    "usuario_id": 1,
                    "data": "2026-07-27",
                    "menstruacao": True,
                    "fase_calculada": None,
                    "observacao": "Fluxo leve.",
                    "criado_em": "2026-07-27T12:00:00Z",
                }
            ]
        },
    )

    id: int
    usuario_id: int
    data: date
    menstruacao: bool
    fase_calculada: Optional[str] = None
    observacao: Optional[str] = None
    criado_em: datetime
