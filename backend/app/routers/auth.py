from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

from app.core.security import criar_access_token, hash_senha, verificar_senha
from app.db.session import get_db
from app.dependencies import get_current_usuario
from app.models import Usuario
from app.schemas.auth import LoginRequest, Token
from app.schemas.usuario import UsuarioCreate, UsuarioOut

router = APIRouter(prefix="/auth", tags=["Autenticação"])

_EMAIL_DUPLICADO = "Já existe uma conta com este e-mail"


@router.post(
    "/registrar",
    response_model=UsuarioOut,
    status_code=status.HTTP_201_CREATED,
    summary="Criar conta",
    response_description="Conta criada com sucesso",
    responses={
        409: {"description": _EMAIL_DUPLICADO},
        422: {"description": "Dados inválidos (e-mail em formato inválido, senha curta ou nome vazio)"},
    },
    description=(
        "Cria uma conta de usuária a partir de `email`, `senha` e `nome`. Não exige "
        "autenticação. `data_nascimento`, `peso` e `objetivo_atual` são preenchidos "
        "depois, via `PATCH /perfil`."
    ),
)
def registrar(dados: UsuarioCreate, db: Session = Depends(get_db)):
    usuario_existente = db.query(Usuario).filter(Usuario.email == dados.email).first()
    if usuario_existente is not None:
        raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail=_EMAIL_DUPLICADO)

    usuario = Usuario(
        email=dados.email,
        senha_hash=hash_senha(dados.senha),
        nome=dados.nome,
    )
    db.add(usuario)
    try:
        db.commit()
    except IntegrityError:
        # Duas requisições concorrentes registrando o mesmo e-mail: a que
        # perde a corrida do commit cai aqui em vez de propagar o
        # IntegrityError como um 500 — mesmo padrão adotado nos demais
        # routers para violações de unicidade.
        db.rollback()
        raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail=_EMAIL_DUPLICADO)
    db.refresh(usuario)

    return usuario


@router.post(
    "/login",
    response_model=Token,
    summary="Autenticar e obter token JWT",
    response_description="Token de acesso emitido com sucesso",
    responses={
        401: {"description": "E-mail ou senha inválidos"},
        422: {"description": "Dados inválidos (e-mail em formato inválido ou senha ausente)"},
    },
    description=(
        "Recebe `email`/`senha` em JSON (não o formulário OAuth2 padrão) e devolve um "
        "`access_token` JWT (`token_type=\"bearer\"`), válido por "
        "`ACCESS_TOKEN_EXPIRE_MINUTES`. Esse token deve ser enviado em todas as rotas "
        "protegidas como `Authorization: Bearer <token>`."
    ),
)
def login(dados: LoginRequest, db: Session = Depends(get_db)):
    usuario = db.query(Usuario).filter(Usuario.email == dados.email).first()

    if usuario is None or not verificar_senha(dados.senha, usuario.senha_hash):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="E-mail ou senha inválidos",
        )

    access_token = criar_access_token(dados={"sub": str(usuario.id)})

    return Token(access_token=access_token)


@router.get(
    "/me",
    response_model=UsuarioOut,
    summary="Obter a própria conta",
    response_description="Dados da usuária autenticada",
    responses={401: {"description": "Token ausente, inválido ou expirado"}},
    description=(
        "Rota de referência para validar o token JWT. Requer autenticação via "
        "`Authorization: Bearer <token>`. Retorna sempre a conta da própria usuária "
        "autenticada — nunca é possível consultar a conta de outra pessoa por aqui."
    ),
)
def me(usuario_atual: Usuario = Depends(get_current_usuario)):
    return usuario_atual
