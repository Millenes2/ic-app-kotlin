from sqlalchemy import Column, Date, DateTime, Float, Integer, String
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func

from app.db.base import Base


class Usuario(Base):
    __tablename__ = "usuario"

    id = Column(Integer, primary_key=True, autoincrement=True)
    email = Column(String, unique=True, nullable=False, index=True)
    senha_hash = Column(String, nullable=False)
    nome = Column(String, nullable=False)
    data_nascimento = Column(Date, nullable=True)
    peso = Column(Float, nullable=True)
    objetivo_atual = Column(String, nullable=True)
    criado_em = Column(DateTime(timezone=True), server_default=func.now())

    consentimentos = relationship("Consentimento", back_populates="usuario")
    registros_diarios = relationship("RegistroDiario", back_populates="usuario")
    registros_ciclo = relationship("RegistroCiclo", back_populates="usuario")
    respostas_objetivo = relationship("RespostaObjetivo", back_populates="usuario")
