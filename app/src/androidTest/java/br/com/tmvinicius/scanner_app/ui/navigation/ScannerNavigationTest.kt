package br.com.tmvinicius.scanner_app.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.tmvinicius.scanner_app.MainActivity
import br.com.tmvinicius.scanner_app.R
import br.com.tmvinicius.scanner_app.ui.sample.SampleDocuments
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Critério de aceite do M1: Início → Documento → voltar e Início → Configurações → voltar. */
@RunWith(AndroidJUnit4::class)
class ScannerNavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun string(id: Int) = composeRule.activity.getString(id)

    @Test
    fun home_toDocument_andBack() {
        val document = SampleDocuments.items.first()

        composeRule.onNodeWithText(document.name).performClick()
        composeRule.onNodeWithText(string(R.string.document_preview_placeholder)).assertIsDisplayed()

        composeRule.onNodeWithContentDescription(string(R.string.action_back)).performClick()
        composeRule.onNodeWithContentDescription(string(R.string.action_settings)).assertIsDisplayed()
    }

    @Test
    fun home_toSettings_andBack() {
        composeRule.onNodeWithContentDescription(string(R.string.action_settings)).performClick()
        composeRule.onNodeWithText(string(R.string.settings_placeholder)).assertIsDisplayed()

        composeRule.onNodeWithContentDescription(string(R.string.action_back)).performClick()
        composeRule.onNodeWithContentDescription(string(R.string.action_settings)).assertIsDisplayed()
    }
}
