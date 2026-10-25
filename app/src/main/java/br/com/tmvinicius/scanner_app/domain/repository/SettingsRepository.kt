package br.com.tmvinicius.scanner_app.domain.repository

import br.com.tmvinicius.scanner_app.domain.model.AppSettings
import br.com.tmvinicius.scanner_app.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

/** Preferências do usuário (tema e prefixo do nome dos arquivos). */
interface SettingsRepository {

    val settings: Flow<AppSettings>

    suspend fun setThemeMode(themeMode: ThemeMode)

    /** Um prefixo vazio depois de saneado volta ao padrão. */
    suspend fun setFileNamePrefix(prefix: String)
}
