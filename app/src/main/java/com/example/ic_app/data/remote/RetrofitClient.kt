package com.example.ic_app.data.remote

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

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
}
