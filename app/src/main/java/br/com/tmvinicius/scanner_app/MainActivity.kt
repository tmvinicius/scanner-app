package br.com.tmvinicius.scanner_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import br.com.tmvinicius.scanner_app.ui.navigation.ScannerNavHost
import br.com.tmvinicius.scanner_app.ui.theme.ScannerTheme
import dagger.hilt.android.AndroidEntryPoint

/** Única Activity do app: aplica o tema e hospeda o grafo de navegação Compose. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Troca o Theme.Scanner.Starting pelo tema do app (postSplashScreenTheme).
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScannerTheme {
                // M3 liga o botão Escanear ao ML Kit.
                ScannerNavHost()
            }
        }
    }
}