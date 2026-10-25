package br.com.tmvinicius.scanner_app.domain.usecase

import br.com.tmvinicius.scanner_app.domain.model.DocumentError
import br.com.tmvinicius.scanner_app.domain.repository.DocumentRepository
import br.com.tmvinicius.scanner_app.testutil.testDocument
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RenameDocumentTest {

    private val repository = mockk<DocumentRepository>()
    private val renameDocument = RenameDocument(repository)

    @Test
    fun nameWithForbiddenCharacters_isSanitizedBeforeRenaming() = runTest {
        val renamed = testDocument(id = 1, name = "Nota_ fiscal_")
        coEvery { repository.rename(1, "Nota_ fiscal_") } returns Result.success(renamed)

        val result = renameDocument(1, "  Nota: fiscal?  ")

        assertEquals(renamed, result.getOrNull())
    }

    @Test
    fun blankName_failsWithoutTouchingRepository() = runTest {
        val result = renameDocument(1, "  ...  ")

        assertTrue(result.exceptionOrNull() is DocumentError.InvalidName)
        coVerify(exactly = 0) { repository.rename(any(), any()) }
    }
}
