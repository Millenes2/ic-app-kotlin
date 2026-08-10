package com.example.ic_app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UsuarioCreateRequest(
    val email: String,
    val senha: String,
    val nome: String
)

data class LoginRequest(
    val email: String,
    val senha: String
)

data class Token(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String
)

data class UsuarioResponse(
    val id: Int,
    val email: String,
    val nome: String,
    @SerializedName("data_nascimento") val dataNascimento: String?,
    val peso: Double?,
    @SerializedName("objetivo_atual") val objetivoAtual: String?,
    @SerializedName("criado_em") val criadoEm: String
)

/**
 * Corpo de erro de POST /auth/registrar, POST /auth/login e GET /auth/me em respostas
 * 401/409. [detail] é sempre uma string nesses casos — o formato de lista de objetos
 * (validação 422) não é tratado aqui, pois as telas atuais só validam client-side antes
 * de chamar a API.
 */
data class ErroDetalhe(
    val detail: String
)
