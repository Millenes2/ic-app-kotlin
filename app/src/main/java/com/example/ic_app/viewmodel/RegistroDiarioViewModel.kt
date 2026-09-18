package com.example.ic_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ic_app.data.local.SessaoDataStore
import com.example.ic_app.repository.RegistroDiarioRepository
import com.example.ic_app.repository.RegistroDiarioResultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface RegistroDiarioUiState {
    data object Idle : RegistroDiarioUiState
    data object Carregando : RegistroDiarioUiState
    data object Sucesso : RegistroDiarioUiState
    data class Erro(val mensagem: String) : RegistroDiarioUiState
}

class RegistroDiarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RegistroDiarioRepository(sessaoDataStore = SessaoDataStore(application))

    private val _estado = MutableStateFlow<RegistroDiarioUiState>(RegistroDiarioUiState.Idle)
    val estado: StateFlow<RegistroDiarioUiState> = _estado

    fun salvar(data: String, humor: String?, sintomaPrincipal: String?, observacao: String?) {
        _estado.value = RegistroDiarioUiState.Carregando
        viewModelScope.launch {
            _estado.value = when (
                val resultado = repository.salvar(data, humor, sintomaPrincipal, observacao)
            ) {
                is RegistroDiarioResultado.Sucesso -> RegistroDiarioUiState.Sucesso
                is RegistroDiarioResultado.Erro -> RegistroDiarioUiState.Erro(resultado.mensagem)
            }
        }
    }
}
