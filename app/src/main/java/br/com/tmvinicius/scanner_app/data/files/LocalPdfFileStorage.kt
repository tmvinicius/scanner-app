package br.com.tmvinicius.scanner_app.data.files

import android.content.ContentResolver
import androidx.core.net.toUri
import br.com.tmvinicius.scanner_app.di.DocumentsDir
import br.com.tmvinicius.scanner_app.di.IoDispatcher
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/** Implementação sobre o sistema de arquivos, na pasta `filesDir/documentos`. */
class LocalPdfFileStorage @Inject constructor(
    @param:DocumentsDir private val documentsDir: File,
    private val contentResolver: ContentResolver,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : PdfFileStorage {

    override suspend fun exists(fileName: String): Boolean = withContext(ioDispatcher) {
        File(documentsDir, fileName).exists()
    }

    override suspend fun import(sourceUri: String, fileName: String): StoredPdf = withContext(ioDispatcher) {
        ensureDocumentsDir()
        val target = File(documentsDir, fileName)
        if (target.exists()) throw IOException("File already exists: ${target.name}")
        try {
            val input = contentResolver.openInputStream(sourceUri.toUri())
                ?: throw IOException("Could not open $sourceUri")
            input.use { source -> target.outputStream().use { source.copyTo(it) } }
        } catch (e: Exception) {
            // Não deixa arquivo pela metade para trás.
            target.delete()
            throw e
        }
        target.toStoredPdf()
    }

    override suspend fun rename(path: String, newFileName: String): StoredPdf = withContext(ioDispatcher) {
        val source = File(path)
        val target = File(source.parentFile ?: documentsDir, newFileName)
        if (target.exists()) throw IOException("File already exists: ${target.name}")
        if (!source.renameTo(target)) throw IOException("Could not rename ${source.name} to ${target.name}")
        target.toStoredPdf()
    }

    override suspend fun delete(path: String): Boolean = withContext(ioDispatcher) {
        val file = File(path)
        file.delete() || !file.exists()
    }

    private fun ensureDocumentsDir() {
        if (!documentsDir.isDirectory && !documentsDir.mkdirs()) {
            throw IOException("Could not create ${documentsDir.path}")
        }
    }

    private fun File.toStoredPdf() = StoredPdf(path = absolutePath, sizeBytes = length())
}
