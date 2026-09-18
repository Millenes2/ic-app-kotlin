package com.example.ic_app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Espelha PATCH /perfil (app/schemas/usuario.py:UsuarioPerfilUpdate). Todos os
 * campos são opcionais — campos ausentes (null aqui) permanecem inalterados
 * no backend (`exclude_unset` no router), então só incluímos no corpo os
 * campos que a tela realmente quer atualizar (ver PerfilRepository).
 * `dataNascimento` no formato ISO "yyyy-MM-dd".
 */
data class PerfilUpdateRequest(
    val nome: String? = null,
    @SerializedName("data_nascimento") val dataNascimento: String? = null,
    val peso: Double? = null,
    @SerializedName("objetivo_atual") val objetivoAtual: String? = null
)
