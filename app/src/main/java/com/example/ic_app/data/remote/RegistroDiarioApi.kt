package com.example.ic_app.data.remote

import com.example.ic_app.data.remote.dto.RegistroDiarioCreateRequest
import com.example.ic_app.data.remote.dto.RegistroDiarioResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RegistroDiarioApi {

    @POST("registros-diarios")
    suspend fun criar(@Body dados: RegistroDiarioCreateRequest): Response<RegistroDiarioResponse>

    @GET("registros-diarios")
    suspend fun listar(): Response<List<RegistroDiarioResponse>>
}
