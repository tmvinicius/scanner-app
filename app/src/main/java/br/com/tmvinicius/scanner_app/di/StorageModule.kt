package br.com.tmvinicius.scanner_app.di

import android.content.ContentResolver
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

/** Arquivos e preferências no armazenamento interno do app. */
@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    private const val DOCUMENTS_FOLDER = "documentos"
    private const val SETTINGS_FILE = "settings"

    @Provides
    @DocumentsDir
    fun provideDocumentsDir(@ApplicationContext context: Context): File =
        File(context.filesDir, DOCUMENTS_FOLDER)

    @Provides
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver =
        context.contentResolver

    /** Só pode existir uma instância de DataStore por arquivo, por isso é singleton. */
    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(SETTINGS_FILE) },
        )
}
