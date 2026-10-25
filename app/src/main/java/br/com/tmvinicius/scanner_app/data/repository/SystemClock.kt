package br.com.tmvinicius.scanner_app.data.repository

import br.com.tmvinicius.scanner_app.domain.repository.Clock
import javax.inject.Inject

class SystemClock @Inject constructor() : Clock {
    override fun nowMillis(): Long = System.currentTimeMillis()
}
