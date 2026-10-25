package br.com.tmvinicius.scanner_app.ui.document

import androidx.lifecycle.SavedStateHandle
import br.com.tmvinicius.scanner_app.domain.usecase.ObserveDocument
import br.com.tmvinicius.scanner_app.testutil.FakeDocumentRepository
import br.com.tmvinicius.scanner_app.testutil.MainDispatcherRule
import br.com.tmvinicius.scanner_app.testutil.testDocument
import br.com.tmvinicius.scanner_app.ui.components.toItemUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DocumentViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val document = testDocument(id = 5)
    private val repository = FakeDocumentRepository(listOf(document))

    private fun createViewModel(documentId: Long?) = DocumentViewModel(
        SavedStateHandle(mapOf(DocumentViewModel.DOCUMENT_ID_KEY to documentId)),
        ObserveDocument(repository),
    )

    /** Cria o ViewModel e mantém o [DocumentViewModel.uiState] coletado durante o teste. */
    private fun TestScope.viewModelFor(documentId: Long?): DocumentViewModel {
        val viewModel = createViewModel(documentId)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    @Test
    fun beforeFirstLoad_isLoading() {
        val state = createViewModel(document.id).uiState.value

        assertNull(state.document)
        assertTrue(state.isLoading)
    }

    @Test
    fun knownId_showsDocument() = runTest {
        val state = viewModelFor(document.id).uiState.value

        assertEquals(DocumentUiState(document = document.toItemUi(), isLoading = false), state)
    }

    @Test
    fun unknownId_showsNotFound() = runTest {
        val state = viewModelFor(documentId = 99).uiState.value

        assertEquals(DocumentUiState(document = null, isLoading = false), state)
    }

    @Test
    fun missingId_showsNotFound() = runTest {
        val state = viewModelFor(documentId = null).uiState.value

        assertEquals(DocumentUiState(document = null, isLoading = false), state)
    }

    @Test
    fun documentDeleted_switchesToNotFound() = runTest {
        val viewModel = viewModelFor(document.id)
        assertEquals(document.toItemUi(), viewModel.uiState.value.document)

        repository.delete(document.id)

        val state = viewModel.uiState.value
        assertNull(state.document)
        assertFalse(state.isLoading)
    }
}
