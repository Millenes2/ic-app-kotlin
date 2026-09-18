package com.example.ic_app.repository

import com.example.ic_app.data.local.SessaoDataStore
import com.example.ic_app.data.remote.PerfilApi
import com.example.ic_app.data.remote.RetrofitClient
import com.example.ic_app.data.remote.dto.PerfilUpdateRequest
import com.example.ic_app.data.remote.dto.UsuarioResponse
import kotlinx.coroutines.flow.first
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

sealed interface PerfilResultado {
    data class Sucesso(val usuario: UsuarioResponse) : PerfilResultado
    data class Erro(val mensagem: String) : PerfilResultado
}

/**
 * Fonte única de verdade para o perfil: fala com [PerfilApi] usando o token
 * salvo em [SessaoDataStore]. `nomeUsuario`/`dataNascimentoUsuario`/
 * `pesoUsuario` em AppScreen.kt continuam sendo o estado local exibido antes
 * do primeiro carregamento (ver PerfilScreen.kt) — este repository só entra
 * em cena quando há sessão ativa.
 */
class PerfilRepository(
    private val sessaoDataStore: SessaoDataStore,
    private val api: PerfilApi = RetrofitClient.perfilApi
) {
    private val formatoTelaBr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        isLenient = false
    }
    private val formatoBackendIso = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    suspend fun buscar(): UsuarioResponse? {
        val token = sessaoDataStore.tokenFlow.first() ?: return null
        val resposta = api.buscar("Bearer $token")
        return if (resposta.isSuccessful) resposta.body() else null
    }

    /**
     * [dataNascimento] é esperada no formato "dd/MM/yyyy" (mesmo formato de
     * `DataNascimentoScreen.kt`/`dataNascimentoUsuario`); [peso] aceita vírgula
     * ou ponto decimal (mesmo formato de `PesoScreen.kt`/`pesoUsuario`). Campos
     * em branco são omitidos do PATCH (não alteram o valor já salvo); campos
     * inválidos retornam erro sem chamar a API.
     */
    suspend fun salvar(
        nome: String,
        dataNascimento: String,
        peso: String,
        objetivoAtual: String
    ): PerfilResultado {
        val token = sessaoDataStore.tokenFlow.first()
            ?: return PerfilResultado.Erro("Sessão expirada, faça login novamente")

        val dataIso = if (dataNascimento.isBlank()) {
            null
        } else {
            try {
                formatoBackendIso.format(formatoTelaBr.parse(dataNascimento)!!)
            } catch (erro: Exception) {
                return PerfilResultado.Erro("Data de nascimento inválida (use dd/mm/aaaa)")
            }
        }

        val pesoDouble = if (peso.isBlank()) {
            null
        } else {
            peso.replace(',', '.').toDoubleOrNull()
                ?: return PerfilResultado.Erro("Peso inválido")
        }

        return try {
            val resposta = api.atualizar(
                "Bearer $token",
                PerfilUpdateRequest(
                    nome = nome.ifBlank { null },
                    dataNascimento = dataIso,
                    peso = pesoDouble,
                    objetivoAtual = objetivoAtual.ifBlank { null }
                )
            )
            if (resposta.isSuccessful && resposta.body() != null) {
                PerfilResultado.Sucesso(resposta.body()!!)
            } else {
                PerfilResultado.Erro(extrairMensagemDeErro(resposta))
            }
        } catch (erro: IOException) {
            PerfilResultado.Erro("Não foi possível conectar ao servidor")
        }
    }
}
