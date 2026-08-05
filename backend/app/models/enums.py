import enum


class HumorEnum(str, enum.Enum):
    BEM = "Bem"
    CANSADA = "Cansada"
    TRISTE = "Triste"
    IRRITADA = "Irritada"


class SintomaEnum(str, enum.Enum):
    COLICAS_E_DOR = "Cólicas e dor"
    ALTERACOES_DE_HUMOR = "Alterações de humor"
    CANSACO = "Cansaço"
