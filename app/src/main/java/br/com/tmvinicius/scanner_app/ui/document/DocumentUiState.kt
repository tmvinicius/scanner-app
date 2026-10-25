package br.com.tmvinicius.scanner_app.ui.document

import br.com.tmvinicius.scanner_app.ui.components.DocumentItemUi

/**
 * Estado da tela Documento. [document] nulo (sem [isLoading]) = documento não encontrado.
 *
 * @property isLoading `true` até a primeira leitura do banco.
 */
data class DocumentUiState(
    val document: DocumentItemUi? = null,
    val isLoading: Boolean = false,
)
