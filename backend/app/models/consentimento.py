from sqlalchemy import Column, DateTime, ForeignKey, Integer, String, UniqueConstraint
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func

from app.db.base import Base


class Consentimento(Base):
    __tablename__ = "consentimento"
    __table_args__ = (
        UniqueConstraint("usuario_id", "versao_termos", name="uq_consentimento_usuario_versao"),
    )

    id = Column(Integer, primary_key=True, autoincrement=True)
    usuario_id = Column(Integer, ForeignKey("usuario.id"), nullable=False, index=True)
    aceito_em = Column(DateTime(timezone=True), server_default=func.now())
    versao_termos = Column(String, nullable=False)

    usuario = relationship("Usuario", back_populates="consentimentos")
