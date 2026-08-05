from sqlalchemy import Column, DateTime, ForeignKey, Integer, String, UniqueConstraint
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func

from app.db.base import Base


class RespostaObjetivo(Base):
    __tablename__ = "resposta_objetivo"
    __table_args__ = (
        UniqueConstraint(
            "usuario_id", "objetivo", "etapa", name="uq_resposta_objetivo_usuario_objetivo_etapa"
        ),
    )

    id = Column(Integer, primary_key=True, autoincrement=True)
    usuario_id = Column(Integer, ForeignKey("usuario.id"), nullable=False, index=True)
    objetivo = Column(String, nullable=False)
    etapa = Column(Integer, nullable=False)
    opcao_selecionada = Column(String, nullable=False)
    criado_em = Column(DateTime(timezone=True), server_default=func.now())

    usuario = relationship("Usuario", back_populates="respostas_objetivo")
