package com.example.ic_app.data.remote

import android.content.Context
import com.example.ic_app.data.local.SessaoDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 10.0.2.2 é o endereço do host visto de dentro do emulador Android, apontando para
    // o uvicorn rodando em 127.0.0.1:8000 na máquina de desenvolvimento (ver
    // AndroidManifest.xml/network_security_config.xml para a liberação de cleartext).
    // Em dispositivo físico, trocar pelo IP da máquina na mesma rede, ou usar
    // `adb reverse tcp:8000 tcp:8000` e apontar para 127.0.0.1.
    private const val BASE_URL = "http://10.0.2.2:8000/"

    private lateinit var sessaoDataStore: SessaoDataStore

    /**
     * Deve ser chamada uma única vez, com o contexto da aplicação, antes de
     * qualquer chamada de rede — ver `LunaApplication.onCreate`. Necessário
     * para o [authInterceptor] conseguir ler o token salvo em DataStore.
     */
    fun init(context: Context) {
        sessaoDataStore = SessaoDataStore(context.applicationContext)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Anexa `Authorization: Bearer <token>` a toda requisição quando há uma
     * sessão salva, substituindo a leitura manual do token e a montagem do
     * header que antes cada Repository fazia (ver CLAUDE.md seção 5). Rotas
     * públicas (`POST /auth/registrar`, `POST /auth/login`) não usam esse
     * header; se não houver token salvo, a requisição simplesmente segue sem
     * ele, e rotas protegidas respondem 401 normalmente.
     */
    private val authInterceptor = Interceptor { chain ->
        val token = runBlocking { sessaoDataStore.tokenFlow.first() }
        val requisicaoOriginal = chain.request()
        val requisicao = if (token != null) {
            requisicaoOriginal.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            requisicaoOriginal
        }
        chain.proceed(requisicao)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
    val registroDiarioApi: RegistroDiarioApi = retrofit.create(RegistroDiarioApi::class.java)
    val perfilApi: PerfilApi = retrofit.create(PerfilApi::class.java)
    val registroCicloApi: RegistroCicloApi = retrofit.create(RegistroCicloApi::class.java)
    val respostaObjetivoApi: RespostaObjetivoApi = retrofit.create(RespostaObjetivoApi::class.java)
    val consentimentoApi: ConsentimentoApi = retrofit.create(ConsentimentoApi::class.java)
}
