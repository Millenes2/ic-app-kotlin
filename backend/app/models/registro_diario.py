from sqlalchemy import Column, Date, DateTime, ForeignKey, Integer, Text, UniqueConstraint
from sqlalchemy import Enum as SqlEnum
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func

from app.db.base import Base
from app.models.enums import HumorEnum, SintomaEnum


class RegistroDiario(Base):
    __tablename__ = "registro_diario"
    __table_args__ = (
        UniqueConstraint("usuario_id", "data", name="uq_registro_diario_usuario_data"),
    )

    id = Column(Integer, primary_key=True, autoincrement=True)
    usuario_id = Column(Integer, ForeignKey("usuario.id"), nullable=False, index=True)
    data = Column(Date, nullable=False)
    humor = Column(SqlEnum(HumorEnum), nullable=True)
    sintoma_principal = Column(SqlEnum(SintomaEnum), nullable=True)
    observacao = Column(Text, nullable=True)
    criado_em = Column(DateTime(timezone=True), server_default=func.now())

    usuario = relationship("Usuario", back_populates="registros_diarios")
