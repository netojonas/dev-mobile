package com.example.aula_06.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import com.example.aula_06.ui.theme.AzulTopo

@Composable
fun Menu(aoReceitas: () -> Unit, aoCompras: () -> Unit, aoMercados: () -> Unit,
         aoUsuarios: () -> Unit, aoSair: () -> Unit) {
    Row(Modifier.fillMaxWidth().background(AzulTopo), verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = aoReceitas, modifier = Modifier.weight(1f)) { Text("Receitas", maxLines = 1) }
        TextButton(onClick = aoCompras, modifier = Modifier.weight(1f)) { Text("Compras", maxLines = 1) }
        TextButton(onClick = aoMercados, modifier = Modifier.weight(1f)) { Text("Mercados", maxLines = 1) }
        TextButton(onClick = aoUsuarios, modifier = Modifier.weight(1f)) { Text("Usuarios", maxLines = 1) }
        TextButton(onClick = aoSair, modifier = Modifier.weight(0.7f)) { Text("Sair", maxLines = 1) }
    }
}
