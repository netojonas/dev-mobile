package com.example.aula_06.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.aula_06.data.ImagemConversor
import com.example.aula_06.ui.viewmodel.ReceitaViewModel

// Botoes "Tirar foto" e "Escolher da galeria" + previa da foto escolhida.
// A tela so abre a camera/galeria; quem guarda e converte a foto e o ViewModel.
@Composable
fun CampoFotoReceita(viewModel: ReceitaViewModel) {
    // Camera: devolve um Bitmap (ou null se o usuario cancelar).
    val camera = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { imagem -> viewModel.receberFoto(imagem) }

    // Galeria: seletor de fotos do sistema, devolve a Uri da imagem escolhida.
    val galeria = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> viewModel.receberImagem(uri) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FotoBlob(viewModel.foto, 180.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                enabled = !viewModel.carregando,
                onClick = {
                    try { camera.launch(null) } catch (_: Exception) { viewModel.informarFalhaFoto("camera") }
                }
            ) { Text("Tirar foto") }
            OutlinedButton(
                enabled = !viewModel.carregando,
                onClick = {
                    try {
                        galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    } catch (_: Exception) { viewModel.informarFalhaFoto("galeria") }
                }
            ) { Text("Escolher da galeria") }
        }
        viewModel.mensagemFoto?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

// Mostra a foto salva (BLOB); se nao houver, a imagem fixa do catalogo; senao nada.
@Composable
fun ImagemReceita(foto: ByteArray?, imagem: String?, descricao: String, altura: Dp) {
    if (foto != null) {
        FotoBlob(foto, altura, descricao)
    } else {
        imagemReceita(imagem)?.let { recurso ->
            Image(
                painter = painterResource(id = recurso),
                contentDescription = descricao,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(altura).clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

@Composable
private fun FotoBlob(foto: ByteArray?, altura: Dp, descricao: String = "Foto da receita") {
    if (foto == null) return
    // remember: so decodifica de novo quando os bytes mudam.
    val bitmap = remember(foto) { ImagemConversor.paraBitmap(foto) } ?: return
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = descricao,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth().height(altura).clip(RoundedCornerShape(12.dp))
    )
}
