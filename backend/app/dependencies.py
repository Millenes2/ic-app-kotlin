import jwt
from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy.orm import Session

from app.core.security import decodificar_access_token
from app.db.session import get_db
from app.models import Usuario

oauth2_scheme = OAuth2PasswordBearer(
    tokenUrl="/auth/login",
    scheme_name="Bearer JWT",
    description=(
        "Token JWT obtido em POST /auth/login. Envie como cabeçalho "
        "'Authorization: Bearer <token>' em toda rota protegida."
    ),
)


def get_current_usuario(
    token: str = Depends(oauth2_scheme),
    db: Session = Depends(get_db),
) -> Usuario:
    credenciais_invalidas = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Credenciais inválidas",
        headers={"WWW-Authenticate": "Bearer"},
    )

    try:
        payload = decodificar_access_token(token)
        usuario_id = payload.get("sub")
        if usuario_id is None:
            raise credenciais_invalidas
    except jwt.PyJWTError:
        raise credenciais_invalidas

    usuario = db.get(Usuario, int(usuario_id))
    if usuario is None:
        raise credenciais_invalidas

    return usuario
