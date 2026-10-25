package br.com.tmvinicius.scanner_app.di

import javax.inject.Qualifier

/** Dispatcher para disco e banco (`Dispatchers.IO` no app; trocado nos testes). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/** Pasta interna onde os PDFs ficam guardados (`filesDir/documentos`). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DocumentsDir
