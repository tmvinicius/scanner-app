package br.com.tmvinicius.scanner_app.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.tmvinicius.scanner_app.domain.model.Document

@Entity(
    tableName = "documents",
    indices = [Index(value = ["file_path"], unique = true)],
)
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "file_path") val filePath: String,
    @ColumnInfo(name = "size_bytes") val sizeBytes: Long,
    @ColumnInfo(name = "created_at") val createdAtMillis: Long,
    @ColumnInfo(name = "page_count") val pageCount: Int,
)

fun DocumentEntity.toDomain(): Document = Document(
    id = id,
    name = name,
    filePath = filePath,
    sizeBytes = sizeBytes,
    createdAtMillis = createdAtMillis,
    pageCount = pageCount,
)

fun Document.toEntity(): DocumentEntity = DocumentEntity(
    id = id,
    name = name,
    filePath = filePath,
    sizeBytes = sizeBytes,
    createdAtMillis = createdAtMillis,
    pageCount = pageCount,
)
