package br.com.tmvinicius.scanner_app.ui.components

/** Dados de um documento no formato que as telas exibem. */
data class DocumentItemUi(
    val id: Long,
    val name: String,
    val createdAtMillis: Long,
    val sizeBytes: Long,
)
