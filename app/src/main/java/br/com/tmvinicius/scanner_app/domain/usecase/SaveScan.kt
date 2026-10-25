package br.com.tmvinicius.scanner_app.domain.usecase

import br.com.tmvinicius.scanner_app.domain.model.Document
import br.com.tmvinicius.scanner_app.domain.model.ScannedPdf
import br.com.tmvinicius.scanner_app.domain.repository.DocumentRepository
import javax.inject.Inject

/** Guarda o PDF que o scanner acabou de gerar (usado pelo fluxo de escaneamento no M3). */
class SaveScan @Inject constructor(
    private val repository: DocumentRepository,
) {
    suspend operator fun invoke(scannedPdf: ScannedPdf): Result<Document> =
        repository.saveScan(scannedPdf)
}
