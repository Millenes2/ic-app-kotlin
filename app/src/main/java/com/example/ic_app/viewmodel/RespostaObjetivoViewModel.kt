package com.example.ic_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ic_app.data.local.SessaoDataStore
import com.example.ic_app.repository.RespostaObjetivoRepository
import kotlinx.coroutines.launch

/**
 * ViewModel "melhor esforço": usado em AppScreen.kt para persistir a opção
 * escolhida em cada etapa dos sub-fluxos de objetivo, sem bloquear a
 * navegação entre telas (ver RespostaObjetivoRepository sobre por que, hoje,
 * normalmente não há sessão ativa nesse ponto do fluxo).
 */
class RespostaObjetivoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RespostaObjetivoRepository(sessaoDataStore = SessaoDataStore(application))

    fun salvar(objetivo: String, etapa: Int, opcaoSelecionada: String) {
        if (objetivo.isBlank() || opcaoSelecionada.isBlank()) return
        viewModelScope.launch {
            repository.salvar(objetivo, etapa, opcaoSelecionada)
        }
    }
}
