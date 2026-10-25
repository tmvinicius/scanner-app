package br.com.tmvinicius.scanner_app.domain.repository

/** Hora atual, injetada para que testes controlem a data dos documentos. */
fun interface Clock {
    fun nowMillis(): Long
}
