package br.com.tmvinicius.scanner_app.di

import br.com.tmvinicius.scanner_app.data.files.LocalPdfFileStorage
import br.com.tmvinicius.scanner_app.data.files.PdfFileStorage
import br.com.tmvinicius.scanner_app.data.prefs.DataStoreSettingsRepository
import br.com.tmvinicius.scanner_app.data.repository.DocumentRepositoryImpl
import br.com.tmvinicius.scanner_app.data.repository.SystemClock
import br.com.tmvinicius.scanner_app.domain.repository.Clock
import br.com.tmvinicius.scanner_app.domain.repository.DocumentRepository
import br.com.tmvinicius.scanner_app.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Liga as interfaces do domínio às implementações da camada de dados. */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindDocumentRepository(impl: DocumentRepositoryImpl): DocumentRepository

    @Binds
    abstract fun bindSettingsRepository(impl: DataStoreSettingsRepository): SettingsRepository

    @Binds
    abstract fun bindPdfFileStorage(impl: LocalPdfFileStorage): PdfFileStorage

    @Binds
    abstract fun bindClock(impl: SystemClock): Clock
}
