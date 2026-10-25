package br.com.tmvinicius.scanner_app.ui.sample

import br.com.tmvinicius.scanner_app.ui.components.DocumentItemUi

/**
 * Documentos de exemplo para previews e testes.
 *
 * Até o módulo M2 (Room + arquivos), os ViewModels também usam esta lista para que a
 * navegação possa ser exercitada; no M2 eles passam a ler o repositório.
 */
object SampleDocuments {

    val items: List<DocumentItemUi> = listOf(
        DocumentItemUi(
            id = 1,
            name = "Documento_2026-10-14_2301",
            createdAtMillis = 1_792_029_660_000, // 14/10/2026 23:01 (UTC-3)
            sizeBytes = 245_760,
        ),
        DocumentItemUi(
            id = 2,
            name = "Documento_2026-10-27_0915",
            createdAtMillis = 1_793_103_300_000, // 27/10/2026 09:15 (UTC-3)
            sizeBytes = 1_153_434,
        ),
        DocumentItemUi(
            id = 3,
            name = "Comprovante de residência",
            createdAtMillis = 1_792_618_920_000, // 21/10/2026 18:42 (UTC-3)
            sizeBytes = 87_040,
        ),
    )

    fun findById(id: Long): DocumentItemUi? = items.firstOrNull { it.id == id }
}
