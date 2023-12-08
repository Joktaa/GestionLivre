package fr.jorisrouziere.gestionlivre.domain.usecase

import fr.jorisrouziere.gestionlivre.domain.exceptions.BookAlreadyReservedException
import fr.jorisrouziere.gestionlivre.domain.exceptions.BookNotFoundException
import fr.jorisrouziere.gestionlivre.domain.model.Book
import fr.jorisrouziere.gestionlivre.domain.port.BDDPort

class BookUseCases(private val bddPort: BDDPort) {
    fun addBook(title: String, author: String) {
        val book = Book(
            title = title,
            author = author
        )
        bddPort.addBook(book)
    }

    fun addBook(book: Book) {
        bddPort.addBook(book)
    }

    fun listBooks(): List<Book> {
        return bddPort.listBooks()
    }

    fun reserveBook(title: String, author: String) {
        val book = Book(
            title = title,
            author = author
        )
        bddPort.listBooks().forEach { b ->
            if (b.title == book.title && b.author == book.author) {
                if (b.reserved) throw BookAlreadyReservedException()
                bddPort.reserveBook(book)
                return
            }
        }
        throw BookNotFoundException()
    }
}