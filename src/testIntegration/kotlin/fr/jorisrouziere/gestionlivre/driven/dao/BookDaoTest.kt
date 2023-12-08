package fr.jorisrouziere.gestionlivre.driven.dao

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
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
            performQuery("DELETE FROM book")
        }

        protected fun performQuery(sql: String) {
            val hikariConfig = HikariConfig()
            hikariConfig.setJdbcUrl(postgreSQLContainer.jdbcUrl)
            hikariConfig.username = postgreSQLContainer.username
            hikariConfig.password = postgreSQLContainer.password
            hikariConfig.setDriverClassName(postgreSQLContainer.driverClassName)
            val ds = HikariDataSource(hikariConfig)

            val statement = ds.getConnection().createStatement()
            statement.execute(sql)
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
        Assertions.assertTrue(books.any { it.title == book.title && it.author == book.author && !it.reserved })
    }

    @Test
    fun `Liste des livres`() {
        // Arrange
        val book1 = Book("La légende des siècles", "Victor Hugo")
        val book2 = Book("Le meilleur des mondes", "Aldous Huxley")

        // Act
        bookDao.addBook(book1)
        bookDao.addBook(book2)
        val books = bookDao.listBooks()

        // Assert
        Assertions.assertTrue(books.any { it.title == book1.title && it.author == book1.author })
        Assertions.assertTrue(books.any { it.title == book2.title && it.author == book2.author })
    }

    @Test
    fun `Réservation d'un livre`() {
        // Arrange
        val book = Book("Les travailleurs de la mer", "Victor Hugo")
        bookDao.addBook(book)

        // Act
        bookDao.reserveBook(book)
        val books = bookDao.listBooks()

        // Assert
        Assertions.assertTrue(books.any { it.title == book.title && it.author == book.author && it.reserved })
    }

}