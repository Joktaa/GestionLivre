package fr.jorisrouziere.gestionlivre.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import fr.jorisrouziere.gestionlivre.domain.exceptions.BookAlreadyReservedException
import fr.jorisrouziere.gestionlivre.domain.exceptions.BookNotFoundException
import fr.jorisrouziere.gestionlivre.domain.model.Book
import fr.jorisrouziere.gestionlivre.domain.port.BDDPort
import fr.jorisrouziere.gestionlivre.domain.usecase.BookUseCases
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest

@ExtendWith(MockKExtension::class)
@SpringBootTest
class BookUseCasesUnitTest {
    @MockK
    private var mock: BDDPort = mockk()
    private var bookUseCases = BookUseCases(mock)
    private val books = mutableListOf<Book>()

    @BeforeEach
    fun setup() {
        books.clear()
        every { mock.addBook(any()) } answers { books.add(firstArg()) }
        every { mock.listBooks() } answers { books.sortedBy { it.title } }
        every { mock.reserveBook(any()) } answers {
            books.forEach { b ->
                if (b.title == firstArg<Book>().title && b.author == firstArg<Book>().author) {
                    b.reserved = true
                }
            }
        }
        bookUseCases = BookUseCases(mock)
    }

    @Test
    fun `addBook ajoute un livre à la fin`() {
        // Arrange

        // Act
        bookUseCases.addBook("Le Seigneur des Anneaux", "J.R.R. Tolkien")

        // Assert
        val lastBook = books.last()
        assertThat(lastBook.title).isEqualTo("Le Seigneur des Anneaux")
        assertThat(lastBook.author).isEqualTo("J.R.R. Tolkien")
    }

    @Test
    fun `addBook lève une exception quand le titre et l'auteur sont vide`() {
        // Arrange

        // Assert
        // Act & Assert
        assertThrows<Exception> {
            bookUseCases.addBook("", "")
        }
    }

    @Test
    fun `listBook retourne la liste trié`() {
        // Arrange
        bookUseCases.addBook("Le Seigneur des Anneaux", "J.R.R. Tolkien")
        bookUseCases.addBook("Le Hobbit", "J.R.R. Tolkien")
        bookUseCases.addBook("Le Silmarillion", "J.R.R. Tolkien")

        // Act
        val retournedBooks = bookUseCases.listBooks()

        // Assert
        assertThat(retournedBooks[0].title).isEqualTo("Le Hobbit")
        assertThat(retournedBooks[1].title).isEqualTo("Le Seigneur des Anneaux")
        assertThat(retournedBooks[2].title).isEqualTo("Le Silmarillion")
    }

    @Test
    fun `reserveBook réserve un livre`() {
        // Arrange
        bookUseCases.addBook("La légende des siècles", "Victor Hugo")

        // Act
        bookUseCases.reserveBook("La légende des siècles", "Victor Hugo")

        // Assert
        val lastBook = books.last()
        assertThat(lastBook.reserved).isEqualTo(true)
    }

    @Test
    fun `un livre créé est non réservé`() {
        // Arrange
        bookUseCases.addBook("La légende des siècles", "Victor Hugo")

        // Assert
        val lastBook = books.last()
        assertThat(lastBook.reserved).isEqualTo(false)
    }

    @Test
    fun `reserveBook lève une exception quand le livre n'existe pas`() {
        // Assert
        // Act & Assert
        assertThrows<BookNotFoundException> {
            bookUseCases.reserveBook("La légende des siècles", "Victor Hugo")
        }
    }

    @Test
    fun `reserveBook lève une exception quand le livre est déjà réservé`() {
        // Arrange
        bookUseCases.addBook("La légende des siècles", "Victor Hugo")
        bookUseCases.reserveBook("La légende des siècles", "Victor Hugo")

        // Act & Assert
        assertThrows<BookAlreadyReservedException> {
            bookUseCases.reserveBook("La légende des siècles", "Victor Hugo")
        }
    }
}