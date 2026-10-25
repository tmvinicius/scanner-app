package br.com.tmvinicius.scanner_app.domain.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Regras de nome dos documentos (o nome também é o nome do arquivo, sem `.pdf`). */
object DocumentNaming {

    const val DEFAULT_PREFIX = "Documento"
    const val FILE_EXTENSION = ".pdf"
    const val MAX_NAME_LENGTH = 100

    /** Caracteres proibidos em nomes de arquivo no Android e nos apps de destino comuns. */
    private val forbiddenChars = Regex("""[\\/:*?"<>|\p{Cntrl}]""")
    private val repeatedSpaces = Regex("""\s+""")

    /** Nome padrão: `<prefixo>_AAAA-MM-DD_HHMM`, ex.: `Documento_2026-10-14_2301`. */
    fun defaultName(
        prefix: String,
        createdAtMillis: Long,
        timeZone: TimeZone = TimeZone.getDefault(),
    ): String {
        val format = SimpleDateFormat("yyyy-MM-dd_HHmm", Locale.ROOT).apply { this.timeZone = timeZone }
        val safePrefix = sanitize(prefix).ifEmpty { DEFAULT_PREFIX }
        return "${safePrefix}_${format.format(Date(createdAtMillis))}"
    }

    /**
     * Troca caracteres proibidos por `_`, junta espaços repetidos, tira pontos e espaços das
     * pontas e limita o tamanho. Pode devolver texto vazio.
     */
    fun sanitize(name: String): String = name
        .replace(forbiddenChars, "_")
        .replace(repeatedSpaces, " ")
        .trim()
        .trim('.')
        .trim()
        .take(MAX_NAME_LENGTH)
        .trim()

    /** Devolve [baseName] ou, se já existir, `baseName_2`, `baseName_3`… */
    suspend fun uniqueName(baseName: String, exists: suspend (String) -> Boolean): String {
        if (!exists(baseName)) return baseName
        var suffix = 2
        while (exists("${baseName}_$suffix")) suffix++
        return "${baseName}_$suffix"
    }

    fun fileName(name: String): String = name + FILE_EXTENSION
}
