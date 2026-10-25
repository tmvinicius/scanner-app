package br.com.tmvinicius.scanner_app.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import br.com.tmvinicius.scanner_app.ui.components.DocumentItemUi
import br.com.tmvinicius.scanner_app.ui.components.DocumentListItem
import br.com.tmvinicius.scanner_app.ui.sample.SampleDocuments
import br.com.tmvinicius.scanner_app.ui.theme.ScannerTheme

/** Espaço livre no fim da lista para o último item não ficar embaixo do botão Escanear. */
private val FabClearance = 88.dp

/** Composable de rota: liga a tela inicial ao [HomeViewModel]. */
@Composable
fun HomeRoute(
    onDocumentClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        onScanClick = viewModel::onScanClick,
        onDocumentClick = onDocumentClick,
        onSettingsClick = onSettingsClick,
        modifier = modifier,
    )
}

/** Tela inicial: lista de documentos (mais recentes primeiro) ou estado vazio. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onScanClick: () -> Unit,
    onDocumentClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = stringResource(R.string.action_settings),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            val scanLabel = stringResource(R.string.action_scan)
            ExtendedFloatingActionButton(
                onClick = onScanClick,
                icon = {
                    // O ExtendedFloatingActionButton remove a semântica do texto do nó do botão;
                    // a descrição do ícone é o que o TalkBack (e os testes) enxergam.
                    Icon(
                        painter = painterResource(R.drawable.ic_document),
                        contentDescription = scanLabel,
                    )
                },
                text = { Text(text = scanLabel) },
            )
        },
    ) { innerPadding ->
        if (uiState.documents.isEmpty()) {
            HomeEmptyState(modifier = Modifier.padding(innerPadding))
        } else {
            DocumentList(
                documents = uiState.documents,
                onDocumentClick = onDocumentClick,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun DocumentList(
    documents: List<DocumentItemUi>,
    onDocumentClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = FabClearance),
    ) {
        items(items = documents, key = { it.id }) { document ->
            DocumentListItem(
                document = document,
                onClick = { onDocumentClick(document.id) },
            )
        }
    }
}

@Composable
private fun HomeEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_document),
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(R.string.home_empty_message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(name = "Vazio · claro", showBackground = true)
@Preview(name = "Vazio · escuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenEmptyPreview() {
    ScannerTheme(dynamicColor = false) {
        HomeScreen(
            uiState = HomeUiState(),
            onScanClick = {},
            onDocumentClick = {},
            onSettingsClick = {},
        )
    }
}

@Preview(name = "Com documentos · claro", showBackground = true)
@Preview(name = "Com documentos · escuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenWithDocumentsPreview() {
    ScannerTheme(dynamicColor = false) {
        HomeScreen(
            uiState = HomeUiState(documents = SampleDocuments.items),
            onScanClick = {},
            onDocumentClick = {},
            onSettingsClick = {},
        )
    }
}