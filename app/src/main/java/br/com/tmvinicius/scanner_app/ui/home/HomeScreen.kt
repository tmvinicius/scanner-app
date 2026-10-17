package br.com.tmvinicius.scanner_app.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.tmvinicius.scanner_app.R
import br.com.tmvinicius.scanner_app.ui.theme.ScannerTheme

/**
 * Tela inicial. Por enquanto mostra apenas o estado vazio e o botão Escanear;
 * a lista de documentos e o HomeViewModel chegam nos módulos M1/M2.
 */
@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
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
        HomeEmptyState(modifier = Modifier.padding(innerPadding))
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

@Preview(name = "Claro", showBackground = true)
@Preview(name = "Escuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenPreview() {
    ScannerTheme(dynamicColor = false) {
        HomeScreen(onScanClick = {})
    }
}
