package com.example.aula_06.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.aula_06.ui.theme.*
import com.example.aula_06.ui.viewmodel.MercadoViewModel

@Composable
fun CadastroMercadoScreen(viewModel: MercadoViewModel, aoVoltar: () -> Unit) {
    LaunchedEffect(viewModel.salvo) {
        if (viewModel.salvo) { viewModel.consumirResultado(); aoVoltar() }
    }
    Column(Modifier.fillMaxSize().background(AzulFundo).imePadding()
        .verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(onClick = aoVoltar, enabled = !viewModel.carregando) { Text("Voltar") }
        Text("Novo mercado", color = AzulEscuro, style = MaterialTheme.typography.titleLarge)
        FormularioMercado(viewModel, habilitado = !viewModel.carregando)
        Button(onClick = viewModel::cadastrar,
            enabled = !viewModel.carregando && !viewModel.salvo && !viewModel.buscandoCep,
            modifier = Modifier.fillMaxWidth()) { Text("Salvar mercado") }
    }
}

// Campos usados no cadastro e na edicao. "Buscar CEP" chama o ViaCEP pelo ViewModel.
@Composable
fun FormularioMercado(viewModel: MercadoViewModel, habilitado: Boolean) {
    OutlinedTextField(value = viewModel.nome, onValueChange = viewModel::mudarNome,
        label = { Text("Nome do mercado") }, modifier = Modifier.fillMaxWidth(), enabled = habilitado)
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = viewModel.cep, onValueChange = viewModel::mudarCep,
            label = { Text("CEP") }, singleLine = true, modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = habilitado && !viewModel.buscandoCep)
        Button(onClick = viewModel::buscarCep, enabled = habilitado && !viewModel.buscandoCep) {
            Text("Buscar CEP")
        }
    }
    if (viewModel.buscandoCep) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    viewModel.mensagemCep?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    OutlinedTextField(value = viewModel.rua, onValueChange = viewModel::mudarRua,
        label = { Text("Rua") }, modifier = Modifier.fillMaxWidth(), enabled = habilitado)
    OutlinedTextField(value = viewModel.bairro, onValueChange = viewModel::mudarBairro,
        label = { Text("Bairro") }, modifier = Modifier.fillMaxWidth(), enabled = habilitado)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = viewModel.cidade, onValueChange = viewModel::mudarCidade,
            label = { Text("Cidade") }, modifier = Modifier.weight(3f), enabled = habilitado)
        OutlinedTextField(value = viewModel.uf, onValueChange = viewModel::mudarUf,
            label = { Text("UF") }, modifier = Modifier.weight(1f), enabled = habilitado)
    }
    viewModel.mensagem?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    if (viewModel.carregando) Text("Aguarde...")
}
