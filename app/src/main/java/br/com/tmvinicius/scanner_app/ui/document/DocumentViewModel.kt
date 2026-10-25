package br.com.tmvinicius.scanner_app.ui.document

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.tmvinicius.scanner_app.domain.model.Document
import br.com.tmvinicius.scanner_app.domain.usecase.ObserveDocument
import br.com.tmvinicius.scanner_app.ui.components.toItemUi
import br.com.tmvinicius.scanner_app.ui.navigation.DocumentDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class DocumentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeDocument: ObserveDocument,
) : ViewModel() {

    /** Argumento da rota [DocumentDestination]. */
    private val documentId: Long? = savedStateHandle[DOCUMENT_ID_KEY]

    private val document: Flow<Document?> = documentId?.let(observeDocument::invoke) ?: flowOf(null)

    val uiState: StateFlow<DocumentUiState> = document
        .map { DocumentUiState(document = it?.toItemUi(), isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = DocumentUiState(isLoading = true),
        )

    companion object {
        /** Mesmo nome da propriedade em [DocumentDestination]. */
        const val DOCUMENT_ID_KEY = "documentId"

        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
