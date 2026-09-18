package com.example.ic_app.repository

import com.example.ic_app.data.local.SessaoDataStore
import com.example.ic_app.data.remote.RespostaObjetivoApi
import com.example.ic_app.data.remote.RetrofitClient
import com.example.ic_app.data.remote.dto.RespostaObjetivoCreateRequest
import kotlinx.coroutines.flow.first
import java.io.IOException

sealed interface RespostaObjetivoResultado {
    data object Sucesso : RespostaObjetivoResultado
    data class Erro(val mensagem: String) : RespostaObjetivoResultado
}

/**
 * Fonte única de verdade para respostas de objetivo: fala com
 * [RespostaObjetivoApi] usando o token salvo em [SessaoDataStore].
 *
 * As telas `objetivos/<objetivo>/Screen1..3` são exibidas durante o
 * onboarding, antes de qualquer login/cadastro (ver AppScreen.kt: o fluxo
 * vai direto de `objetivo` para os sub-fluxos, sem passar por `login`/
 * `criar`) — hoje, sem uma sessão ativa, [salvar] sempre retorna
 * [RespostaObjetivoResultado.Erro] com "sessão expirada". A chamada é feita
 * de forma "melhor esforço" pelo chamador (ver `RespostaObjetivoViewModel`):
 * a navegação entre etapas não é bloqueada por essa persistência, do mesmo
 * jeito que hoje não é bloqueada por nada. Isso passa a funcionar de fato
 * assim que o fluxo de autenticação vier antes do onboarding — mudança de
 * navegação fora do escopo desta integração.
 */
class RespostaObjetivoRepository(
    private val sessaoDataStore: SessaoDataStore,
    private val api: RespostaObjetivoApi = RetrofitClient.respostaObjetivoApi
) {

    suspend fun salvar(objetivo: String, etapa: Int, opcaoSelecionada: String): RespostaObjetivoResultado {
        val token = sessaoDataStore.tokenFlow.first()
            ?: return RespostaObjetivoResultado.Erro("Sessão expirada, faça login novamente")

        return try {
            val resposta = api.salvar(
                "Bearer $token",
                RespostaObjetivoCreateRequest(
                    objetivo = objetivo,
                    etapa = etapa,
                    opcaoSelecionada = opcaoSelecionada
                )
            )
            if (resposta.isSuccessful) {
                RespostaObjetivoResultado.Sucesso
            } else {
                RespostaObjetivoResultado.Erro(extrairMensagemDeErro(resposta))
            }
        } catch (erro: IOException) {
            RespostaObjetivoResultado.Erro("Não foi possível conectar ao servidor")
        }
    }
}
