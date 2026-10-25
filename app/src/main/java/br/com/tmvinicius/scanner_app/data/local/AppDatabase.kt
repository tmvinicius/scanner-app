package br.com.tmvinicius.scanner_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Banco local do app. O schema de cada versão é exportado para `app/schemas/`;
 * ao mudar uma entidade, aumente [version] e escreva a migração.
 */
@Database(entities = [DocumentEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun documentDao(): DocumentDao

    companion object {
        const val NAME = "scanner.db"
    }
}
