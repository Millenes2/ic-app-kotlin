package com.example.ic_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ic_app.data.local.SessaoDataStore
import com.example.ic_app.data.remote.RetrofitClient
import com.example.ic_app.data.remote.dto.ErroDetalhe
import com.example.ic_app.data.remote.dto.RegistroDiarioCreateRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

sealed interface RegistroDiarioUiState {
    data object Idle : RegistroDiarioUiState
    data object Carregando : RegistroDiarioUiState
    data object Sucesso : RegistroDiarioUiState
    data class Erro(val mensagem: String) : RegistroDiarioUiState
}

class RegistroDiarioViewModel(application: Application) : AndroidViewModel(application) {

    private val api = RetrofitClient.registroDiarioApi
    private val sessaoDataStore = SessaoDataStore(application)
    private val gson = Gson()

    private val _estado = MutableStateFlow<RegistroDiarioUiState>(RegistroDiarioUiState.Idle)
    val estado: StateFlow<RegistroDiarioUiState> = _estado

    fun salvar(data: String, humor: String?, sintomaPrincipal: String?, observacao: String?) {
        _estado.value = RegistroDiarioUiState.Carregando
        viewModelScope.launch {
            val token = sessaoDataStore.tokenFlow.first()
            if (token == null) {
                _estado.value = RegistroDiarioUiState.Erro("Sessão expirada, faça login novamente")
                return@launch
            }
            try {
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
                    _estado.value = RegistroDiarioUiState.Sucesso
                } else {
                    _estado.value = RegistroDiarioUiState.Erro(extrairMensagemDeErro(resposta))
                }
            } catch (erro: IOException) {
                _estado.value = RegistroDiarioUiState.Erro("Não foi possível conectar ao servidor")
            }
        }
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
