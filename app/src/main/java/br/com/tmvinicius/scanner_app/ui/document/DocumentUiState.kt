package br.com.tmvinicius.scanner_app.ui.document

import br.com.tmvinicius.scanner_app.ui.components.DocumentItemUi

/** Estado da tela Documento. [document] nulo = documento não encontrado. */
data class DocumentUiState(
    val document: DocumentItemUi? = null,
)
