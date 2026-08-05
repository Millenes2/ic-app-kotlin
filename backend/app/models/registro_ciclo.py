from sqlalchemy import (
    Boolean,
    Column,
    Date,
    DateTime,
    ForeignKey,
    Integer,
    String,
    Text,
    UniqueConstraint,
)
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func

from app.db.base import Base


class RegistroCiclo(Base):
    __tablename__ = "registro_ciclo"
    __table_args__ = (
        UniqueConstraint("usuario_id", "data", name="uq_registro_ciclo_usuario_data"),
    )

    id = Column(Integer, primary_key=True, autoincrement=True)
    usuario_id = Column(Integer, ForeignKey("usuario.id"), nullable=False, index=True)
    data = Column(Date, nullable=False)
    menstruacao = Column(Boolean, nullable=False, default=False)
    fase_calculada = Column(String, nullable=True)
    observacao = Column(Text, nullable=True)
    criado_em = Column(DateTime(timezone=True), server_default=func.now())

    usuario = relationship("Usuario", back_populates="registros_ciclo")
