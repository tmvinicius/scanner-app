package br.com.tmvinicius.scanner_app.ui.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.tmvinicius.scanner_app.R
import br.com.tmvinicius.scanner_app.ui.components.BackTopAppBar
import br.com.tmvinicius.scanner_app.ui.theme.ScannerTheme

/** Composable de rota. O SettingsViewModel (DataStore) chega no módulo M5. */
@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsScreen(onBack = onBack, modifier = modifier)
}

/** Tela Configurações (esqueleto). Tema, padrão de nome, privacidade e versão chegam no M5. */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            BackTopAppBar(title = stringResource(R.string.settings_title), onBack = onBack)
        },
    ) { innerPadding ->
        Text(
            text = stringResource(R.string.settings_placeholder),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
        )
    }
}

@Preview(name = "Claro", showBackground = true)
@Preview(name = "Escuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsScreenPreview() {
    ScannerTheme(dynamicColor = false) {
        SettingsScreen(onBack = {})
    }
}
