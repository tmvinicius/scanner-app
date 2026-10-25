package br.com.tmvinicius.scanner_app.domain.model

/** Preferências do usuário. */
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val fileNamePrefix: String = DocumentNaming.DEFAULT_PREFIX,
)

/** Tema escolhido nas Configurações. */
enum class ThemeMode { SYSTEM, LIGHT, DARK }
