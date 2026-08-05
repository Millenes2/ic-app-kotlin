from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException, Query, Response, status
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

from app.db.session import get_db
from app.dependencies import get_current_usuario
from app.models import RespostaObjetivo, Usuario
from app.schemas.resposta_objetivo import RespostaObjetivoCreate, RespostaObjetivoOut

router = APIRouter(prefix="/respostas-objetivo", tags=["Respostas dos Objetivos"])


def _buscar_resposta(
    db: Session, usuario_id: int, objetivo: str, etapa: int
) -> Optional[RespostaObjetivo]:
    return (
        db.query(RespostaObjetivo)
        .filter(
            RespostaObjetivo.usuario_id == usuario_id,
            RespostaObjetivo.objetivo == objetivo,
            RespostaObjetivo.etapa == etapa,
        )
        .first()
    )


@router.post(
    "",
    response_model=RespostaObjetivoOut,
    summary="Criar ou atualizar uma resposta de objetivo",
    response_description="Resposta salva (criada ou atualizada)",
    responses={
        200: {"description": "Já existia uma resposta para este objetivo e etapa; opcao_selecionada foi atualizada"},
        201: {"description": "Resposta criada (não existia para essa combinação de objetivo e etapa)"},
        401: {"description": "Token ausente, inválido ou expirado"},
        422: {"description": "Dados inválidos: objetivo fora dos valores aceitos, etapa não positiva, ou opcao_selecionada vazia/só com espaços"},
    },
    description=(
        "Funciona como criação-ou-atualização (upsert): no máximo uma resposta por "
        "usuária, `objetivo` e `etapa`. Se a combinação ainda não existir, cria e "
        "responde **201**; se já existir, atualiza `opcao_selecionada` e responde "
        "**200** — nunca cria duplicidade. `objetivo` deve estar entre os valores já "
        "aceitos em `PATCH /perfil` (`objetivo_atual`)."
    ),
)
def salvar_resposta(
    dados: RespostaObjetivoCreate,
    response: Response,
    usuario_atual: Usuario = Depends(get_current_usuario),
    db: Session = Depends(get_db),
):
    resposta = _buscar_resposta(db, usuario_atual.id, dados.objetivo, dados.etapa)

    if resposta is not None:
        resposta.opcao_selecionada = dados.opcao_selecionada
        db.commit()
        response.status_code = status.HTTP_200_OK
        db.refresh(resposta)
        return resposta

    resposta = RespostaObjetivo(usuario_id=usuario_atual.id, **dados.model_dump())
    db.add(resposta)
    try:
        db.commit()
    except IntegrityError:
        # Duas requisições concorrentes tentando criar a mesma combinação
        # usuario_id + objetivo + etapa: a que perde a corrida do commit cai
        # aqui e passa a atualizar o registro que a outra acabou de criar,
        # em vez de propagar o IntegrityError como um 500.
        db.rollback()
        resposta = _buscar_resposta(db, usuario_atual.id, dados.objetivo, dados.etapa)
        if resposta is None:
            # A unicidade violada só pode ser usuario_id + objetivo + etapa
            # (a única constraint da tabela), então este ramo não deveria
            # ser alcançável; ainda assim, respondemos 409 em vez de deixar
            # a situação virar um 500 não tratado.
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail="Não foi possível salvar a resposta devido a um conflito de dados; tente novamente.",
            )
        resposta.opcao_selecionada = dados.opcao_selecionada
        db.commit()
        response.status_code = status.HTTP_200_OK
    else:
        response.status_code = status.HTTP_201_CREATED

    db.refresh(resposta)
    return resposta


@router.get(
    "",
    response_model=List[RespostaObjetivoOut],
    summary="Listar respostas de objetivo",
    response_description="Respostas de objetivo da usuária, ordenadas por objetivo e etapa",
    responses={401: {"description": "Token ausente, inválido ou expirado"}},
    description=(
        "Lista apenas as respostas da própria usuária autenticada, ordenadas por "
        "`objetivo` e depois por `etapa`, ambos crescentes. `objetivo` e `etapa` são "
        "filtros opcionais e combináveis; um filtro sem correspondência retorna lista "
        "vazia."
    ),
)
def listar_respostas(
    objetivo: Optional[str] = Query(None, description="Filtra respostas por objetivo (opcional)"),
    etapa: Optional[int] = Query(None, description="Filtra respostas por etapa (opcional)"),
    usuario_atual: Usuario = Depends(get_current_usuario),
    db: Session = Depends(get_db),
):
    query = db.query(RespostaObjetivo).filter(RespostaObjetivo.usuario_id == usuario_atual.id)

    if objetivo is not None:
        query = query.filter(RespostaObjetivo.objetivo == objetivo)
    if etapa is not None:
        query = query.filter(RespostaObjetivo.etapa == etapa)

    return query.order_by(RespostaObjetivo.objetivo.asc(), RespostaObjetivo.etapa.asc()).all()
