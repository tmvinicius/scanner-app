package br.com.tmvinicius.scanner_app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Ponto de entrada da aplicação; inicializa o grafo de dependências do Hilt. */
@HiltAndroidApp
class ScannerApp : Application()
