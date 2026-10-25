package br.com.tmvinicius.scanner_app.ui.home

import br.com.tmvinicius.scanner_app.ui.components.DocumentItemUi

/** Estado da tela inicial. Lista vazia = primeira abertura (estado vazio). */
data class HomeUiState(
    val documents: List<DocumentItemUi> = emptyList(),
)
