package com.example.aula_06.data.dal

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.aula_06.data.local.AppDatabase
import com.example.aula_06.model.Mercado

class MercadoDAO(context: Context) {
    private val appContext = context.applicationContext
    private val colunas = arrayOf("id", "nome", "cep", "rua", "bairro", "cidade", "uf")

    fun cadastrar(mercado: Mercado): Long = AppDatabase(appContext).use { db ->
        db.writableDatabase.insertOrThrow("mercados", null, valores(mercado))
    }

    fun listar(): List<Mercado> = AppDatabase(appContext).use { db ->
        db.readableDatabase.query("mercados", colunas, null, null, null, null, "nome").use { cursor ->
            buildList {
                while (cursor.moveToNext()) add(lerMercado(cursor))
            }
        }
    }

    fun buscarPorId(id: Long): Mercado? = AppDatabase(appContext).use { db ->
        db.readableDatabase.query(
            "mercados", colunas, "id = ?", arrayOf(id.toString()), null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) lerMercado(cursor) else null
        }
    }

    fun alterar(mercado: Mercado) {
        AppDatabase(appContext).use { db ->
            val alterado = db.writableDatabase.update(
                "mercados", valores(mercado), "id = ?", arrayOf(mercado.id.toString())
            )
            require(alterado == 1) { "Mercado nao encontrado. Atualize a lista." }
        }
    }

    fun excluir(id: Long) {
        AppDatabase(appContext).use { db ->
            val excluido = db.writableDatabase.delete("mercados", "id = ?", arrayOf(id.toString()))
            require(excluido == 1) { "Mercado nao encontrado. Atualize a lista." }
        }
    }

    private fun valores(mercado: Mercado) = ContentValues().apply {
        put("nome", mercado.nome)
        put("cep", mercado.cep)
        put("rua", mercado.rua)
        put("bairro", mercado.bairro)
        put("cidade", mercado.cidade)
        put("uf", mercado.uf)
    }

    private fun lerMercado(cursor: Cursor) = Mercado(
        cursor.getLong(0),
        cursor.getString(1),
        cursor.getString(2),
        cursor.getString(3),
        cursor.getString(4),
        cursor.getString(5),
        cursor.getString(6)
    )
}
