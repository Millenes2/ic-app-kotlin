package com.example.ic_app.data.remote

import com.example.ic_app.data.remote.dto.RegistroCicloCreateRequest
import com.example.ic_app.data.remote.dto.RegistroCicloResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RegistroCicloApi {

    @POST("registros-ciclo")
    suspend fun criar(
        @Header("Authorization") token: String,
        @Body dados: RegistroCicloCreateRequest
    ): Response<RegistroCicloResponse>
}
