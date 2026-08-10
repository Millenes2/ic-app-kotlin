package com.example.ic_app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Espelha POST /registros-diarios (app/schemas/registro_diario.py:RegistroDiarioCreate).
 * `data` no formato ISO "yyyy-MM-dd". `humor`/`sintomaPrincipal` usam os mesmos
 * textos de HumorEnum/SintomaEnum no backend (ex.: "Bem", "Cansaço"), que já são
 * os mesmos textos usados em RegistrarHojeScreen.kt.
 */
data class RegistroDiarioCreateRequest(
    val data: String,
    val humor: String?,
    @SerializedName("sintoma_principal") val sintomaPrincipal: String?,
    val observacao: String?
)

/** Espelha RegistroDiarioOut (app/schemas/registro_diario.py). */
data class RegistroDiarioResponse(
    val id: Int,
    @SerializedName("usuario_id") val usuarioId: Int,
    val data: String,
    val humor: String?,
    @SerializedName("sintoma_principal") val sintomaPrincipal: String?,
    val observacao: String?,
    @SerializedName("criado_em") val criadoEm: String
)
