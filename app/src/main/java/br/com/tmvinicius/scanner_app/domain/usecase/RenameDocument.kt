package br.com.tmvinicius.scanner_app.domain.usecase

import br.com.tmvinicius.scanner_app.domain.model.Document
import br.com.tmvinicius.scanner_app.domain.model.DocumentError
import br.com.tmvinicius.scanner_app.domain.model.DocumentNaming
import br.com.tmvinicius.scanner_app.domain.repository.DocumentRepository
import javax.inject.Inject

/**
 * Renomeia um documento. O nome é saneado (caracteres proibidos viram `_`); se ficar vazio,
 * falha com [DocumentError.InvalidName]. Se já houver outro com o mesmo nome, ganha sufixo `_2`.
 */
class RenameDocument @Inject constructor(
    private val repository: DocumentRepository,
) {
    suspend operator fun invoke(id: Long, newName: String): Result<Document> {
        val sanitized = DocumentNaming.sanitize(newName)
        if (sanitized.isEmpty()) return Result.failure(DocumentError.InvalidName(newName))
        return repository.rename(id, sanitized)
    }
}
