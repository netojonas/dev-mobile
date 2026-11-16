package com.example.aula_06.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.aula_06.model.Mercado
import com.example.aula_06.ui.theme.*
import com.example.aula_06.ui.viewmodel.MercadoViewModel

@Composable
fun MercadoEditarScreen(viewModel: MercadoViewModel, mercadoId: Long, aoVoltar: () -> Unit) {
    val contexto = LocalContext.current
    // Ao abrir a tela (ou trocar de id), carrega o mercado do banco.
    LaunchedEffect(mercadoId) { viewModel.buscarPorId(mercadoId) }
    // Quando o ViewModel avisa que salvou, volta para a lista.
    LaunchedEffect(viewModel.salvo) {
        if (viewModel.salvo) { viewModel.consumirResultado(); aoVoltar() }
    }
    if (!viewModel.carregado) {
        Column(Modifier.fillMaxSize().background(AzulFundo).padding(16.dp)) {
            viewModel.mensagem?.let { Text(it) }
            if (viewModel.carregando) CircularProgressIndicator()
            else TextButton(onClick = { viewModel.buscarPorId(mercadoId) }) { Text("Tentar carregar novamente.") }
            TextButton(onClick = aoVoltar) { Text("Voltar") }
        }
        return
    }
    Column(Modifier.fillMaxSize().background(AzulFundo).imePadding()
        .verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(onClick = aoVoltar, enabled = !viewModel.carregando) { Text("Voltar") }
        Text("Editar mercado", color = AzulEscuro, style = MaterialTheme.typography.titleLarge)
        FormularioMercado(viewModel, habilitado = !viewModel.carregando)
        OutlinedButton(onClick = {
            abrirNoMapa(contexto, Mercado(nome = viewModel.nome, cep = viewModel.cep, rua = viewModel.rua,
                bairro = viewModel.bairro, cidade = viewModel.cidade, uf = viewModel.uf))
        }, modifier = Modifier.fillMaxWidth()) { Text("Abrir no mapa") }
        Button(onClick = viewModel::salvarAlteracoes,
            enabled = !viewModel.carregando && !viewModel.salvo && !viewModel.buscandoCep,
            modifier = Modifier.fillMaxWidth()) { Text("Salvar mercado") }
    }
}
