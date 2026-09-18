package com.example.ic_app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Espelha POST /respostas-objetivo (app/schemas/resposta_objetivo.py:RespostaObjetivoCreate).
 * `objetivo` usa os mesmos textos de OBJETIVOS_VALIDOS no backend, que são os
 * mesmos textos dos cards em ObjetivoScreen.kt (ver AppScreen.kt: objetivoUsuario).
 */
data class RespostaObjetivoCreateRequest(
    val objetivo: String,
    val etapa: Int,
    @SerializedName("opcao_selecionada") val opcaoSelecionada: String
)

/** Espelha RespostaObjetivoOut (app/schemas/resposta_objetivo.py). */
data class RespostaObjetivoResponse(
    val id: Int,
    @SerializedName("usuario_id") val usuarioId: Int,
    val objetivo: String,
    val etapa: Int,
    @SerializedName("opcao_selecionada") val opcaoSelecionada: String,
    @SerializedName("criado_em") val criadoEm: String
)
