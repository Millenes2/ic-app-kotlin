package com.example.ic_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ic_app.data.local.SessaoDataStore
import com.example.ic_app.repository.RegistroCicloRepository
import com.example.ic_app.repository.RegistroCicloResultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface RegistroCicloUiState {
    data object Idle : RegistroCicloUiState
    data object Carregando : RegistroCicloUiState
    data object Sucesso : RegistroCicloUiState
    data class Erro(val mensagem: String) : RegistroCicloUiState
}

class RegistroCicloViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RegistroCicloRepository(sessaoDataStore = SessaoDataStore(application))

    private val _estado = MutableStateFlow<RegistroCicloUiState>(RegistroCicloUiState.Idle)
    val estado: StateFlow<RegistroCicloUiState> = _estado

    /** Ver RegistroCicloRepository: sempre salva para a data de hoje. */
    fun salvar(menstruacao: Boolean, observacao: String? = null) {
        _estado.value = RegistroCicloUiState.Carregando
        viewModelScope.launch {
            val dataHoje = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            _estado.value = when (
                val resultado = repository.salvar(dataHoje, menstruacao, observacao)
            ) {
                is RegistroCicloResultado.Sucesso -> RegistroCicloUiState.Sucesso
                is RegistroCicloResultado.Erro -> RegistroCicloUiState.Erro(resultado.mensagem)
            }
        }
    }
}
