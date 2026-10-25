package br.com.tmvinicius.scanner_app.data.prefs

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import br.com.tmvinicius.scanner_app.domain.model.AppSettings
import br.com.tmvinicius.scanner_app.domain.model.DocumentNaming
import br.com.tmvinicius.scanner_app.domain.model.ThemeMode
import java.io.File
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DataStoreSettingsRepositoryTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private fun TestScope.repository() = DataStoreSettingsRepository(
        PreferenceDataStoreFactory.create(
            scope = backgroundScope,
            produceFile = { File(tempFolder.root, "settings.preferences_pb") },
        ),
    )

    @Test
    fun noSavedValues_returnsDefaults() = runTest {
        assertEquals(AppSettings(), repository().settings.first())
    }

    @Test
    fun savedValues_areReadBack() = runTest {
        val repository = repository()

        repository.setThemeMode(ThemeMode.DARK)
        repository.setFileNamePrefix("Contrato")

        assertEquals(AppSettings(ThemeMode.DARK, "Contrato"), repository.settings.first())
    }

    @Test
    fun prefix_isSanitized_andBlankFallsBackToDefault() = runTest {
        val repository = repository()

        repository.setFileNamePrefix("Nota/fiscal")
        assertEquals("Nota_fiscal", repository.settings.first().fileNamePrefix)

        repository.setFileNamePrefix("   ")
        assertEquals(DocumentNaming.DEFAULT_PREFIX, repository.settings.first().fileNamePrefix)
    }
}
