package com.example.aula_06.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.aula_06.model.Mercado
import com.example.aula_06.ui.theme.*
import com.example.aula_06.ui.viewmodel.MercadoViewModel

@Composable
fun MercadosScreen(viewModel: MercadoViewModel, aoNovo: () -> Unit, aoEditar: (Long) -> Unit) {
    val contexto = LocalContext.current
    var excluirId by rememberSaveable { mutableStateOf<Long?>(null) }
    LazyColumn(modifier = Modifier.fillMaxSize().background(AzulFundo).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Mercados", color = AzulEscuro, style = MaterialTheme.typography.titleLarge)
            Row {
                Button(onClick = aoNovo, enabled = !viewModel.carregando) { Text("Adicionar mercado") }
                TextButton(onClick = viewModel::listar, enabled = !viewModel.carregando) { Text("Atualizar") }
            }
            viewModel.mensagem?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            if (viewModel.carregando) Text("Carregando...")
            if (!viewModel.carregando && viewModel.mercados.isEmpty()) Text("Nenhum mercado cadastrado.")
        }
        items(viewModel.mercados, key = { it.id }) { mercado ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(mercado.nome, color = AzulEscuro, style = MaterialTheme.typography.titleMedium)
                    Text(enderecoCompleto(mercado).ifBlank { "CEP ${mercado.cep}" })
                    Row {
                        TextButton(onClick = { aoEditar(mercado.id) }, enabled = !viewModel.carregando) { Text("Editar") }
                        TextButton(onClick = { excluirId = mercado.id }, enabled = !viewModel.carregando) { Text("Excluir") }
                        TextButton(onClick = { abrirNoMapa(contexto, mercado) }) { Text("Mapa") }
                    }
                }
            }
        }
    }
    val selecionado = viewModel.mercados.firstOrNull { it.id == excluirId }
    if (selecionado != null) {
        AlertDialog(onDismissRequest = { excluirId = null }, title = { Text("Excluir mercado?") },
            text = { Text("Excluir ${selecionado.nome}?") },
            confirmButton = { TextButton(onClick = { excluirId = null; viewModel.excluir(selecionado.id) },
                enabled = !viewModel.carregando) { Text("Confirmar exclusao") } },
            dismissButton = { TextButton(onClick = { excluirId = null }) { Text("Cancelar") } })
    }
}

fun enderecoCompleto(mercado: Mercado): String =
    listOf(mercado.rua, mercado.bairro, mercado.cidade, mercado.uf)
        .filter { it.isNotBlank() }
        .joinToString(", ")

// Abre o app de mapas no endereco do mercado (Intent geo:, como na aula; sem GPS e sem permissao).
fun abrirNoMapa(contexto: Context, mercado: Mercado) {
    val endereco = enderecoCompleto(mercado).ifBlank { mercado.cep }
    val mapa = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(endereco)))
    try {
        contexto.startActivity(mapa)
    } catch (_: Exception) {
        Toast.makeText(contexto, "Nao foi possivel abrir o mapa.", Toast.LENGTH_LONG).show()
    }
}
