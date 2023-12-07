package fr.jorisrouziere.gestionlivre.driven.dao

import fr.jorisrouziere.gestionlivre.domain.model.Book
import fr.jorisrouziere.gestionlivre.infrastructure.driven.dao.BookDAO
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@ExtendWith(SpringExtension::class)
@SpringBootTest
@Testcontainers
class BookDaoTest {

    @Autowired
    private lateinit var bookDao: BookDAO

    companion object {
        @Container
        private val postgreSQLContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:13-alpine")

        @JvmStatic
        @BeforeAll
        fun setUp() {
            postgreSQLContainer.start()
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            postgreSQLContainer.stop()
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
        }

        @BeforeEach
        fun cleanUp() {
            postgreSQLContainer.execInContainer("psql", "-U", "postgres", "-c", "DROP DATABASE IF EXISTS gestion_livre")
            postgreSQLContainer.execInContainer("psql", "-U", "postgres", "-c", "CREATE DATABASE gestion_livre")
        }
    }

    @Test
    fun `Ajout d'un livre`() {
        // Arrange
        val book = Book("Les misérables", "Victor Hugo")

        // Act
        bookDao.addBook(book)
        val books = bookDao.listBooks()

        // Assert
        Assertions.assertTrue(books.any { it.title == book.title && it.author == book.author })
    }

    @Test
    fun `Ajout d'un autre livre`() {
        // Arrange
        val book = Book("La Légende des siècles", "Victor Hugo")

        // Act
        bookDao.addBook(book)
        val books = bookDao.listBooks()

        // Assert
        Assertions.assertTrue(books.any { it.title == book.title && it.author == book.author })
    }

}