package fr.jorisrouziere.gestionlivre.domain

import assertk.assertThat
import assertk.assertions.contains
import fr.jorisrouziere.gestionlivre.domain.model.Book
import fr.jorisrouziere.gestionlivre.domain.port.BDDPort
import fr.jorisrouziere.gestionlivre.domain.usecase.BookUseCases
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import net.jqwik.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest

@ExtendWith(MockKExtension::class)
@SpringBootTest
class BookUseCasePropertyBasedTest {
    @MockK
    private var mock: BDDPort = mockk()
    private var bookUseCases = BookUseCases(mock)
    private val books = mutableListOf<Book>()

    @Property
    fun `La liste des livres retournés contient l'éléments de la liste stockée`(
            @ForAll("book") arbitraryBook: Book
    ) {
        // Arrange
        every { mock.addBook(any()) } answers { books.add(firstArg()) }
        every { mock.listBooks() } answers { books.sortedBy { it.title } }
        bookUseCases.addBook(arbitraryBook)

        // Act
        val result = bookUseCases.listBooks()

        // Assert
        assertThat(result).contains(arbitraryBook)
    }

    /*@Property
    fun `La liste des livres retournés contient tous les éléments de la liste stockée`(
            @ForAll("books") arbitraryBooks: List<Book>
    ) {
        // Arrange
        books.clear()
        every { mock.addBook(any()) } answers { books.add(firstArg()) }
        every { mock.listBooks() } answers { books.sortedBy { it.title } }
        arbitraryBooks.forEach { bookUseCases.addBook(it) }

        // Act
        val result = bookUseCases.listBooks()

        // Assert
        assertThat(result).containsAll(arbitraryBooks)
    }*/

    @Provide
    fun book(): Arbitrary<Book> {
        return Arbitraries.strings().alpha().ofLength(10)
                .flatMap { title ->
                    Arbitraries.strings().alpha().ofLength(10)
                            .map { author ->
                                Book(title = title, author = author)
                            }
                }
    }

    @Provide
    fun books(): Arbitrary<List<Book>> {

        return book().list()
                .ofSize(10)
                .uniqueElements { b -> b.title }
    }
}