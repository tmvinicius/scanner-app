package br.com.tmvinicius.scanner_app.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.tmvinicius.scanner_app.MainActivity
import br.com.tmvinicius.scanner_app.R
import br.com.tmvinicius.scanner_app.data.local.DocumentDao
import br.com.tmvinicius.scanner_app.data.local.DocumentEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Navegação com o app de verdade (Hilt + banco em memória do TestDatabaseModule):
 * Início → Documento → voltar e Início → Configurações → voltar.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ScannerNavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var documentDao: DocumentDao

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    private fun string(id: Int) = composeRule.activity.getString(id)

    private fun waitForText(text: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun noDocuments_showsEmptyState() {
        waitForText(string(R.string.home_empty_message))
    }

    @Test
    fun home_toDocument_andBack() {
        val name = "Contrato de aluguel"
        runBlocking {
            documentDao.insert(
                DocumentEntity(
                    name = name,
                    filePath = "/documentos/$name.pdf",
                    sizeBytes = 120_000,
                    createdAtMillis = 1_793_103_300_000,
                    pageCount = 1,
                ),
            )
        }
        waitForText(name)

        composeRule.onNodeWithText(name).performClick()
        // A tela Documento lê o banco em segundo plano antes de mostrar o conteúdo.
        waitForText(string(R.string.document_preview_placeholder))
        composeRule.onNodeWithText(string(R.string.document_preview_placeholder)).assertIsDisplayed()

        composeRule.onNodeWithContentDescription(string(R.string.action_back)).performClick()
        composeRule.onNodeWithText(name).assertIsDisplayed()
    }

    @Test
    fun home_toSettings_andBack() {
        composeRule.onNodeWithContentDescription(string(R.string.action_settings)).performClick()
        composeRule.onNodeWithText(string(R.string.settings_placeholder)).assertIsDisplayed()

        composeRule.onNodeWithContentDescription(string(R.string.action_back)).performClick()
        composeRule.onNodeWithContentDescription(string(R.string.action_settings)).assertIsDisplayed()
    }
}