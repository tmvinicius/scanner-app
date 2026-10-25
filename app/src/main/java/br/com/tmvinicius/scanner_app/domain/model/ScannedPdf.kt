package br.com.tmvinicius.scanner_app.domain.model

/**
 * PDF recém-gerado pelo scanner, ainda fora do armazenamento do app.
 *
 * @property sourceUri URI (em texto) de onde o PDF pode ser lido.
 */
data class ScannedPdf(
    val sourceUri: String,
    val pageCount: Int,
)
