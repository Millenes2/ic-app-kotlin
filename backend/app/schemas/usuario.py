from datetime import date, datetime
from typing import Optional

from pydantic import BaseModel, ConfigDict, EmailStr, Field, field_validator, model_validator

# Valores exibidos em ObjetivoScreen.kt (os 5 cards) mais "Geral", gravado quando
# o usuário toca em "Pular" nesse mesmo fluxo (ver AppScreen.kt).
OBJETIVOS_VALIDOS = {
    "Monitorar meu ciclo",
    "Entender meu corpo",
    "Engravidar",
    "Acompanhar minha Gestação",
    "Melhorar minha Saúde Mental",
    "Geral",
}

IDADE_MINIMA_ANOS = 12


class UsuarioCreate(BaseModel):
    model_config = ConfigDict(
        json_schema_extra={
            "examples": [{"email": "usuaria@example.com", "senha": "senha123", "nome": "Maria"}]
        }
    )

    email: EmailStr
    senha: str = Field(min_length=6)
    nome: str = Field(min_length=1)


class UsuarioOut(BaseModel):
    model_config = ConfigDict(
        from_attributes=True,
        json_schema_extra={
            "examples": [
                {
                    "id": 1,
                    "email": "usuaria@example.com",
                    "nome": "Maria",
                    "data_nascimento": "1995-04-12",
                    "peso": 62.5,
                    "objetivo_atual": "Monitorar meu ciclo",
                    "criado_em": "2026-07-27T12:00:00Z",
                }
            ]
        },
    )

    id: int
    email: EmailStr
    nome: str
    data_nascimento: Optional[date] = None
    peso: Optional[float] = None
    objetivo_atual: Optional[str] = None
    criado_em: datetime


class UsuarioPerfilUpdate(BaseModel):
    """Atualização parcial do perfil. Email e senha não fazem parte deste schema.

    Campos ausentes no corpo da requisição permanecem inalterados; campos
    enviados explicitamente como null limpam o valor (ver `exclude_unset`
    no router de perfil).
    """

    model_config = ConfigDict(
        json_schema_extra={
            "examples": [
                {
                    "nome": "Maria",
                    "data_nascimento": "1995-04-12",
                    "peso": 62.5,
                    "objetivo_atual": "Monitorar meu ciclo",
                }
            ]
        }
    )

    nome: Optional[str] = None
    data_nascimento: Optional[date] = None
    peso: Optional[float] = None
    objetivo_atual: Optional[str] = None

    @model_validator(mode="before")
    @classmethod
    def rejeitar_nome_null(cls, dados):
        # `field_validator("nome")` recebe None tanto quando o campo não é
        # enviado quanto quando é enviado como null; para diferenciar os dois
        # casos (nome não pode ser limpo, ao contrário dos demais campos)
        # é preciso olhar o corpo bruto da requisição antes da validação.
        if isinstance(dados, dict) and "nome" in dados and dados["nome"] is None:
            raise ValueError("nome não pode ser null")
        return dados

    @field_validator("nome")
    @classmethod
    def validar_nome(cls, valor: Optional[str]) -> Optional[str]:
        if valor is not None and not valor.strip():
            raise ValueError("nome não pode ser vazio")
        return valor

    @field_validator("peso")
    @classmethod
    def validar_peso(cls, valor: Optional[float]) -> Optional[float]:
        if valor is not None and not (0 < valor <= 500):
            raise ValueError("peso deve ser maior que 0 e no máximo 500 kg")
        return valor

    @field_validator("data_nascimento")
    @classmethod
    def validar_data_nascimento(cls, valor: Optional[date]) -> Optional[date]:
        if valor is None:
            return valor

        hoje = date.today()
        if valor > hoje:
            raise ValueError("data de nascimento não pode ser no futuro")

        idade = hoje.year - valor.year - ((hoje.month, hoje.day) < (valor.month, valor.day))
        if idade < IDADE_MINIMA_ANOS:
            raise ValueError(f"idade mínima é de {IDADE_MINIMA_ANOS} anos")

        return valor

    @field_validator("objetivo_atual")
    @classmethod
    def validar_objetivo_atual(cls, valor: Optional[str]) -> Optional[str]:
        if valor is not None and valor not in OBJETIVOS_VALIDOS:
            raise ValueError(
                f"objetivo_atual inválido; valores aceitos: {sorted(OBJETIVOS_VALIDOS)}"
            )
        return valor
