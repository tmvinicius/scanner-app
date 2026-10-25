package br.com.tmvinicius.scanner_app.data.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DocumentDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: DocumentDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).build()
        dao = database.documentDao()
    }

    @After
    fun tearDown() = database.close()

    private fun entity(name: String, createdAtMillis: Long) = DocumentEntity(
        name = name,
        filePath = "/documentos/$name.pdf",
        sizeBytes = 1_024,
        createdAtMillis = createdAtMillis,
        pageCount = 1,
    )

    @Test
    fun observeAll_newestFirst() = runTest {
        dao.insert(entity("Antigo", createdAtMillis = 1_000))
        dao.insert(entity("Novo", createdAtMillis = 3_000))
        dao.insert(entity("Meio", createdAtMillis = 2_000))

        assertEquals(listOf("Novo", "Meio", "Antigo"), dao.observeAll().first().map { it.name })
    }

    @Test
    fun insert_returnsGeneratedId_readableById() = runTest {
        val id = dao.insert(entity("Contrato", createdAtMillis = 1_000))

        assertEquals("Contrato", dao.getById(id)?.name)
        assertEquals("Contrato", dao.observeById(id).first()?.name)
    }

    @Test
    fun update_changesNameAndPath() = runTest {
        val id = dao.insert(entity("Antigo", createdAtMillis = 1_000))
        val renamed = entity("Novo", createdAtMillis = 1_000).copy(id = id)

        assertEquals(1, dao.update(renamed))
        assertEquals(renamed, dao.getById(id))
    }

    @Test
    fun deleteById_removesDocument() = runTest {
        val id = dao.insert(entity("Contrato", createdAtMillis = 1_000))

        assertEquals(1, dao.deleteById(id))
        assertNull(dao.getById(id))
        assertEquals(0, dao.deleteById(id))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun filePath_isUnique() = runTest {
        dao.insert(entity("Contrato", createdAtMillis = 1_000))
        dao.insert(entity("Contrato", createdAtMillis = 2_000))
    }
}
