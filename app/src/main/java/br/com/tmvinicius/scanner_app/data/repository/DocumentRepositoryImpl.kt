package br.com.tmvinicius.scanner_app.data.repository

import br.com.tmvinicius.scanner_app.data.files.PdfFileStorage
import br.com.tmvinicius.scanner_app.data.local.DocumentDao
import br.com.tmvinicius.scanner_app.data.local.DocumentEntity
import br.com.tmvinicius.scanner_app.data.local.toDomain
import br.com.tmvinicius.scanner_app.di.IoDispatcher
import br.com.tmvinicius.scanner_app.domain.model.Document
import br.com.tmvinicius.scanner_app.domain.model.DocumentError
import br.com.tmvinicius.scanner_app.domain.model.DocumentNaming
import br.com.tmvinicius.scanner_app.domain.model.ScannedPdf
import br.com.tmvinicius.scanner_app.domain.repository.Clock
import br.com.tmvinicius.scanner_app.domain.repository.DocumentRepository
import br.com.tmvinicius.scanner_app.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Mantém arquivo e registro consistentes: quando o segundo passo de uma operação falha,
 * o primeiro é desfeito antes de devolver o erro.
 */
@Singleton
class DocumentRepositoryImpl @Inject constructor(
    private val dao: DocumentDao,
    private val fileStorage: PdfFileStorage,
    private val settingsRepository: SettingsRepository,
    private val clock: Clock,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : DocumentRepository {

    /** Serializa escritas para que duas operações não escolham o mesmo nome livre. */
    private val writeMutex = Mutex()

    override fun observeDocuments(): Flow<List<Document>> =
        dao.observeAll().map { entities -> entities.map(DocumentEntity::toDomain) }

    override fun observeDocument(id: Long): Flow<Document?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun saveScan(scannedPdf: ScannedPdf): Result<Document> = write {
        val createdAt = clock.nowMillis()
        val prefix = settingsRepository.settings.first().fileNamePrefix
        val name = availableName(DocumentNaming.defaultName(prefix, createdAt))
        val stored = fileStorage.import(scannedPdf.sourceUri, DocumentNaming.fileName(name))

        val entity = DocumentEntity(
            name = name,
            filePath = stored.path,
            sizeBytes = stored.sizeBytes,
            createdAtMillis = createdAt,
            pageCount = scannedPdf.pageCount,
        )
        val id = undoOnFailure(undo = { fileStorage.delete(stored.path) }) { dao.insert(entity) }
        entity.copy(id = id).toDomain()
    }

    override suspend fun rename(id: Long, newName: String): Result<Document> = write {
        val current = dao.getById(id) ?: throw DocumentError.NotFound(id)
        if (newName == current.name) return@write current.toDomain()

        val name = availableName(newName)
        val renamed = fileStorage.rename(current.filePath, DocumentNaming.fileName(name))
        val updated = current.copy(name = name, filePath = renamed.path)
        undoOnFailure(undo = { fileStorage.rename(renamed.path, DocumentNaming.fileName(current.name)) }) {
            if (dao.update(updated) == 0) throw DocumentError.NotFound(id)
        }
        updated.toDomain()
    }

    override suspend fun delete(id: Long): Result<Unit> = write {
        val current = dao.getById(id) ?: throw DocumentError.NotFound(id)
        // O registro sai primeiro: se o arquivo não puder ser apagado, o registro volta.
        if (dao.deleteById(id) == 0) throw DocumentError.NotFound(id)
        undoOnFailure(undo = { dao.insert(current) }) {
            if (!fileStorage.delete(current.filePath)) error("Could not delete ${current.filePath}")
        }
    }

    private suspend fun availableName(baseName: String): String =
        DocumentNaming.uniqueName(baseName) { fileStorage.exists(DocumentNaming.fileName(it)) }

    /** Executa [block] no dispatcher de IO, um por vez, convertendo falhas em [DocumentError]. */
    private suspend fun <T> write(block: suspend () -> T): Result<T> = withContext(ioDispatcher) {
        writeMutex.withLock {
            try {
                Result.success(block())
            } catch (e: CancellationException) {
                throw e
            } catch (e: DocumentError) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(DocumentError.Storage(e))
            }
        }
    }

    /** Executa [block]; se falhar, roda [undo] (sem mascarar o erro original) e relança. */
    private suspend fun <T> undoOnFailure(undo: suspend () -> Unit, block: suspend () -> T): T =
        try {
            block()
        } catch (e: Exception) {
            if (e !is CancellationException) {
                runCatching { undo() }.exceptionOrNull()?.let(e::addSuppressed)
            }
            throw e
        }
}
