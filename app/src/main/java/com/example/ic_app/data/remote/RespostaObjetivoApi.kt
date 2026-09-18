package com.example.ic_app.data.remote

import com.example.ic_app.data.remote.dto.RespostaObjetivoCreateRequest
import com.example.ic_app.data.remote.dto.RespostaObjetivoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RespostaObjetivoApi {

    // POST funciona como upsert no backend (201 na criação, 200 na
    // atualização da mesma combinação usuario+objetivo+etapa).
    @POST("respostas-objetivo")
    suspend fun salvar(@Body dados: RespostaObjetivoCreateRequest): Response<RespostaObjetivoResponse>
}
