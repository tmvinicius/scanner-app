package br.com.tmvinicius.scanner_app.testutil

import br.com.tmvinicius.scanner_app.domain.model.Document
import br.com.tmvinicius.scanner_app.domain.model.DocumentError
import br.com.tmvinicius.scanner_app.domain.model.ScannedPdf
import br.com.tmvinicius.scanner_app.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** Repositório em memória para testar ViewModels sem banco nem arquivos. */
class FakeDocumentRepository(initial: List<Document> = emptyList()) : DocumentRepository {

    val documents = MutableStateFlow(initial)

    override fun observeDocuments(): Flow<List<Document>> = documents

    override fun observeDocument(id: Long): Flow<Document?> =
        documents.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun saveScan(scannedPdf: ScannedPdf): Result<Document> {
        val document = Document(
            id = (documents.value.maxOfOrNull { it.id } ?: 0) + 1,
            name = "Documento",
            filePath = "/documentos/Documento.pdf",
            sizeBytes = 0,
            createdAtMillis = 0,
            pageCount = scannedPdf.pageCount,
        )
        documents.update { listOf(document) + it }
        return Result.success(document)
    }

    override suspend fun rename(id: Long, newName: String): Result<Document> {
        val current = documents.value.firstOrNull { it.id == id }
            ?: return Result.failure(DocumentError.NotFound(id))
        val renamed = current.copy(name = newName)
        documents.update { list -> list.map { if (it.id == id) renamed else it } }
        return Result.success(renamed)
    }

    override suspend fun delete(id: Long): Result<Unit> {
        if (documents.value.none { it.id == id }) return Result.failure(DocumentError.NotFound(id))
        documents.update { list -> list.filterNot { it.id == id } }
        return Result.success(Unit)
    }
}

fun testDocument(id: Long, name: String = "Documento $id", createdAtMillis: Long = id * 1_000) = Document(
    id = id,
    name = name,
    filePath = "/documentos/$name.pdf",
    sizeBytes = 1_024 * id,
    createdAtMillis = createdAtMillis,
    pageCount = 1,
)
