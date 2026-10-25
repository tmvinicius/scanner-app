package br.com.tmvinicius.scanner_app.di

import android.content.Context
import androidx.room.Room
import br.com.tmvinicius.scanner_app.data.local.AppDatabase
import br.com.tmvinicius.scanner_app.data.local.DocumentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Banco Room. Nos testes instrumentados é trocado por um banco em memória. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME).build()

    @Provides
    fun provideDocumentDao(database: AppDatabase): DocumentDao = database.documentDao()
}
