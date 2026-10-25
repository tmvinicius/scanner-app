package br.com.tmvinicius.scanner_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.tmvinicius.scanner_app.ui.document.DocumentRoute
import br.com.tmvinicius.scanner_app.ui.home.HomeRoute
import br.com.tmvinicius.scanner_app.ui.settings.SettingsRoute

/** Grafo de navegação do app: Início → Documento e Início → Configurações. */
@Composable
fun ScannerNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination,
        modifier = modifier,
    ) {
        composable<HomeDestination> {
            HomeRoute(
                onDocumentClick = { documentId -> navController.navigate(DocumentDestination(documentId)) },
                onSettingsClick = { navController.navigate(SettingsDestination) },
            )
        }
        composable<DocumentDestination> {
            DocumentRoute(onBack = { navController.navigateUp() })
        }
        composable<SettingsDestination> {
            SettingsRoute(onBack = { navController.navigateUp() })
        }
    }
}
