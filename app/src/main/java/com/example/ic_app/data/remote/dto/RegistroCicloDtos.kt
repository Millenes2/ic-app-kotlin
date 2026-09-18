package com.example.ic_app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Espelha POST /registros-ciclo (app/schemas/registro_ciclo.py:RegistroCicloCreate).
 * `data` no formato ISO "yyyy-MM-dd".
 */
data class RegistroCicloCreateRequest(
    val data: String,
    val menstruacao: Boolean,
    val observacao: String?
)

/** Espelha RegistroCicloOut (app/schemas/registro_ciclo.py). */
data class RegistroCicloResponse(
    val id: Int,
    @SerializedName("usuario_id") val usuarioId: Int,
    val data: String,
    val menstruacao: Boolean,
    @SerializedName("fase_calculada") val faseCalculada: String?,
    val observacao: String?,
    @SerializedName("criado_em") val criadoEm: String
)
