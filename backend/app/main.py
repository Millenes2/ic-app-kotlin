from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.routers import (
    auth,
    consentimentos,
    perfil,
    registros_ciclo,
    registros_diarios,
    respostas_objetivo,
)

TAGS_METADATA = [
    {
        "name": "Autenticação",
        "description": "Cadastro, login e emissão do token JWT usado por todas as demais rotas.",
    },
    {
        "name": "Perfil",
        "description": "Leitura e atualização parcial dos dados de perfil da própria usuária autenticada.",
    },
    {
        "name": "Registros Diários",
        "description": (
            "Humor, sintoma e observação do dia. No máximo um registro por usuária e por data."
        ),
    },
    {
        "name": "Registros do Ciclo",
        "description": (
            "Menstruação e observação do dia, para acompanhamento do ciclo. No máximo um "
            "registro por usuária e por data. Não inclui cálculo de fase ou previsão."
        ),
    },
    {
        "name": "Respostas dos Objetivos",
        "description": (
            "Opções escolhidas nas telas dos sub-fluxos de onboarding por objetivo. "
            "No máximo uma resposta por usuária, objetivo e etapa."
        ),
    },
    {
        "name": "Consentimentos",
        "description": "Aceite da versão dos termos de uso pela usuária autenticada.",
    },
    {
        "name": "Sistema",
        "description": "Rotas de verificação da API, sem autenticação.",
    },
]

app = FastAPI(
    title="Luna API",
    description=(
        "Backend do aplicativo Luna (acompanhamento do ciclo menstrual e da saúde "
        "feminina). Toda rota protegida exige um token JWT obtido em `POST "
        "/auth/login`, enviado no cabeçalho `Authorization: Bearer <token>`."
    ),
    version="0.1.0",
    openapi_tags=TAGS_METADATA,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth.router)
app.include_router(perfil.router)
app.include_router(registros_diarios.router)
app.include_router(registros_ciclo.router)
app.include_router(respostas_objetivo.router)
app.include_router(consentimentos.router)


@app.get(
    "/",
    tags=["Sistema"],
    summary="Mensagem de boas-vindas",
    response_description="Mensagem de boas-vindas da API",
)
def home():
    return {
        "mensagem": "Bem-vindo à API do Luna!"
    }


@app.get(
    "/health",
    tags=["Sistema"],
    summary="Verificação de disponibilidade",
    response_description="API disponível",
)
def health():
    return {
        "status": "ok"
    }