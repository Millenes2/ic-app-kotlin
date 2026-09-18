package com.example.ic_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ic_app.data.local.SessaoDataStore
import com.example.ic_app.data.remote.dto.UsuarioResponse
import com.example.ic_app.repository.AuthRepository
import com.example.ic_app.repository.AuthResultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Carregando : AuthUiState
    data object Sucesso : AuthUiState
    data class Erro(val mensagem: String) : AuthUiState
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(sessaoDataStore = SessaoDataStore(application))

    private val _estado = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val estado: StateFlow<AuthUiState> = _estado

    fun login(email: String, senha: String) {
        _estado.value = AuthUiState.Carregando
        viewModelScope.launch {
            _estado.value = when (val resultado = repository.login(email, senha)) {
                is AuthResultado.Sucesso -> AuthUiState.Sucesso
                is AuthResultado.Erro -> AuthUiState.Erro(resultado.mensagem)
            }
        }
    }

    fun registrar(email: String, senha: String, nome: String) {
        _estado.value = AuthUiState.Carregando
        viewModelScope.launch {
            _estado.value = when (val resultado = repository.registrar(email, senha, nome)) {
                is AuthResultado.Sucesso -> AuthUiState.Sucesso
                is AuthResultado.Erro -> AuthUiState.Erro(resultado.mensagem)
            }
        }
    }

    /**
     * Ver AuthRepository.buscarUsuarioAutenticado: existe só para comprovar,
     * na prática, que o JWT armazenado é aceito pelo backend — não é chamada
     * por nenhuma tela hoje.
     */
    suspend fun buscarUsuarioAutenticado(): UsuarioResponse? = repository.buscarUsuarioAutenticado()
}
