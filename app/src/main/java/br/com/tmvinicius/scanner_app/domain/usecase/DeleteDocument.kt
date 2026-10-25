package br.com.tmvinicius.scanner_app.domain.usecase

import br.com.tmvinicius.scanner_app.domain.repository.DocumentRepository
import javax.inject.Inject

/** Exclui arquivo e registro. A confirmação em diálogo fica com a UI (M4). */
class DeleteDocument @Inject constructor(
    private val repository: DocumentRepository,
) {
    suspend operator fun invoke(id: Long): Result<Unit> = repository.delete(id)
}
