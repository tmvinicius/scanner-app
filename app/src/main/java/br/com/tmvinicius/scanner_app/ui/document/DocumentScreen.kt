package br.com.tmvinicius.scanner_app.ui.document

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.tmvinicius.scanner_app.R
import br.com.tmvinicius.scanner_app.ui.components.BackTopAppBar
import br.com.tmvinicius.scanner_app.ui.sample.SampleDocuments
import br.com.tmvinicius.scanner_app.ui.theme.ScannerTheme

/** Composable de rota: liga a tela Documento ao [DocumentViewModel]. */
@Composable
fun DocumentRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DocumentViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DocumentScreen(uiState = uiState, onBack = onBack, modifier = modifier)
}

/**
 * Tela Documento (esqueleto). A prévia da página e as ações (compartilhar, salvar,
 * abrir, renomear, excluir) chegam no módulo M4.
 */
@Composable
fun DocumentScreen(
    uiState: DocumentUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            BackTopAppBar(
                title = uiState.document?.name ?: stringResource(R.string.document_title_fallback),
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        // Ainda lendo o banco: não mostra "não encontrado" antes da hora.
        if (uiState.isLoading) return@Scaffold
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_document),
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(
                    if (uiState.document == null) R.string.document_not_found else R.string.document_preview_placeholder,
                ),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(name = "Claro", showBackground = true)
@Preview(name = "Escuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DocumentScreenPreview() {
    ScannerTheme(dynamicColor = false) {
        DocumentScreen(
            uiState = DocumentUiState(document = SampleDocuments.items.first()),
            onBack = {},
        )
    }
}
