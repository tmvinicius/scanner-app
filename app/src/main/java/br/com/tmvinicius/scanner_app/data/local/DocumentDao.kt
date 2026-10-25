package br.com.tmvinicius.scanner_app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    /** Mais recentes primeiro; o id desempata documentos criados no mesmo milissegundo. */
    @Query("SELECT * FROM documents ORDER BY created_at DESC, id DESC")
    fun observeAll(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id")
    fun observeById(id: Long): Flow<DocumentEntity?>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getById(id: Long): DocumentEntity?

    /** Devolve o id gerado. */
    @Insert
    suspend fun insert(document: DocumentEntity): Long

    /** Devolve quantas linhas mudaram (0 se o documento não existe mais). */
    @Update
    suspend fun update(document: DocumentEntity): Int

    /** Devolve quantas linhas foram apagadas. */
    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}
