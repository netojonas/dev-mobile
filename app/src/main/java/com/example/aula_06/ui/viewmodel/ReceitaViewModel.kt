package com.example.aula_06.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import com.example.aula_06.data.ImagemConversor
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aula_06.auth.SessaoUsuario
import com.example.aula_06.data.dal.ReceitaDAO
import com.example.aula_06.model.Receita
import com.example.aula_06.validation.ReceitaValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReceitaViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = ReceitaDAO(application)
    var receitas by mutableStateOf<List<Receita>>(emptyList())
        private set
    var receita by mutableStateOf(Receita())
        private set
    var nome by mutableStateOf("")
        private set
    var tempo by mutableStateOf("")
        private set
    var porcoes by mutableStateOf("")
        private set
    var dificuldade by mutableStateOf("")
        private set
    var ingredientes by mutableStateOf("")
        private set
    var modoPreparo by mutableStateOf("")
        private set
    var carregando by mutableStateOf(false)
        private set
    var mensagem by mutableStateOf<String?>(null)
        private set
    var carregado by mutableStateOf(false)
        private set
    var salvo by mutableStateOf(false)
        private set
    var excluido by mutableStateOf(false)
        private set
    // Foto em bytes (BLOB). A tela abre a camera/galeria e entrega o resultado aqui.
    var foto by mutableStateOf<ByteArray?>(null)
        private set
    var mensagemFoto by mutableStateOf<String?>(null)
        private set

    fun receberFoto(imagem: Bitmap?) {
        if (imagem == null) {
            mensagemFoto = "Captura cancelada."
            return
        }
        foto = ImagemConversor.paraBlob(imagem)
        mensagemFoto = null
    }

    fun receberImagem(uri: Uri?) {
        if (uri == null) {
            mensagemFoto = "Nenhuma imagem selecionada."
            return
        }
        viewModelScope.launch {
            try {
                foto = ImagemConversor.paraBlob(getApplication(), uri)
                mensagemFoto = null
            } catch (erro: CancellationException) {
                throw erro
            } catch (_: Exception) {
                mensagemFoto = "Nao foi possivel abrir a imagem."
            }
        }
    }

    fun informarFalhaFoto(origem: String) {
        mensagemFoto = "Nao foi possivel abrir a $origem."
    }

    private fun executar(operacao: suspend () -> Unit) {
        if (carregando || salvo || excluido) return
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
                mensagem = "Nao foi possivel acessar as receitas. Tente novamente."
            } finally {
                carregando = false
            }
        }
    }
    fun listar() = executar { receitas = withContext(Dispatchers.IO) { dao.listar() } }
    fun buscarPorId(id: Long) {
        if (carregado && receita.id == id) return
        carregar(id)
    }

    fun carregar(id: Long) = executar {
        carregado = false
        val encontrado = requireNotNull(withContext(Dispatchers.IO) { dao.buscarPorId(id) }) {
            "Receita nao encontrada."
        }
        receita = encontrado
        nome = encontrado.nome
        tempo = encontrado.tempo
        porcoes = encontrado.porcoes
        dificuldade = encontrado.dificuldade
        ingredientes = encontrado.ingredientes
        modoPreparo = encontrado.modoPreparo
        foto = encontrado.foto
        carregado = true
    }

    fun mudarNome(valor: String) {
        if (!carregando && !salvo) nome = valor
    }
    fun mudarTempo(valor: String) {
        if (!carregando && !salvo) tempo = valor
    }
    fun mudarPorcoes(valor: String) {
        if (!carregando && !salvo) porcoes = valor
    }
    fun mudarDificuldade(valor: String) {
        if (!carregando && !salvo) dificuldade = valor
    }
    fun mudarIngredientes(valor: String) {
        if (!carregando && !salvo) ingredientes = valor
    }
    fun mudarModoPreparo(valor: String) {
        if (!carregando && !salvo) modoPreparo = valor
    }

    fun cadastrar() = executar {
        ReceitaValidator.validar(
            nome.trim(),
            tempo.trim(),
            porcoes.trim(),
            dificuldade.trim(),
            ingredientes.trim(),
            modoPreparo.trim()
        )
        withContext(Dispatchers.IO) {
            dao.cadastrar(Receita(
                nome = nome.trim(),
                tempo = tempo.trim(),
                porcoes = porcoes.trim(),
                dificuldade = dificuldade.trim(),
                ingredientes = ingredientes.trim(),
                modoPreparo = modoPreparo.trim(),
                foto = foto
            ))
        }
        salvo = true
    }

    fun salvarAlteracoes() = executar {
        require(carregado && receita.id > 0) { "Selecione um registro para alterar." }
        ReceitaValidator.validar(
            nome.trim(),
            tempo.trim(),
            porcoes.trim(),
            dificuldade.trim(),
            ingredientes.trim(),
            modoPreparo.trim()
        )
        withContext(Dispatchers.IO) {
            dao.alterar(Receita(
                id = receita.id,
                nome = nome.trim(),
                tempo = tempo.trim(),
                porcoes = porcoes.trim(),
                dificuldade = dificuldade.trim(),
                ingredientes = ingredientes.trim(),
                modoPreparo = modoPreparo.trim(),
                imagem = receita.imagem,
                foto = foto
            ))
        }
        salvo = true
    }
    fun excluir() = executar {
        require(carregado && receita.id > 0) { "Selecione uma receita." }
        withContext(Dispatchers.IO) { dao.excluir(receita.id) }
        excluido = true
    }
    fun consumirResultado() {
        salvo = false
        excluido = false
    }
}
