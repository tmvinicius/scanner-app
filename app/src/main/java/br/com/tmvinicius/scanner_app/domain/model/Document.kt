package br.com.tmvinicius.scanner_app.domain.model

/**
 * Um PDF guardado pelo app.
 *
 * @property name nome exibido, que também é o nome do arquivo (sem a extensão `.pdf`).
 * @property filePath caminho absoluto do arquivo no armazenamento interno do app.
 * @property createdAtMillis data de criação em milissegundos desde a época Unix.
 */
data class Document(
    val id: Long,
    val name: String,
    val filePath: String,
    val sizeBytes: Long,
    val createdAtMillis: Long,
    val pageCount: Int,
)
