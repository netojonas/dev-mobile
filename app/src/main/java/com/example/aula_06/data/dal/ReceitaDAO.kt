package com.example.aula_06.data.dal

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.aula_06.data.local.AppDatabase
import com.example.aula_06.model.Receita

class ReceitaDAO(context: Context) {
    private val appContext = context.applicationContext
    private val colunas = arrayOf(
        "id", "nome", "tempo", "porcoes", "dificuldade", "ingredientes", "modo_preparo", "imagem", "foto"
    )

    fun cadastrar(receita: Receita): Long = AppDatabase(appContext).use { db ->
        db.writableDatabase.insertOrThrow("receitas", null, valores(receita))
    }

    fun listar(): List<Receita> = AppDatabase(appContext).use { db ->
        db.readableDatabase.query(
            "receitas", colunas, null, null, null, null, "id"
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) add(lerReceita(cursor))
            }
        }
    }

    fun buscarPorId(id: Long): Receita? = AppDatabase(appContext).use { db ->
        db.readableDatabase.query(
            "receitas", colunas, "id = ?", arrayOf(id.toString()), null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) lerReceita(cursor) else null
        }
    }

    fun alterar(receita: Receita) {
        AppDatabase(appContext).use { db ->
            val alterado = db.writableDatabase.update(
                "receitas", valores(receita), "id = ?", arrayOf(receita.id.toString())
            )
            require(alterado == 1) { "Receita nao encontrada. Atualize a lista." }
        }
    }

    fun excluir(id: Long) {
        AppDatabase(appContext).use { db ->
            val excluido = db.writableDatabase.delete(
                "receitas", "id = ?", arrayOf(id.toString())
            )
            require(excluido == 1) { "Receita nao encontrada. Atualize a lista." }
        }
    }

    private fun valores(receita: Receita) = ContentValues().apply {
        put("nome", receita.nome)
        put("tempo", receita.tempo)
        put("porcoes", receita.porcoes)
        put("dificuldade", receita.dificuldade)
        put("ingredientes", receita.ingredientes)
        put("modo_preparo", receita.modoPreparo)
        put("imagem", receita.imagem)
        put("foto", receita.foto)
    }

    private fun lerReceita(cursor: Cursor) = Receita(
        cursor.getLong(0),
        cursor.getString(1),
        cursor.getString(2),
        cursor.getString(3),
        cursor.getString(4),
        cursor.getString(5),
        cursor.getString(6),
        if (cursor.isNull(7)) null else cursor.getString(7),
        if (cursor.isNull(8)) null else cursor.getBlob(8)
    )
}
