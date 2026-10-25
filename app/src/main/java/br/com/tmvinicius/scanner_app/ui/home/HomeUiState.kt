package br.com.tmvinicius.scanner_app.ui.home

import br.com.tmvinicius.scanner_app.ui.components.DocumentItemUi

/**
 * Estado da tela inicial. Lista vazia = primeira abertura (estado vazio).
 *
 * @property isLoading `true` até a primeira leitura do banco, para não piscar o estado vazio.
 */
data class HomeUiState(
    val documents: List<DocumentItemUi> = emptyList(),
    val isLoading: Boolean = false,
)
