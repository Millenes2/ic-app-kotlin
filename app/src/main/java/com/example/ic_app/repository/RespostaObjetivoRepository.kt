package com.example.ic_app.repository

import com.example.ic_app.data.remote.RespostaObjetivoApi
import com.example.ic_app.data.remote.RetrofitClient
import com.example.ic_app.data.remote.dto.RespostaObjetivoCreateRequest
import java.io.IOException

sealed interface RespostaObjetivoResultado {
    data object Sucesso : RespostaObjetivoResultado
    data class Erro(val mensagem: String) : RespostaObjetivoResultado
}

/**
 * Fonte única de verdade para respostas de objetivo: fala com
 * [RespostaObjetivoApi] (o header Authorization é anexado automaticamente
 * por RetrofitClient, quando há sessão ativa).
 *
 * As telas `objetivos/<objetivo>/Screen1..3` são exibidas durante o
 * onboarding, antes de qualquer login/cadastro (ver AppScreen.kt: o fluxo
 * vai direto de `objetivo` para os sub-fluxos, sem passar por `login`/
 * `criar`) — hoje, sem uma sessão ativa, a requisição segue sem o header e o
 * backend responde 401, então [salvar] sempre retorna
 * [RespostaObjetivoResultado.Erro]. A chamada é feita de forma "melhor
 * esforço" pelo chamador (ver `RespostaObjetivoViewModel`): a navegação entre
 * etapas não é bloqueada por essa persistência, do mesmo jeito que hoje não é
 * bloqueada por nada. Isso passa a funcionar de fato assim que o fluxo de
 * autenticação vier antes do onboarding — mudança de navegação fora do
 * escopo desta integração.
 */
class RespostaObjetivoRepository(
    private val api: RespostaObjetivoApi = RetrofitClient.respostaObjetivoApi
) {

    suspend fun salvar(objetivo: String, etapa: Int, opcaoSelecionada: String): RespostaObjetivoResultado {
        return try {
            val resposta = api.salvar(
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
