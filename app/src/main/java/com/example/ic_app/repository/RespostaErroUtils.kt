package com.example.ic_app.repository

import com.example.ic_app.data.remote.dto.ErroDetalhe
import com.google.gson.Gson
import retrofit2.Response

/**
 * Extrai a mensagem de erro do corpo de uma resposta HTTP não bem-sucedida,
 * no formato `{"detail": "..."}` usado pelo backend FastAPI. Compartilhado
 * entre os repositories para não duplicar esse parsing em cada um.
 */
fun extrairMensagemDeErro(resposta: Response<*>, gson: Gson = Gson()): String {
    val corpo = resposta.errorBody()?.string()
    return try {
        gson.fromJson(corpo, ErroDetalhe::class.java)?.detail
            ?: "Erro inesperado (${resposta.code()})"
    } catch (erro: Exception) {
        "Erro inesperado (${resposta.code()})"
    }
}
