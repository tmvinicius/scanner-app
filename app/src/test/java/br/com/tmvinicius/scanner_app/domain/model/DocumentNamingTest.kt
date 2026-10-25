package br.com.tmvinicius.scanner_app.domain.model

import java.util.TimeZone
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DocumentNamingTest {

    private val brasilia = TimeZone.getTimeZone("GMT-03:00")

    /** 14/10/2026 23:01 em UTC-3. */
    private val createdAt = 1_792_029_660_000

    @Test
    fun defaultName_usesPrefixDateAndTime() {
        assertEquals(
            "Documento_2026-10-14_2301",
            DocumentNaming.defaultName("Documento", createdAt, brasilia),
        )
    }

    @Test
    fun defaultName_blankPrefix_fallsBackToDefault() {
        assertEquals(
            "Documento_2026-10-14_2301",
            DocumentNaming.defaultName(" ... ", createdAt, brasilia),
        )
    }

    @Test
    fun sanitize_replacesForbiddenCharacters() {
        assertEquals("Nota_ fiscal_ 2026_10", DocumentNaming.sanitize("Nota: fiscal? 2026/10"))
    }

    @Test
    fun sanitize_collapsesSpacesAndTrimsDots() {
        assertEquals("Contrato de aluguel", DocumentNaming.sanitize("  ..Contrato   de aluguel.  "))
    }

    @Test
    fun sanitize_onlyDotsAndSpaces_returnsEmpty() {
        assertEquals("", DocumentNaming.sanitize(" ... "))
    }

    @Test
    fun sanitize_limitsLength() {
        assertEquals(DocumentNaming.MAX_NAME_LENGTH, DocumentNaming.sanitize("a".repeat(300)).length)
    }

    @Test
    fun uniqueName_freeName_isKept() = runTest {
        assertEquals("Contrato", DocumentNaming.uniqueName("Contrato") { false })
    }

    @Test
    fun uniqueName_takenNames_getNextSuffix() = runTest {
        val taken = setOf("Contrato", "Contrato_2")

        assertEquals("Contrato_3", DocumentNaming.uniqueName("Contrato") { it in taken })
    }
}
