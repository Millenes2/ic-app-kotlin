package com.example.ic_app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessaoDataStore by preferencesDataStore(name = "sessao")

class SessaoDataStore(private val context: Context) {

    private val chaveToken = stringPreferencesKey("access_token")

    val tokenFlow: Flow<String?> =
        context.sessaoDataStore.data.map { preferencias -> preferencias[chaveToken] }

    suspend fun salvarToken(token: String) {
        context.sessaoDataStore.edit { preferencias -> preferencias[chaveToken] = token }
    }

    suspend fun limparToken() {
        context.sessaoDataStore.edit { preferencias -> preferencias.remove(chaveToken) }
    }
}
