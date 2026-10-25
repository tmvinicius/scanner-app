package br.com.tmvinicius.scanner_app.di

import android.content.Context
import androidx.room.Room
import br.com.tmvinicius.scanner_app.data.local.AppDatabase
import br.com.tmvinicius.scanner_app.data.local.DocumentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/** Banco em memória para os testes com Hilt: cada teste começa com o banco vazio. */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DatabaseModule::class])
object TestDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

    @Provides
    fun provideDocumentDao(database: AppDatabase): DocumentDao = database.documentDao()
}
