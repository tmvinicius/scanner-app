package br.com.tmvinicius.scanner_app.data.files

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.io.File
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class LocalPdfFileStorageTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var root: File
    private lateinit var documentsDir: File
    private lateinit var storage: LocalPdfFileStorage

    private val pdfBytes = "%PDF-1.7 conteúdo de teste".toByteArray()

    @Before
    fun setUp() {
        root = File(context.cacheDir, "pdf-storage-test").apply { deleteRecursively(); mkdirs() }
        documentsDir = File(root, "documentos")
        storage = LocalPdfFileStorage(documentsDir, context.contentResolver, UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        root.deleteRecursively()
    }

    private fun sourceUri(): String {
        val source = File(root, "scan.pdf").apply { writeBytes(pdfBytes) }
        return Uri.fromFile(source).toString()
    }

    @Test
    fun import_copiesContentIntoDocumentsDir() = runTest {
        val stored = storage.import(sourceUri(), "Contrato.pdf")

        val file = File(stored.path)
        assertEquals(documentsDir.absolutePath, file.parentFile?.absolutePath)
        assertArrayEquals(pdfBytes, file.readBytes())
        assertEquals(pdfBytes.size.toLong(), stored.sizeBytes)
        assertTrue(storage.exists("Contrato.pdf"))
    }

    @Test(expected = IOException::class)
    fun import_existingName_fails() = runTest {
        storage.import(sourceUri(), "Contrato.pdf")
        storage.import(sourceUri(), "Contrato.pdf")
    }

    @Test
    fun rename_movesFileToNewName() = runTest {
        val stored = storage.import(sourceUri(), "Antigo.pdf")

        val renamed = storage.rename(stored.path, "Novo.pdf")

        assertFalse(storage.exists("Antigo.pdf"))
        assertTrue(storage.exists("Novo.pdf"))
        assertArrayEquals(pdfBytes, File(renamed.path).readBytes())
    }

    @Test
    fun delete_removesFile_andIsIdempotent() = runTest {
        val stored = storage.import(sourceUri(), "Contrato.pdf")

        assertTrue(storage.delete(stored.path))
        assertFalse(storage.exists("Contrato.pdf"))
        assertTrue(storage.delete(stored.path))
    }
}
