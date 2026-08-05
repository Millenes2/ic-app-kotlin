from datetime import date
from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

from app.db.session import get_db
from app.dependencies import get_current_usuario
from app.models import RegistroDiario, Usuario
from app.schemas.registro_diario import (
    RegistroDiarioCreate,
    RegistroDiarioOut,
    RegistroDiarioUpdate,
)

router = APIRouter(prefix="/registros-diarios", tags=["Registros Diários"])


def _buscar_registro_ou_404(db: Session, registro_id: int, usuario_id: int) -> RegistroDiario:
    registro = (
        db.query(RegistroDiario)
        .filter(RegistroDiario.id == registro_id, RegistroDiario.usuario_id == usuario_id)
        .first()
    )
    if registro is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Registro diário não encontrado",
        )
    return registro


def _buscar_registro_na_data(
    db: Session, usuario_id: int, data_registro: date, excluir_id: Optional[int] = None
) -> Optional[RegistroDiario]:
    query = db.query(RegistroDiario).filter(
        RegistroDiario.usuario_id == usuario_id,
        RegistroDiario.data == data_registro,
    )
    if excluir_id is not None:
        query = query.filter(RegistroDiario.id != excluir_id)
    return query.first()


def _commit_ou_conflito(
    db: Session, usuario_id: int, data_alvo: date, excluir_id: Optional[int] = None
) -> None:
    # Além da verificação prévia feita em cada rota, a unicidade real é
    # garantida pela constraint do banco; se duas requisições concorrentes
    # passarem pela verificação antes de uma delas commitar, o commit da
    # segunda viola a constraint. Tratamos isso aqui com rollback + 409 em
    # vez de deixar o IntegrityError virar um 500.
    try:
        db.commit()
    except IntegrityError:
        db.rollback()
        conflito = _buscar_registro_na_data(db, usuario_id, data_alvo, excluir_id=excluir_id)
        detalhe = "Já existe um registro diário para esta data."
        if conflito is not None:
            detalhe += f" Use PATCH /registros-diarios/{conflito.id} para atualizá-lo."
        raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail=detalhe)


@router.post(
    "",
    response_model=RegistroDiarioOut,
    status_code=status.HTTP_201_CREATED,
    summary="Criar registro diário",
    response_description="Registro diário criado",
    responses={
        401: {"description": "Token ausente, inválido ou expirado"},
        409: {"description": "Já existe um registro diário do usuário para essa data; use PATCH para atualizá-lo"},
        422: {
            "description": (
                "Dados inválidos: data no futuro, enum de humor/sintoma inválido, ou "
                "nenhum entre humor, sintoma_principal e observacao preenchido"
            )
        },
    },
    description=(
        "Cria um registro de humor/sintoma/observação para uma data. Regra de "
        "unicidade: no máximo um `RegistroDiario` por usuária e por data — reenviar "
        "a mesma data retorna 409 com orientação para usar `PATCH`. `data` não pode "
        "ser futura. Ao menos um entre `humor`, `sintoma_principal` e `observacao` "
        "deve estar preenchido."
    ),
)
def criar_registro(
    dados: RegistroDiarioCreate,
    usuario_atual: Usuario = Depends(get_current_usuario),
    db: Session = Depends(get_db),
):
    existente = _buscar_registro_na_data(db, usuario_atual.id, dados.data)
    if existente is not None:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=(
                "Já existe um registro diário para esta data. "
                f"Use PATCH /registros-diarios/{existente.id} para atualizá-lo."
            ),
        )

    registro = RegistroDiario(usuario_id=usuario_atual.id, **dados.model_dump())
    db.add(registro)
    _commit_ou_conflito(db, usuario_atual.id, dados.data)
    db.refresh(registro)

    return registro


@router.get(
    "",
    response_model=List[RegistroDiarioOut],
    summary="Listar registros diários",
    response_description="Registros diários da usuária, do mais recente ao mais antigo",
    responses={
        401: {"description": "Token ausente, inválido ou expirado"},
        422: {"description": "data_inicio maior que data_fim"},
    },
    description=(
        "Lista apenas os registros da própria usuária autenticada, ordenados por "
        "`data` decrescente. `data_inicio` e `data_fim` são opcionais e combináveis "
        "(filtram por `data >= data_inicio` e/ou `data <= data_fim`); "
        "`data_inicio` não pode ser maior que `data_fim`."
    ),
)
def listar_registros(
    data_inicio: Optional[date] = Query(
        None, description="Filtra registros com data >= data_inicio (opcional)"
    ),
    data_fim: Optional[date] = Query(
        None, description="Filtra registros com data <= data_fim (opcional)"
    ),
    usuario_atual: Usuario = Depends(get_current_usuario),
    db: Session = Depends(get_db),
):
    if data_inicio is not None and data_fim is not None and data_inicio > data_fim:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_CONTENT,
            detail="data_inicio não pode ser maior que data_fim",
        )

    query = db.query(RegistroDiario).filter(RegistroDiario.usuario_id == usuario_atual.id)

    if data_inicio is not None:
        query = query.filter(RegistroDiario.data >= data_inicio)
    if data_fim is not None:
        query = query.filter(RegistroDiario.data <= data_fim)

    return query.order_by(RegistroDiario.data.desc()).all()


@router.get(
    "/{registro_id}",
    response_model=RegistroDiarioOut,
    summary="Obter um registro diário",
    response_description="Registro diário encontrado",
    responses={
        401: {"description": "Token ausente, inválido ou expirado"},
        404: {"description": "Registro inexistente ou pertencente a outra usuária"},
    },
    description=(
        "Requer autenticação. Se o `id` não existir ou pertencer a outra usuária, "
        "retorna 404 em ambos os casos, para não revelar a existência do registro."
    ),
)
def obter_registro(
    registro_id: int,
    usuario_atual: Usuario = Depends(get_current_usuario),
    db: Session = Depends(get_db),
):
    return _buscar_registro_ou_404(db, registro_id, usuario_atual.id)


@router.patch(
    "/{registro_id}",
    response_model=RegistroDiarioOut,
    summary="Atualizar um registro diário",
    response_description="Registro diário atualizado",
    responses={
        401: {"description": "Token ausente, inválido ou expirado"},
        404: {"description": "Registro inexistente ou pertencente a outra usuária"},
        409: {"description": "A nova data já possui um registro do usuário"},
        422: {
            "description": (
                "Dados inválidos: data no futuro ou null, ou o estado final do "
                "registro deixaria humor, sintoma_principal e observacao todos vazios"
            )
        },
    },
    description=(
        "Atualização parcial (`PATCH`) — campos ausentes permanecem inalterados. "
        "`data` não pode ser `null`, mas pode ser trocada (409 se a nova data já "
        "tiver um registro do usuário). `humor`/`sintoma_principal`/`observacao` "
        "podem ser limpos com `null`, desde que ao menos um dos três continue "
        "preenchido após a atualização."
    ),
)
def atualizar_registro(
    registro_id: int,
    dados: RegistroDiarioUpdate,
    usuario_atual: Usuario = Depends(get_current_usuario),
    db: Session = Depends(get_db),
):
    registro = _buscar_registro_ou_404(db, registro_id, usuario_atual.id)
    atualizacoes = dados.model_dump(exclude_unset=True)

    if "data" in atualizacoes and atualizacoes["data"] != registro.data:
        if _buscar_registro_na_data(
            db, usuario_atual.id, atualizacoes["data"], excluir_id=registro_id
        ) is not None:
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail="Já existe um registro diário para esta data",
            )

    humor_final = atualizacoes["humor"] if "humor" in atualizacoes else registro.humor
    sintoma_final = (
        atualizacoes["sintoma_principal"]
        if "sintoma_principal" in atualizacoes
        else registro.sintoma_principal
    )
    observacao_final = (
        atualizacoes["observacao"] if "observacao" in atualizacoes else registro.observacao
    )

    observacao_preenchida = observacao_final is not None and observacao_final.strip() != ""
    if humor_final is None and sintoma_final is None and not observacao_preenchida:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_CONTENT,
            detail="ao menos um entre humor, sintoma_principal e observacao deve estar preenchido",
        )

    for campo, valor in atualizacoes.items():
        setattr(registro, campo, valor)

    data_alvo = atualizacoes.get("data", registro.data)
    _commit_ou_conflito(db, usuario_atual.id, data_alvo, excluir_id=registro_id)
    db.refresh(registro)

    return registro


@router.delete(
    "/{registro_id}",
    status_code=status.HTTP_204_NO_CONTENT,
    summary="Excluir um registro diário",
    response_description="Registro excluído (sem conteúdo)",
    responses={
        401: {"description": "Token ausente, inválido ou expirado"},
        404: {"description": "Registro inexistente ou pertencente a outra usuária"},
    },
    description=(
        "Requer autenticação. Se o `id` não existir ou pertencer a outra usuária, "
        "retorna 404 em ambos os casos."
    ),
)
def excluir_registro(
    registro_id: int,
    usuario_atual: Usuario = Depends(get_current_usuario),
    db: Session = Depends(get_db),
):
    registro = _buscar_registro_ou_404(db, registro_id, usuario_atual.id)
    db.delete(registro)
    db.commit()
