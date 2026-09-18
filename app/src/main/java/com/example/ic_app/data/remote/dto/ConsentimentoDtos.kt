package com.example.ic_app.data.remote.dto

import com.google.gson.annotations.SerializedName

/** Espelha POST /consentimentos (app/schemas/consentimento.py:ConsentimentoCreate). */
data class ConsentimentoCreateRequest(
    @SerializedName("versao_termos") val versaoTermos: String
)

/** Espelha ConsentimentoOut (app/schemas/consentimento.py). */
data class ConsentimentoResponse(
    val id: Int,
    @SerializedName("usuario_id") val usuarioId: Int,
    @SerializedName("versao_termos") val versaoTermos: String,
    @SerializedName("aceito_em") val aceitoEm: String
)
