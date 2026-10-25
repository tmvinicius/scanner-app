package br.com.tmvinicius.scanner_app.data.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import br.com.tmvinicius.scanner_app.domain.model.AppSettings
import br.com.tmvinicius.scanner_app.domain.model.DocumentNaming
import br.com.tmvinicius.scanner_app.domain.model.ThemeMode
import br.com.tmvinicius.scanner_app.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override val settings: Flow<AppSettings> = dataStore.data.map { preferences ->
        AppSettings(
            themeMode = preferences[THEME_MODE]
                ?.let { stored -> ThemeMode.entries.firstOrNull { it.name == stored } }
                ?: ThemeMode.SYSTEM,
            fileNamePrefix = preferences[FILE_NAME_PREFIX] ?: DocumentNaming.DEFAULT_PREFIX,
        )
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.edit { it[THEME_MODE] = themeMode.name }
    }

    override suspend fun setFileNamePrefix(prefix: String) {
        val sanitized = DocumentNaming.sanitize(prefix).ifEmpty { DocumentNaming.DEFAULT_PREFIX }
        dataStore.edit { it[FILE_NAME_PREFIX] = sanitized }
    }

    private companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val FILE_NAME_PREFIX = stringPreferencesKey("file_name_prefix")
    }
}
