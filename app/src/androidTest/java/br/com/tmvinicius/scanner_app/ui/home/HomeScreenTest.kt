package br.com.tmvinicius.scanner_app.ui.home

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import br.com.tmvinicius.scanner_app.R
import br.com.tmvinicius.scanner_app.ui.theme.ScannerTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun emptyState_showsMessageAndScanButton() {
        composeRule.setContent { ScannerTheme { HomeScreen(onScanClick = {}) } }

        composeRule.onNodeWithText(context.getString(R.string.home_empty_message)).assertIsDisplayed()
        scanButton().assertIsDisplayed()
    }

    @Test
    fun scanButton_click_invokesCallback() {
        var clicked = false
        composeRule.setContent { ScannerTheme { HomeScreen(onScanClick = { clicked = true }) } }

        scanButton().performClick()

        assertTrue(clicked)
    }

    /** O botão Escanear, encontrado como o TalkBack o anuncia: clicável e com a descrição "Escanear". */
    private fun scanButton(): SemanticsNodeInteraction = composeRule.onNode(
        hasClickAction() and hasContentDescription(context.getString(R.string.action_scan)),
    )
}
