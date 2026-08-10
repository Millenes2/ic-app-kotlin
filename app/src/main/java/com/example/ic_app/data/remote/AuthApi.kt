package com.example.ic_app.data.remote

import com.example.ic_app.data.remote.dto.LoginRequest
import com.example.ic_app.data.remote.dto.Token
import com.example.ic_app.data.remote.dto.UsuarioCreateRequest
import com.example.ic_app.data.remote.dto.UsuarioResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/registrar")
    suspend fun registrar(@Body dados: UsuarioCreateRequest): Response<UsuarioResponse>

    @POST("auth/login")
    suspend fun login(@Body dados: LoginRequest): Response<Token>

    // Rota protegida usada só para comprovar que o token JWT salvo no DataStore
    // é aceito pelo backend (ver AuthViewModel.buscarUsuarioAutenticado).
    @GET("auth/me")
    suspend fun me(@Header("Authorization") token: String): Response<UsuarioResponse>
}
