package br.com.tmvinicius.scanner_app.ui.home

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import br.com.tmvinicius.scanner_app.R
import br.com.tmvinicius.scanner_app.ui.sample.SampleDocuments
import br.com.tmvinicius.scanner_app.ui.theme.ScannerTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun setHomeScreen(
        uiState: HomeUiState = HomeUiState(),
        onScanClick: () -> Unit = {},
        onDocumentClick: (Long) -> Unit = {},
        onSettingsClick: () -> Unit = {},
    ) {
        composeRule.setContent {
            ScannerTheme {
                HomeScreen(
                    uiState = uiState,
                    onScanClick = onScanClick,
                    onDocumentClick = onDocumentClick,
                    onSettingsClick = onSettingsClick,
                )
            }
        }
    }

    @Test
    fun emptyState_showsMessageAndScanButton() {
        setHomeScreen()

        composeRule.onNodeWithText(context.getString(R.string.home_empty_message)).assertIsDisplayed()
        scanButton().assertIsDisplayed()
    }

    @Test
    fun scanButton_click_invokesCallback() {
        var clicked = false
        setHomeScreen(onScanClick = { clicked = true })

        scanButton().performClick()

        assertTrue(clicked)
    }

    @Test
    fun withDocuments_showsListInsteadOfEmptyState() {
        setHomeScreen(uiState = HomeUiState(documents = SampleDocuments.items))

        SampleDocuments.items.forEach { composeRule.onNodeWithText(it.name).assertIsDisplayed() }
        composeRule.onNodeWithText(context.getString(R.string.home_empty_message)).assertDoesNotExist()
    }

    @Test
    fun documentClick_passesDocumentId() {
        val target = SampleDocuments.items[1]
        var clickedId: Long? = null
        setHomeScreen(
            uiState = HomeUiState(documents = SampleDocuments.items),
            onDocumentClick = { clickedId = it },
        )

        composeRule.onNodeWithText(target.name).performClick()

        assertEquals(target.id, clickedId)
    }

    @Test
    fun settingsButton_click_invokesCallback() {
        var clicked = false
        setHomeScreen(onSettingsClick = { clicked = true })

        composeRule.onNodeWithContentDescription(context.getString(R.string.action_settings)).performClick()

        assertTrue(clicked)
    }

    /** O botão Escanear, encontrado como o TalkBack o anuncia: clicável e com a descrição "Escanear". */
    private fun scanButton(): SemanticsNodeInteraction = composeRule.onNode(
        hasClickAction() and hasContentDescription(context.getString(R.string.action_scan)),
    )
}