from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.db.session import get_db
from app.dependencies import get_current_usuario
from app.models import Usuario
from app.schemas.usuario import UsuarioOut, UsuarioPerfilUpdate

router = APIRouter(prefix="/perfil", tags=["Perfil"])


@router.get(
    "",
    response_model=UsuarioOut,
    summary="Obter o próprio perfil",
    response_description="Dados de perfil da usuária autenticada",
    responses={401: {"description": "Token ausente, inválido ou expirado"}},
    description="Requer autenticação via `Authorization: Bearer <token>`. Retorna sempre o perfil da própria usuária autenticada.",
)
def obter_perfil(usuario_atual: Usuario = Depends(get_current_usuario)):
    return usuario_atual


@router.patch(
    "",
    response_model=UsuarioOut,
    summary="Atualizar o próprio perfil",
    response_description="Perfil atualizado",
    responses={
        401: {"description": "Token ausente, inválido ou expirado"},
        422: {
            "description": (
                "Dados inválidos: nome vazio ou null, peso fora de 0–500, data de "
                "nascimento no futuro ou idade abaixo de 12 anos, objetivo_atual fora "
                "dos valores aceitos"
            )
        },
    },
    description=(
        "Atualização parcial (`PATCH`) — campos ausentes no corpo permanecem "
        "inalterados. `email` e `senha` não fazem parte deste schema. Campos "
        "enviados explicitamente como `null` limpam o valor, exceto `nome`, que "
        "nunca pode ser `null` nem vazio."
    ),
)
def atualizar_perfil(
    dados: UsuarioPerfilUpdate,
    usuario_atual: Usuario = Depends(get_current_usuario),
    db: Session = Depends(get_db),
):
    for campo, valor in dados.model_dump(exclude_unset=True).items():
        setattr(usuario_atual, campo, valor)

    db.commit()
    db.refresh(usuario_atual)

    return usuario_atual
