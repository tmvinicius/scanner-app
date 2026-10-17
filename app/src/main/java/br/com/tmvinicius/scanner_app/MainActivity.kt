package br.com.tmvinicius.scanner_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.tmvinicius.scanner_app.ui.home.HomeScreen
import br.com.tmvinicius.scanner_app.ui.theme.ScannerTheme
import dagger.hilt.android.AndroidEntryPoint

/** Única Activity do app: aplica o tema e hospeda as telas Compose. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScannerTheme {
                // M1 troca esta chamada pelo NavHost; M3 liga o botão ao ML Kit.
                HomeScreen(onScanClick = {})
            }
        }
    }
}