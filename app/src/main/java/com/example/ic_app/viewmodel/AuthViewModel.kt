package com.example.ic_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ic_app.data.local.SessaoDataStore
import com.example.ic_app.data.remote.RetrofitClient
import com.example.ic_app.data.remote.dto.ErroDetalhe
import com.example.ic_app.data.remote.dto.LoginRequest
import com.example.ic_app.data.remote.dto.UsuarioCreateRequest
import com.example.ic_app.data.remote.dto.UsuarioResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Carregando : AuthUiState
    data object Sucesso : AuthUiState
    data class Erro(val mensagem: String) : AuthUiState
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val api = RetrofitClient.authApi
    private val sessaoDataStore = SessaoDataStore(application)
    private val gson = Gson()

    private val _estado = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val estado: StateFlow<AuthUiState> = _estado

    fun login(email: String, senha: String) {
        _estado.value = AuthUiState.Carregando
        viewModelScope.launch {
            try {
                val resposta = api.login(LoginRequest(email = email, senha = senha))
                if (resposta.isSuccessful) {
                    val token = resposta.body()?.accessToken
                    if (token != null) {
                        sessaoDataStore.salvarToken(token)
                        _estado.value = AuthUiState.Sucesso
                    } else {
                        _estado.value = AuthUiState.Erro("Resposta inválida do servidor")
                    }
                } else {
                    _estado.value = AuthUiState.Erro(extrairMensagemDeErro(resposta))
                }
            } catch (erro: IOException) {
                _estado.value = AuthUiState.Erro("Não foi possível conectar ao servidor")
            }
        }
    }

    fun registrar(email: String, senha: String, nome: String) {
        _estado.value = AuthUiState.Carregando
        viewModelScope.launch {
            try {
                val resposta = api.registrar(
                    UsuarioCreateRequest(email = email, senha = senha, nome = nome)
                )
                if (resposta.isSuccessful) {
                    // POST /auth/registrar não devolve token — encadeia um login com as
                    // mesmas credenciais para manter o comportamento já existente com o
                    // Firebase (usuária continua logada após criar a conta).
                    login(email, senha)
                } else {
                    _estado.value = AuthUiState.Erro(extrairMensagemDeErro(resposta))
                }
            } catch (erro: IOException) {
                _estado.value = AuthUiState.Erro("Não foi possível conectar ao servidor")
            }
        }
    }

    /**
     * Busca o usuário autenticado em GET /auth/me usando o token salvo no
     * DataStore. Existe só para comprovar, na prática, que o JWT armazenado
     * após login/registro é aceito pelo backend em uma rota protegida
     * (evidência de autenticação ponta a ponta) — não é chamada por nenhuma
     * tela hoje.
     */
    suspend fun buscarUsuarioAutenticado(): UsuarioResponse? {
        val token = sessaoDataStore.tokenFlow.first() ?: return null
        val resposta = api.me("Bearer $token")
        return if (resposta.isSuccessful) resposta.body() else null
    }

    private fun extrairMensagemDeErro(resposta: Response<*>): String {
        val corpo = resposta.errorBody()?.string()
        return try {
            gson.fromJson(corpo, ErroDetalhe::class.java)?.detail
                ?: "Erro inesperado (${resposta.code()})"
        } catch (erro: Exception) {
            "Erro inesperado (${resposta.code()})"
        }
    }
}
