package br.com.tmvinicius.scanner_app.ui.navigation

import kotlinx.serialization.Serializable

/** Tela inicial: lista de documentos. */
@Serializable
data object HomeDestination

/** Tela de um documento. O nome da propriedade é a chave do argumento no SavedStateHandle. */
@Serializable
data class DocumentDestination(val documentId: Long)

/** Tela de configurações. */
@Serializable
data object SettingsDestination
