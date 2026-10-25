package br.com.tmvinicius.scanner_app.ui.components

import br.com.tmvinicius.scanner_app.domain.model.Document

/** Dados de um documento no formato que as telas exibem. */
data class DocumentItemUi(
    val id: Long,
    val name: String,
    val createdAtMillis: Long,
    val sizeBytes: Long,
)

/** Converte o modelo de domínio para o formato exibido nas telas. */
fun Document.toItemUi(): DocumentItemUi = DocumentItemUi(
    id = id,
    name = name,
    createdAtMillis = createdAtMillis,
    sizeBytes = sizeBytes,
)
