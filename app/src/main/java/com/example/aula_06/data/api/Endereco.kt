package com.example.aula_06.data.api

import com.google.gson.annotations.SerializedName

// Resposta JSON do ViaCEP. @SerializedName liga o nome do JSON ao campo em portugues.
data class Endereco(
    @SerializedName("cep") val cep: String? = null,
    @SerializedName("logradouro") val rua: String? = null,
    @SerializedName("bairro") val bairro: String? = null,
    @SerializedName("localidade") val cidade: String? = null,
    @SerializedName("uf") val uf: String? = null,
    @SerializedName("erro") val erro: Boolean? = null
)
