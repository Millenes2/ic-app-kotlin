package com.example.ic_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ic_app.data.local.SessaoDataStore
import com.example.ic_app.repository.ConsentimentoRepository
import kotlinx.coroutines.launch

/**
 * ViewModel "melhor esforço": usado em AppScreen.kt para registrar o
 * consentimento assim que a usuária confirma em ConsentScreen, sem bloquear
 * o avanço do onboarding (ver ConsentimentoRepository).
 */
class ConsentimentoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ConsentimentoRepository(sessaoDataStore = SessaoDataStore(application))

    fun registrar() {
        viewModelScope.launch {
            repository.registrar()
        }
    }
}
