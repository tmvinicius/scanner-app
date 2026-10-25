package br.com.tmvinicius.scanner_app.domain.usecase

import br.com.tmvinicius.scanner_app.domain.model.Document
import br.com.tmvinicius.scanner_app.domain.repository.DocumentRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Lista de documentos para a tela inicial, mais recentes primeiro. */
class ObserveDocuments @Inject constructor(
    private val repository: DocumentRepository,
) {
    operator fun invoke(): Flow<List<Document>> = repository.observeDocuments()
}
