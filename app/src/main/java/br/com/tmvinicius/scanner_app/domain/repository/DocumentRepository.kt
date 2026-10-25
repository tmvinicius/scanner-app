package br.com.tmvinicius.scanner_app.domain.repository

import br.com.tmvinicius.scanner_app.domain.model.Document
import br.com.tmvinicius.scanner_app.domain.model.DocumentError
import br.com.tmvinicius.scanner_app.domain.model.ScannedPdf
import kotlinx.coroutines.flow.Flow

/**
 * Fonte única dos documentos: mantém arquivo PDF e registro no banco sempre consistentes.
 * As falhas chegam como [Result.failure] com um [DocumentError].
 */
interface DocumentRepository {

    /** Todos os documentos, mais recentes primeiro. */
    fun observeDocuments(): Flow<List<Document>>

    /** Um documento, ou `null` se não existir (ou tiver sido excluído). */
    fun observeDocument(id: Long): Flow<Document?>

    /** Copia o PDF escaneado para o app com o nome padrão e cria o registro. */
    suspend fun saveScan(scannedPdf: ScannedPdf): Result<Document>

    /** Renomeia arquivo e registro. [newName] já deve vir saneado e não vazio. */
    suspend fun rename(id: Long, newName: String): Result<Document>

    /** Apaga arquivo e registro. */
    suspend fun delete(id: Long): Result<Unit>
}
