package br.com.tmvinicius.scanner_app.domain.usecase

import br.com.tmvinicius.scanner_app.domain.model.Document
import br.com.tmvinicius.scanner_app.domain.repository.DocumentRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Um documento para a tela Documento; `null` se não existir. */
class ObserveDocument @Inject constructor(
    private val repository: DocumentRepository,
) {
    operator fun invoke(id: Long): Flow<Document?> = repository.observeDocument(id)
}
