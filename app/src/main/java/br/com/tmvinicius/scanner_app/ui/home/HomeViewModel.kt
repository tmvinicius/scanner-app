package br.com.tmvinicius.scanner_app.ui.home

import androidx.lifecycle.ViewModel
import br.com.tmvinicius.scanner_app.ui.sample.SampleDocuments
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    // M2: trocar pela lista vinda do repositório (já ordenada pelo banco).
    private val _uiState = MutableStateFlow(
        HomeUiState(documents = SampleDocuments.items.sortedByDescending { it.createdAtMillis }),
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onScanClick() {
        // M3: abrir o scanner do ML Kit.
    }
}
