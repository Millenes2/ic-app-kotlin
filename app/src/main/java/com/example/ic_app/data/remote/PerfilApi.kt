package com.example.ic_app.data.remote

import com.example.ic_app.data.remote.dto.PerfilUpdateRequest
import com.example.ic_app.data.remote.dto.UsuarioResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH

interface PerfilApi {

    @GET("perfil")
    suspend fun buscar(@Header("Authorization") token: String): Response<UsuarioResponse>

    @PATCH("perfil")
    suspend fun atualizar(
        @Header("Authorization") token: String,
        @Body dados: PerfilUpdateRequest
    ): Response<UsuarioResponse>
}
