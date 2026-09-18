package com.example.ic_app.repository

import com.example.ic_app.data.local.SessaoDataStore
import com.example.ic_app.data.remote.RegistroDiarioApi
import com.example.ic_app.data.remote.RetrofitClient
import com.example.ic_app.data.remote.dto.RegistroDiarioCreateRequest
import kotlinx.coroutines.flow.first
import java.io.IOException

sealed interface RegistroDiarioResultado {
    data object Sucesso : RegistroDiarioResultado
    data class Erro(val mensagem: String) : RegistroDiarioResultado
}

/**
 * Fonte única de verdade para o registro diário: fala com [RegistroDiarioApi]
 * usando o token salvo em [SessaoDataStore]. Extraído de
 * RegistroDiarioViewModel (etapa 6 do plano, ver CLAUDE.md seção 11).
 */
class RegistroDiarioRepository(
    private val sessaoDataStore: SessaoDataStore,
    private val api: RegistroDiarioApi = RetrofitClient.registroDiarioApi
) {

    suspend fun salvar(
        data: String,
        humor: String?,
        sintomaPrincipal: String?,
        observacao: String?
    ): RegistroDiarioResultado {
        val token = sessaoDataStore.tokenFlow.first()
            ?: return RegistroDiarioResultado.Erro("Sessão expirada, faça login novamente")

        return try {
            val resposta = api.criar(
                "Bearer $token",
                RegistroDiarioCreateRequest(
                    data = data,
                    humor = humor,
                    sintomaPrincipal = sintomaPrincipal,
                    observacao = observacao
                )
            )
            if (resposta.isSuccessful) {
                RegistroDiarioResultado.Sucesso
            } else {
                RegistroDiarioResultado.Erro(extrairMensagemDeErro(resposta))
            }
        } catch (erro: IOException) {
            RegistroDiarioResultado.Erro("Não foi possível conectar ao servidor")
        }
    }
}
