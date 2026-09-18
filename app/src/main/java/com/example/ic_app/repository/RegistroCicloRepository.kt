package com.example.ic_app.repository

import com.example.ic_app.data.remote.RegistroCicloApi
import com.example.ic_app.data.remote.RetrofitClient
import com.example.ic_app.data.remote.dto.RegistroCicloCreateRequest
import java.io.IOException

sealed interface RegistroCicloResultado {
    data object Sucesso : RegistroCicloResultado
    data class Erro(val mensagem: String) : RegistroCicloResultado
}

/**
 * Fonte única de verdade para o registro de ciclo: fala com
 * [RegistroCicloApi] (o header Authorization é anexado automaticamente por
 * RetrofitClient).
 *
 * `CalendarioScreen.kt` hoje exibe uma semana fixa fictícia (ver CLAUDE.md
 * seção 4) sem correspondência real com o calendário do dispositivo; por
 * isso, o registro salvo aqui sempre usa a data de hoje (mesmo padrão já
 * usado em RegistroDiarioRepository), e não o "dia" fictício selecionado na
 * tela — corrigir a lógica de calendário é um achado próprio, fora do
 * escopo desta integração.
 */
class RegistroCicloRepository(
    private val api: RegistroCicloApi = RetrofitClient.registroCicloApi
) {

    suspend fun salvar(data: String, menstruacao: Boolean, observacao: String?): RegistroCicloResultado {
        return try {
            val resposta = api.criar(
                RegistroCicloCreateRequest(
                    data = data,
                    menstruacao = menstruacao,
                    observacao = observacao
                )
            )
            if (resposta.isSuccessful) {
                RegistroCicloResultado.Sucesso
            } else {
                RegistroCicloResultado.Erro(extrairMensagemDeErro(resposta))
            }
        } catch (erro: IOException) {
            RegistroCicloResultado.Erro("Não foi possível conectar ao servidor")
        }
    }
}
