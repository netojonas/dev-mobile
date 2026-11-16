package com.example.aula_06.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

// Converte a foto (camera ou galeria) em bytes para gravar na coluna BLOB.
object ImagemConversor {
    // Limite do lado maior: fotos da galeria podem ter varios MB e o SQLite
    // nao le linhas maiores que ~2 MB.
    private const val LADO_MAXIMO = 800

    fun paraBlob(imagem: Bitmap): ByteArray {
        val reduzida = reduzir(imagem)
        return ByteArrayOutputStream().use { saida ->
            check(reduzida.compress(Bitmap.CompressFormat.JPEG, 85, saida)) {
                "Nao foi possivel salvar a imagem."
            }
            saida.toByteArray()
        }
    }

    suspend fun paraBlob(contexto: Context, uri: Uri): ByteArray = withContext(Dispatchers.IO) {
        val bitmap = checkNotNull(
            contexto.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
        ) { "Nao foi possivel abrir a imagem." }
        paraBlob(bitmap)
    }

    fun paraBitmap(blob: ByteArray): Bitmap? = BitmapFactory.decodeByteArray(blob, 0, blob.size)

    private fun reduzir(imagem: Bitmap): Bitmap {
        val maior = maxOf(imagem.width, imagem.height)
        if (maior <= LADO_MAXIMO) return imagem
        val escala = LADO_MAXIMO.toFloat() / maior
        return Bitmap.createScaledBitmap(
            imagem, (imagem.width * escala).toInt(), (imagem.height * escala).toInt(), true
        )
    }
}
