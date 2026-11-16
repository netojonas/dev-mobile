package com.example.aula_06.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aula_06.auth.SessaoUsuario
import com.example.aula_06.data.api.ViaCepApi
import com.example.aula_06.data.dal.MercadoDAO
import com.example.aula_06.model.Mercado
import com.example.aula_06.validation.MercadoValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MercadoViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = MercadoDAO(application)
    var mercados by mutableStateOf<List<Mercado>>(emptyList())
        private set
    var mercado by mutableStateOf(Mercado())
        private set
    var nome by mutableStateOf("")
        private set
    var cep by mutableStateOf("")
        private set
    var rua by mutableStateOf("")
        private set
    var bairro by mutableStateOf("")
        private set
    var cidade by mutableStateOf("")
        private set
    var uf by mutableStateOf("")
        private set
    var carregando by mutableStateOf(false)
        private set
    var buscandoCep by mutableStateOf(false)
        private set
    var mensagem by mutableStateOf<String?>(null)
        private set
    var mensagemCep by mutableStateOf<String?>(null)
        private set
    var carregado by mutableStateOf(false)
        private set
    var salvo by mutableStateOf(false)
        private set

    private fun executar(operacao: suspend () -> Unit) {
        if (carregando || salvo) return
        carregando = true
        mensagem = null
        viewModelScope.launch {
            try {
                SessaoUsuario.exigirUsuario()
                operacao()
            } catch (erro: CancellationException) {
                throw erro
            } catch (erro: IllegalArgumentException) {
                mensagem = erro.message
            } catch (erro: Exception) {
                mensagem = "Nao foi possivel acessar os mercados. Tente novamente."
            } finally {
                carregando = false
            }
        }
    }

    fun listar() = executar { mercados = withContext(Dispatchers.IO) { dao.listar() } }

    fun buscarPorId(id: Long) {
        if (carregado && mercado.id == id) return
        executar {
            carregado = false
            val encontrado = requireNotNull(withContext(Dispatchers.IO) { dao.buscarPorId(id) }) {
                "Mercado nao encontrado."
            }
            mercado = encontrado
            nome = encontrado.nome
            cep = encontrado.cep
            rua = encontrado.rua
            bairro = encontrado.bairro
            cidade = encontrado.cidade
            uf = encontrado.uf
            carregado = true
        }
    }

    fun mudarNome(valor: String) { if (!carregando && !salvo) nome = valor }
    fun mudarCep(valor: String) {
        if (carregando || salvo || buscandoCep || valor.length > 9) return
        cep = valor
        mensagemCep = null
    }
    fun mudarRua(valor: String) { if (!carregando && !salvo) rua = valor }
    fun mudarBairro(valor: String) { if (!carregando && !salvo) bairro = valor }
    fun mudarCidade(valor: String) { if (!carregando && !salvo) cidade = valor }
    fun mudarUf(valor: String) { if (!carregando && !salvo && valor.length <= 2) uf = valor.uppercase() }

    // Consulta o ViaCEP com Retrofit e preenche o endereco (mesma ideia do CepViewModel da aula).
    fun buscarCep() {
        if (buscandoCep) return
        mensagemCep = null
        if (!MercadoValidator.cepValido(cep)) {
            mensagemCep = "Informe um CEP valido (00000-000)."
            return
        }
        buscandoCep = true
        viewModelScope.launch {
            try {
                val resposta = ViaCepApi.service.buscar(cep.replace("-", ""))
                if (resposta.erro == true || resposta.cep.isNullOrBlank()) {
                    mensagemCep = "CEP nao encontrado."
                } else {
                    rua = resposta.rua.orEmpty()
                    bairro = resposta.bairro.orEmpty()
                    cidade = resposta.cidade.orEmpty()
                    uf = resposta.uf.orEmpty()
                }
            } catch (erro: CancellationException) {
                throw erro
            } catch (_: Exception) {
                mensagemCep = "Nao foi possivel consultar o CEP. Verifique a internet."
            } finally {
                buscandoCep = false
            }
        }
    }

    private fun mercadoDoFormulario(id: Long = 0): Mercado {
        MercadoValidator.validar(nome.trim(), cep.trim())
        return Mercado(id, nome.trim(), cep.trim(), rua.trim(), bairro.trim(), cidade.trim(), uf.trim())
    }

    fun cadastrar() = executar {
        val novo = mercadoDoFormulario()
        withContext(Dispatchers.IO) { dao.cadastrar(novo) }
        salvo = true
    }

    fun salvarAlteracoes() = executar {
        require(carregado && mercado.id > 0) { "Selecione um registro para alterar." }
        val alterado = mercadoDoFormulario(mercado.id)
        withContext(Dispatchers.IO) { dao.alterar(alterado) }
        salvo = true
    }

    fun excluir(id: Long) = executar {
        withContext(Dispatchers.IO) { dao.excluir(id) }
        mercados = mercados.filterNot { it.id == id }
    }

    fun consumirResultado() { salvo = false }
}
