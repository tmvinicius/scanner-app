package br.com.tmvinicius.scanner_app.data.repository

import br.com.tmvinicius.scanner_app.data.files.PdfFileStorage
import br.com.tmvinicius.scanner_app.data.files.StoredPdf
import br.com.tmvinicius.scanner_app.data.local.DocumentDao
import br.com.tmvinicius.scanner_app.data.local.DocumentEntity
import br.com.tmvinicius.scanner_app.domain.model.AppSettings
import br.com.tmvinicius.scanner_app.domain.model.DocumentError
import br.com.tmvinicius.scanner_app.domain.model.DocumentNaming
import br.com.tmvinicius.scanner_app.domain.model.ScannedPdf
import br.com.tmvinicius.scanner_app.domain.repository.Clock
import br.com.tmvinicius.scanner_app.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DocumentRepositoryImplTest {

    private val now = 1_792_029_660_000
    private val prefix = "Doc"
    private val defaultName = DocumentNaming.defaultName(prefix, now)
    private val scannedPdf = ScannedPdf(sourceUri = "content://scanner/result.pdf", pageCount = 1)

    private val dao = mockk<DocumentDao>()
    private val fileStorage = mockk<PdfFileStorage>()
    private val settingsRepository = mockk<SettingsRepository> {
        every { settings } returns flowOf(AppSettings(fileNamePrefix = prefix))
    }

    private val repository = DocumentRepositoryImpl(
        dao = dao,
        fileStorage = fileStorage,
        settingsRepository = settingsRepository,
        clock = Clock { now },
        ioDispatcher = UnconfinedTestDispatcher(),
    )

    private fun storedPdf(name: String, size: Long = 2_048) = StoredPdf(path = "/documentos/$name.pdf", sizeBytes = size)

    private fun entity(id: Long, name: String) = DocumentEntity(
        id = id,
        name = name,
        filePath = "/documentos/$name.pdf",
        sizeBytes = 2_048,
        createdAtMillis = now,
        pageCount = 1,
    )

    // --- saveScan ---

    @Test
    fun saveScan_copiesFileWithDefaultName_andCreatesRecord() = runTest {
        coEvery { fileStorage.exists(any()) } returns false
        coEvery { fileStorage.import(scannedPdf.sourceUri, "$defaultName.pdf") } returns storedPdf(defaultName)
        coEvery { dao.insert(any()) } returns 7

        val document = repository.saveScan(scannedPdf).getOrThrow()

        assertEquals(7L, document.id)
        assertEquals(defaultName, document.name)
        assertEquals("/documentos/$defaultName.pdf", document.filePath)
        assertEquals(2_048L, document.sizeBytes)
        assertEquals(now, document.createdAtMillis)
        assertEquals(1, document.pageCount)
    }

    @Test
    fun saveScan_nameTaken_addsSuffix() = runTest {
        coEvery { fileStorage.exists("$defaultName.pdf") } returns true
        coEvery { fileStorage.exists("${defaultName}_2.pdf") } returns false
        coEvery { fileStorage.import(any(), "${defaultName}_2.pdf") } returns storedPdf("${defaultName}_2")
        coEvery { dao.insert(any()) } returns 1

        val document = repository.saveScan(scannedPdf).getOrThrow()

        assertEquals("${defaultName}_2", document.name)
    }

    @Test
    fun saveScan_copyFails_returnsStorageError_withoutRecord() = runTest {
        coEvery { fileStorage.exists(any()) } returns false
        coEvery { fileStorage.import(any(), any()) } throws IOException("disk full")

        val result = repository.saveScan(scannedPdf)

        assertTrue(result.exceptionOrNull() is DocumentError.Storage)
        coVerify(exactly = 0) { dao.insert(any()) }
    }

    @Test
    fun saveScan_recordFails_deletesCopiedFile() = runTest {
        coEvery { fileStorage.exists(any()) } returns false
        coEvery { fileStorage.import(any(), any()) } returns storedPdf(defaultName)
        coEvery { dao.insert(any()) } throws IllegalStateException("db error")
        coEvery { fileStorage.delete(any()) } returns true

        val result = repository.saveScan(scannedPdf)

        assertTrue(result.exceptionOrNull() is DocumentError.Storage)
        coVerify { fileStorage.delete("/documentos/$defaultName.pdf") }
    }

    // --- rename ---

    @Test
    fun rename_renamesFileAndRecord() = runTest {
        coEvery { dao.getById(1) } returns entity(1, "Antigo")
        coEvery { fileStorage.exists("Novo.pdf") } returns false
        coEvery { fileStorage.rename("/documentos/Antigo.pdf", "Novo.pdf") } returns storedPdf("Novo")
        coEvery { dao.update(any()) } returns 1

        val document = repository.rename(1, "Novo").getOrThrow()

        assertEquals("Novo", document.name)
        assertEquals("/documentos/Novo.pdf", document.filePath)
        coVerify { dao.update(entity(1, "Novo")) }
    }

    @Test
    fun rename_sameName_changesNothing() = runTest {
        coEvery { dao.getById(1) } returns entity(1, "Igual")

        val document = repository.rename(1, "Igual").getOrThrow()

        assertEquals("Igual", document.name)
        coVerify(exactly = 0) { fileStorage.rename(any(), any()) }
    }

    @Test
    fun rename_recordFails_restoresFileName() = runTest {
        coEvery { dao.getById(1) } returns entity(1, "Antigo")
        coEvery { fileStorage.exists(any()) } returns false
        coEvery { fileStorage.rename("/documentos/Antigo.pdf", "Novo.pdf") } returns storedPdf("Novo")
        coEvery { dao.update(any()) } throws IllegalStateException("db error")
        coEvery { fileStorage.rename("/documentos/Novo.pdf", "Antigo.pdf") } returns storedPdf("Antigo")

        val result = repository.rename(1, "Novo")

        assertTrue(result.exceptionOrNull() is DocumentError.Storage)
        coVerify { fileStorage.rename("/documentos/Novo.pdf", "Antigo.pdf") }
    }

    @Test
    fun rename_missingDocument_returnsNotFound() = runTest {
        coEvery { dao.getById(9) } returns null

        val result = repository.rename(9, "Novo")

        assertTrue(result.exceptionOrNull() is DocumentError.NotFound)
    }

    // --- delete ---

    @Test
    fun delete_removesRecordAndFile() = runTest {
        coEvery { dao.getById(1) } returns entity(1, "Velho")
        coEvery { dao.deleteById(1) } returns 1
        coEvery { fileStorage.delete("/documentos/Velho.pdf") } returns true

        assertTrue(repository.delete(1).isSuccess)
    }

    @Test
    fun delete_fileCannotBeDeleted_restoresRecord() = runTest {
        val current = entity(1, "Velho")
        coEvery { dao.getById(1) } returns current
        coEvery { dao.deleteById(1) } returns 1
        coEvery { fileStorage.delete(any()) } returns false
        coEvery { dao.insert(current) } returns 1

        val result = repository.delete(1)

        assertTrue(result.exceptionOrNull() is DocumentError.Storage)
        coVerify { dao.insert(current) }
    }

    @Test
    fun delete_missingDocument_returnsNotFound() = runTest {
        coEvery { dao.getById(9) } returns null

        assertTrue(repository.delete(9).exceptionOrNull() is DocumentError.NotFound)
    }
}
