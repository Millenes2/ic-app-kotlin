package com.example.ic_app.repository

import com.example.ic_app.data.remote.ConsentimentoApi
import com.example.ic_app.data.remote.RetrofitClient
import com.example.ic_app.data.remote.dto.ConsentimentoCreateRequest
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
 * (o header Authorization é anexado automaticamente por RetrofitClient,
 * quando há sessão ativa).
 *
 * Assim como em [RespostaObjetivoRepository], `ConsentScreen` é exibida no
 * início do onboarding, antes de qualquer login/cadastro — sem sessão ativa,
 * a requisição segue sem o header e o backend responde 401, então
 * [registrar] sempre retorna [ConsentimentoResultado.Erro]. A chamada é
 * "melhor esforço": não bloqueia o avanço do onboarding.
 */
class ConsentimentoRepository(
    private val api: ConsentimentoApi = RetrofitClient.consentimentoApi
) {

    suspend fun registrar(versaoTermos: String = VERSAO_TERMOS_ATUAL): ConsentimentoResultado {
        return try {
            val resposta = api.registrar(ConsentimentoCreateRequest(versaoTermos = versaoTermos))
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
