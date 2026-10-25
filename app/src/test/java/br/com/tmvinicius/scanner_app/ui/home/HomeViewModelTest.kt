package br.com.tmvinicius.scanner_app.ui.home

import br.com.tmvinicius.scanner_app.domain.usecase.ObserveDocuments
import br.com.tmvinicius.scanner_app.testutil.FakeDocumentRepository
import br.com.tmvinicius.scanner_app.testutil.MainDispatcherRule
import br.com.tmvinicius.scanner_app.testutil.testDocument
import br.com.tmvinicius.scanner_app.ui.components.toItemUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeDocumentRepository()
    // Criado só quando o teste roda, depois que a MainDispatcherRule trocou o Dispatchers.Main.
    private val viewModel by lazy { HomeViewModel(ObserveDocuments(repository)) }

    @Test
    fun beforeFirstLoad_isLoading() {
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun emptyRepository_showsEmptyState() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        assertEquals(HomeUiState(documents = emptyList(), isLoading = false), viewModel.uiState.value)
    }

    @Test
    fun repositoryChanges_updateList() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        val documents = listOf(testDocument(id = 2), testDocument(id = 1))

        repository.documents.value = documents

        assertEquals(documents.map { it.toItemUi() }, viewModel.uiState.value.documents)
    }
}
