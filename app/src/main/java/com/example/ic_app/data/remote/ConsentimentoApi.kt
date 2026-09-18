package com.example.ic_app.data.remote

import com.example.ic_app.data.remote.dto.ConsentimentoCreateRequest
import com.example.ic_app.data.remote.dto.ConsentimentoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ConsentimentoApi {

    // POST funciona como criação-ou-reafirmação no backend (201 na criação,
    // 200 ao reenviar a mesma versão).
    @POST("consentimentos")
    suspend fun registrar(@Body dados: ConsentimentoCreateRequest): Response<ConsentimentoResponse>
}
