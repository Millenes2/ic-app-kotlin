package com.example.ic_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ic_app.data.remote.dto.UsuarioResponse
import com.example.ic_app.repository.PerfilRepository
import com.example.ic_app.repository.PerfilResultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface PerfilUiState {
    data object Idle : PerfilUiState
    data object Carregando : PerfilUiState
    data class Sucesso(val usuario: UsuarioResponse) : PerfilUiState
    data class Erro(val mensagem: String) : PerfilUiState
}

/**
 * Resultado do salvamento (`PATCH /perfil`), separado de [PerfilUiState] de
 * propósito: [PerfilUiState] também é usado por [PerfilViewModel.carregar] só
 * para pré-preencher os campos da tela, e não deve disparar navegação — só o
 * salvamento explícito deve.
 */
sealed interface PerfilSalvarEvento {
    data object Idle : PerfilSalvarEvento
    data object Salvando : PerfilSalvarEvento
    data object Salvo : PerfilSalvarEvento
    data class Erro(val mensagem: String) : PerfilSalvarEvento
}

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PerfilRepository()

    private val _estado = MutableStateFlow<PerfilUiState>(PerfilUiState.Idle)
    val estado: StateFlow<PerfilUiState> = _estado

    private val _eventoSalvar = MutableStateFlow<PerfilSalvarEvento>(PerfilSalvarEvento.Idle)
    val eventoSalvar: StateFlow<PerfilSalvarEvento> = _eventoSalvar

    /**
     * Busca o perfil salvo no backend, se houver sessão ativa. Usada para
     * pré-preencher a tela com dados reais em vez de só o estado local
     * (`nomeUsuario`/etc. em AppScreen.kt) quando a usuária já está logada.
     */
    fun carregar() {
        viewModelScope.launch {
            repository.buscar()?.let { usuario ->
                _estado.value = PerfilUiState.Sucesso(usuario)
            }
        }
    }

    fun salvar(nome: String, dataNascimento: String, peso: String, objetivoAtual: String) {
        _eventoSalvar.value = PerfilSalvarEvento.Salvando
        viewModelScope.launch {
            _eventoSalvar.value = when (
                val resultado = repository.salvar(nome, dataNascimento, peso, objetivoAtual)
            ) {
                is PerfilResultado.Sucesso -> PerfilSalvarEvento.Salvo
                is PerfilResultado.Erro -> PerfilSalvarEvento.Erro(resultado.mensagem)
            }
        }
    }
}
