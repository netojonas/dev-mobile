package com.example.aula_06.validation

object MercadoValidator {
    private val formatoCep = Regex("[0-9]{5}-?[0-9]{3}")

    fun cepValido(cep: String) = formatoCep.matches(cep)

    fun validar(nome: String, cep: String) {
        require(nome.isNotBlank()) { "Informe o nome do mercado." }
        require(cepValido(cep)) { "Informe um CEP valido (00000-000)." }
    }
}
