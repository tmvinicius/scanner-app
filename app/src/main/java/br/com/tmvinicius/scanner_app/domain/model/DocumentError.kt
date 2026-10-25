package br.com.tmvinicius.scanner_app.domain.model

/** Falhas das operações com documentos, entregues dentro de um [Result]. */
sealed class DocumentError(message: String, cause: Throwable? = null) : Exception(message, cause) {

    class NotFound(val documentId: Long) : DocumentError("Document $documentId not found")

    /** O nome ficou vazio depois de removidos os caracteres não permitidos. */
    class InvalidName(val name: String) : DocumentError("Invalid document name: \"$name\"")

    /** Falha ao ler, gravar ou apagar o arquivo, ou ao atualizar o banco. */
    class Storage(cause: Throwable) : DocumentError("Storage operation failed", cause)
}
