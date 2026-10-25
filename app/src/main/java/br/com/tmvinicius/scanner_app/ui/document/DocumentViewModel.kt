package br.com.tmvinicius.scanner_app.ui.document

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import br.com.tmvinicius.scanner_app.ui.navigation.DocumentDestination
import br.com.tmvinicius.scanner_app.ui.sample.SampleDocuments
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class DocumentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /** Argumento da rota [DocumentDestination]. */
    private val documentId: Long? = savedStateHandle[DOCUMENT_ID_KEY]

    // M2: buscar o documento no repositório.
    private val _uiState = MutableStateFlow(
        DocumentUiState(document = documentId?.let(SampleDocuments::findById)),
    )
    val uiState: StateFlow<DocumentUiState> = _uiState.asStateFlow()

    companion object {
        /** Mesmo nome da propriedade em [DocumentDestination]. */
        const val DOCUMENT_ID_KEY = "documentId"
    }
}
