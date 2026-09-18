package com.example.ic_app.repository

import com.example.ic_app.data.local.SessaoDataStore
import com.example.ic_app.data.remote.ConsentimentoApi
import com.example.ic_app.data.remote.RetrofitClient
import com.example.ic_app.data.remote.dto.ConsentimentoCreateRequest
import kotlinx.coroutines.flow.first
import java.io.IOException

/**
 * Versão atual dos termos exibidos em `ConsentScreen.kt`. Não existe, até
 * aqui, nenhum identificador de versão no Android (ver CLAUDE.md seção 7) —
 * este é o primeiro valor adotado, seguindo o mesmo formato do exemplo em
 * `ConsentimentoCreate` no backend ("1.0"). Deve ser incrementada se o texto
 * do diálogo de política de dados em `ConsentScreen.kt` mudar de forma
 * relevante.
 */
const val VERSAO_TERMOS_ATUAL = "1.0"

sealed interface ConsentimentoResultado {
    data object Sucesso : ConsentimentoResultado
    data class Erro(val mensagem: String) : ConsentimentoResultado
}

/**
 * Fonte única de verdade para o consentimento: fala com [ConsentimentoApi]
 * usando o token salvo em [SessaoDataStore].
 *
 * Assim como em [RespostaObjetivoRepository], `ConsentScreen` é exibida no
 * início do onboarding, antes de qualquer login/cadastro — sem sessão ativa,
 * [registrar] sempre retorna [ConsentimentoResultado.Erro]. A chamada é
 * "melhor esforço": não bloqueia o avanço do onboarding.
 */
class ConsentimentoRepository(
    private val sessaoDataStore: SessaoDataStore,
    private val api: ConsentimentoApi = RetrofitClient.consentimentoApi
) {

    suspend fun registrar(versaoTermos: String = VERSAO_TERMOS_ATUAL): ConsentimentoResultado {
        val token = sessaoDataStore.tokenFlow.first()
            ?: return ConsentimentoResultado.Erro("Sessão expirada, faça login novamente")

        return try {
            val resposta = api.registrar(
                "Bearer $token",
                ConsentimentoCreateRequest(versaoTermos = versaoTermos)
            )
            if (resposta.isSuccessful) {
                ConsentimentoResultado.Sucesso
            } else {
                ConsentimentoResultado.Erro(extrairMensagemDeErro(resposta))
            }
        } catch (erro: IOException) {
            ConsentimentoResultado.Erro("Não foi possível conectar ao servidor")
        }
    }
}
