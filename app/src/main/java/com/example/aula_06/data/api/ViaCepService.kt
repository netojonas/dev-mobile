package com.example.aula_06.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// Retrofit transforma esta interface em chamadas HTTP.
// Ex.: buscar("01001000") faz GET https://viacep.com.br/ws/01001000/json/
interface ViaCepService {
    @GET("{cep}/json/")
    suspend fun buscar(@Path("cep") cep: String): Endereco
}

object ViaCepApi {
    val service: ViaCepService by lazy {
        Retrofit.Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ViaCepService::class.java)
    }
}
