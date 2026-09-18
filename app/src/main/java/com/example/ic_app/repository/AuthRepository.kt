package com.example.ic_app.repository

import com.example.ic_app.data.local.SessaoDataStore
import com.example.ic_app.data.remote.AuthApi
import com.example.ic_app.data.remote.RetrofitClient
import com.example.ic_app.data.remote.dto.LoginRequest
import com.example.ic_app.data.remote.dto.UsuarioCreateRequest
import com.example.ic_app.data.remote.dto.UsuarioResponse
import java.io.IOException

sealed interface AuthResultado {
    data object Sucesso : AuthResultado
    data class Erro(val mensagem: String) : AuthResultado
}

/**
 * Fonte única de verdade para autenticação: fala com [AuthApi] e persiste o
 * token de sessão em [SessaoDataStore]. Extraído de AuthViewModel (etapa 6 do
 * plano, ver CLAUDE.md seção 11) para que o ViewModel só cuide de estado de
 * UI, sem conhecer detalhes de Retrofit/DataStore.
 */
class AuthRepository(
    private val sessaoDataStore: SessaoDataStore,
    private val api: AuthApi = RetrofitClient.authApi
) {

    suspend fun login(email: String, senha: String): AuthResultado {
        return try {
            val resposta = api.login(LoginRequest(email = email, senha = senha))
            if (resposta.isSuccessful) {
                val token = resposta.body()?.accessToken
                if (token != null) {
                    sessaoDataStore.salvarToken(token)
                    AuthResultado.Sucesso
                } else {
                    AuthResultado.Erro("Resposta inválida do servidor")
                }
            } else {
                AuthResultado.Erro(extrairMensagemDeErro(resposta))
            }
        } catch (erro: IOException) {
            AuthResultado.Erro("Não foi possível conectar ao servidor")
        }
    }

    suspend fun registrar(email: String, senha: String, nome: String): AuthResultado {
        return try {
            val resposta = api.registrar(
                UsuarioCreateRequest(email = email, senha = senha, nome = nome)
            )
            if (resposta.isSuccessful) {
                // POST /auth/registrar não devolve token — encadeia um login com as
                // mesmas credenciais para manter o comportamento já existente com o
                // Firebase (usuária continua logada após criar a conta).
                login(email, senha)
            } else {
                AuthResultado.Erro(extrairMensagemDeErro(resposta))
            }
        } catch (erro: IOException) {
            AuthResultado.Erro("Não foi possível conectar ao servidor")
        }
    }

    /**
     * Busca o usuário autenticado em GET /auth/me — o header Authorization é
     * anexado automaticamente por RetrofitClient a partir do token salvo no
     * DataStore. Existe só para comprovar, na prática, que o JWT armazenado
     * após login/registro é aceito pelo backend em uma rota protegida
     * (evidência de autenticação ponta a ponta) — não é chamada por nenhuma
     * tela hoje.
     */
    suspend fun buscarUsuarioAutenticado(): UsuarioResponse? {
        val resposta = api.me()
        return if (resposta.isSuccessful) resposta.body() else null
    }
}
