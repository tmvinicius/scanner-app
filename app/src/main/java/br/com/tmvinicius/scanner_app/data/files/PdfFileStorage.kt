package br.com.tmvinicius.scanner_app.data.files

/** Arquivo PDF guardado na pasta de documentos do app. */
data class StoredPdf(
    val path: String,
    val sizeBytes: Long,
)

/** Arquivos PDF na pasta interna `documentos/`. As funções lançam exceção em caso de falha. */
interface PdfFileStorage {

    /** Se já existe um arquivo com este nome (com extensão) na pasta de documentos. */
    suspend fun exists(fileName: String): Boolean

    /** Copia o conteúdo de [sourceUri] para um arquivo novo chamado [fileName]. */
    suspend fun import(sourceUri: String, fileName: String): StoredPdf

    /** Renomeia o arquivo em [path] para [newFileName] na mesma pasta. */
    suspend fun rename(path: String, newFileName: String): StoredPdf

    /** Apaga o arquivo. Devolve `true` se ele não existe mais ao final. */
    suspend fun delete(path: String): Boolean
}
