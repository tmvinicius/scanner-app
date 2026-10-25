package br.com.tmvinicius.scanner_app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.tmvinicius.scanner_app.domain.usecase.ObserveDocuments
import br.com.tmvinicius.scanner_app.ui.components.toItemUi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeDocuments: ObserveDocuments,
) : ViewModel() {

    /** Lista vinda do repositório, já ordenada pelo banco (mais recentes primeiro). */
    val uiState: StateFlow<HomeUiState> = observeDocuments()
        .map { documents -> HomeUiState(documents = documents.map { it.toItemUi() }, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = HomeUiState(isLoading = true),
        )

    fun onScanClick() {
        // M3: abrir o scanner do ML Kit.
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
