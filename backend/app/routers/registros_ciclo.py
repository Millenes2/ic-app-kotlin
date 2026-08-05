from datetime import date
from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

from app.db.session import get_db
from app.dependencies import get_current_usuario
from app.models import RegistroCiclo, Usuario
from app.schemas.registro_ciclo import (
    RegistroCicloCreate,
    RegistroCicloOut,
    RegistroCicloUpdate,
)

router = APIRouter(prefix="/registros-ciclo", tags=["Registros do Ciclo"])


def _buscar_registro_ou_404(db: Session, registro_id: int, usuario_id: int) -> RegistroCiclo:
    registro = (
        db.query(RegistroCiclo)
        .filter(RegistroCiclo.id == registro_id, RegistroCiclo.usuario_id == usuario_id)
        .first()
    )
    if registro is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Registro de ciclo não encontrado",
        )
    return registro


def _buscar_registro_na_data(
    db: Session, usuario_id: int, data_registro: date, excluir_id: Optional[int] = None
) -> Optional[RegistroCiclo]:
    query = db.query(RegistroCiclo).filter(
        RegistroCiclo.usuario_id == usuario_id,
        RegistroCiclo.data == data_registro,
    )
    if excluir_id is not None:
        query = query.filter(RegistroCiclo.id != excluir_id)
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
        detalhe = "Já existe um registro de ciclo para esta data."
        if conflito is not None:
            detalhe += f" Use PATCH /registros-ciclo/{conflito.id} para atualizá-lo."
        raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail=detalhe)


@router.post(
    "",
    response_model=RegistroCicloOut,
    status_code=status.HTTP_201_CREATED,
    summary="Criar registro de ciclo",
    response_description="Registro de ciclo criado",
    responses={
        401: {"description": "Token ausente, inválido ou expirado"},
        409: {"description": "Já existe um registro de ciclo do usuário para essa data; use PATCH para atualizá-lo"},
        422: {"description": "Dados inválidos: data no futuro, menstruacao ausente/não booleana, ou observacao só com espaços"},
    },
    description=(
        "Cria um registro de menstruação/observação para uma data. Regra de "
        "unicidade: no máximo um `RegistroCiclo` por usuária e por data — reenviar "
        "a mesma data retorna 409 com orientação para usar `PATCH`. `data` não pode "
        "ser futura. `menstruacao` é obrigatório. Não há cálculo de fase ou previsão "
        "nesta etapa (`fase_calculada` é sempre `null`)."
    ),
)
def criar_registro(
    dados: RegistroCicloCreate,
    usuario_atual: Usuario = Depends(get_current_usuario),
    db: Session = Depends(get_db),
):
    existente = _buscar_registro_na_data(db, usuario_atual.id, dados.data)
    if existente is not None:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=(
                "Já existe um registro de ciclo para esta data. "
                f"Use PATCH /registros-ciclo/{existente.id} para atualizá-lo."
            ),
        )

    registro = RegistroCiclo(usuario_id=usuario_atual.id, **dados.model_dump())
    db.add(registro)
    _commit_ou_conflito(db, usuario_atual.id, dados.data)
    db.refresh(registro)

    return registro


@router.get(
    "",
    response_model=List[RegistroCicloOut],
    summary="Listar registros de ciclo",
    response_description="Registros de ciclo da usuária, do mais recente ao mais antigo",
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

    query = db.query(RegistroCiclo).filter(RegistroCiclo.usuario_id == usuario_atual.id)

    if data_inicio is not None:
        query = query.filter(RegistroCiclo.data >= data_inicio)
    if data_fim is not None:
        query = query.filter(RegistroCiclo.data <= data_fim)

    return query.order_by(RegistroCiclo.data.desc()).all()


@router.get(
    "/{registro_id}",
    response_model=RegistroCicloOut,
    summary="Obter um registro de ciclo",
    response_description="Registro de ciclo encontrado",
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
    response_model=RegistroCicloOut,
    summary="Atualizar um registro de ciclo",
    response_description="Registro de ciclo atualizado",
    responses={
        401: {"description": "Token ausente, inválido ou expirado"},
        404: {"description": "Registro inexistente ou pertencente a outra usuária"},
        409: {"description": "A nova data já possui um registro do usuário"},
        422: {"description": "Dados inválidos: data no futuro ou null, menstruacao null, ou observacao só com espaços"},
    },
    description=(
        "Atualização parcial (`PATCH`) — campos ausentes permanecem inalterados. "
        "`data` e `menstruacao` não podem ser `null`; `data` pode ser trocada (409 "
        "se a nova data já tiver um registro do usuário). `observacao` pode ser "
        "limpa com `null`, por ser opcional."
    ),
)
def atualizar_registro(
    registro_id: int,
    dados: RegistroCicloUpdate,
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
                detail="Já existe um registro de ciclo para esta data",
            )

    for campo, valor in atualizacoes.items():
        setattr(registro, campo, valor)

    # Estado final após a atualização: `data` continua obrigatória e não
    # futura, e `menstruacao` continua um booleano válido — ambos garantidos
    # pelos validators do schema, que rejeitam `null` nesses dois campos.
    # Não há aqui um invariante equivalente ao "ao menos um campo preenchido"
    # do registro diário, pois `observacao` é opcional por natureza.

    data_alvo = atualizacoes.get("data", registro.data)
    _commit_ou_conflito(db, usuario_atual.id, data_alvo, excluir_id=registro_id)
    db.refresh(registro)

    return registro


@router.delete(
    "/{registro_id}",
    status_code=status.HTTP_204_NO_CONTENT,
    summary="Excluir um registro de ciclo",
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
