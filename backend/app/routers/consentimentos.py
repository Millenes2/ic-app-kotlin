from datetime import datetime, timezone
from typing import Optional

from fastapi import APIRouter, Depends, HTTPException, Response, status
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

from app.db.session import get_db
from app.dependencies import get_current_usuario
from app.models import Consentimento, Usuario
from app.schemas.consentimento import ConsentimentoCreate, ConsentimentoOut

router = APIRouter(prefix="/consentimentos", tags=["Consentimentos"])


def _buscar_consentimento(
    db: Session, usuario_id: int, versao_termos: str
) -> Optional[Consentimento]:
    return (
        db.query(Consentimento)
        .filter(
            Consentimento.usuario_id == usuario_id,
            Consentimento.versao_termos == versao_termos,
        )
        .first()
    )


@router.post(
    "",
    response_model=ConsentimentoOut,
    summary="Registrar ou reafirmar aceite dos termos",
    response_description="Consentimento salvo (criado ou reafirmado)",
    responses={
        200: {"description": "Já havia um consentimento para essa versão; aceito_em foi atualizado (reafirmação)"},
        201: {"description": "Consentimento criado (não existia para essa versão dos termos)"},
        401: {"description": "Token ausente, inválido ou expirado"},
        422: {"description": "versao_termos ausente, vazia ou só com espaços"},
    },
    description=(
        "Registra o aceite de uma versão dos termos pela usuária autenticada. No "
        "máximo um consentimento por usuária e por `versao_termos`. Reenviar a mesma "
        "versão não cria duplicidade: atualiza `aceito_em` para o momento atual "
        "(reafirmação) e responde **200**; uma versão nova responde **201**. "
        "`usuario_id` e `aceito_em` são sempre definidos pelo servidor — nunca pelo "
        "corpo da requisição. Consentimentos de outras versões nunca são apagados."
    ),
)
def registrar_consentimento(
    dados: ConsentimentoCreate,
    response: Response,
    usuario_atual: Usuario = Depends(get_current_usuario),
    db: Session = Depends(get_db),
):
    consentimento = _buscar_consentimento(db, usuario_atual.id, dados.versao_termos)

    if consentimento is not None:
        # Reenvio da mesma versão dos termos: em vez de rejeitar ou criar uma
        # linha duplicada, tratamos como uma reafirmação do consentimento e
        # atualizamos `aceito_em` para o momento atual — mais coerente com o
        # nome do campo ("aceito em") do que deixá-lo parado na primeira vez
        # que essa versão foi aceita. Nenhum outro consentimento (de outra
        # versão) é alterado ou removido.
        consentimento.aceito_em = datetime.now(timezone.utc)
        db.commit()
        response.status_code = status.HTTP_200_OK
        db.refresh(consentimento)
        return consentimento

    consentimento = Consentimento(usuario_id=usuario_atual.id, **dados.model_dump())
    db.add(consentimento)
    try:
        db.commit()
    except IntegrityError:
        # Duas requisições concorrentes registrando a mesma combinação
        # usuario_id + versao_termos: a que perde a corrida do commit cai
        # aqui e passa a atualizar (reafirmar) o registro que a outra acabou
        # de criar, em vez de propagar o IntegrityError como um 500.
        db.rollback()
        consentimento = _buscar_consentimento(db, usuario_atual.id, dados.versao_termos)
        if consentimento is None:
            # A unicidade violada só pode ser usuario_id + versao_termos (a
            # única constraint da tabela), então este ramo não deveria ser
            # alcançável; ainda assim, respondemos 409 em vez de deixar a
            # situação virar um 500 não tratado.
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail="Não foi possível salvar o consentimento devido a um conflito de dados; tente novamente.",
            )
        consentimento.aceito_em = datetime.now(timezone.utc)
        db.commit()
        response.status_code = status.HTTP_200_OK
    else:
        response.status_code = status.HTTP_201_CREATED

    db.refresh(consentimento)
    return consentimento


@router.get(
    "/atual",
    response_model=ConsentimentoOut,
    summary="Obter o consentimento mais recente",
    response_description="Consentimento mais recente da usuária autenticada",
    responses={
        401: {"description": "Token ausente, inválido ou expirado"},
        404: {"description": "A usuária ainda não possui nenhum consentimento registrado"},
    },
    description=(
        "Retorna o consentimento mais recente da própria usuária autenticada, "
        "ordenado por `aceito_em` decrescente e, em caso de empate, por `id` "
        "decrescente. 404 se a usuária ainda não tiver registrado nenhum "
        "consentimento."
    ),
)
def obter_consentimento_atual(
    usuario_atual: Usuario = Depends(get_current_usuario),
    db: Session = Depends(get_db),
):
    consentimento = (
        db.query(Consentimento)
        .filter(Consentimento.usuario_id == usuario_atual.id)
        .order_by(Consentimento.aceito_em.desc(), Consentimento.id.desc())
        .first()
    )
    if consentimento is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Nenhum consentimento registrado para este usuário",
        )

    return consentimento
