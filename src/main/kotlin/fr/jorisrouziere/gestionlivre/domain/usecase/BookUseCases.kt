package fr.jorisrouziere.gestionlivre.domain.usecase

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

    fun listBooks(): List<Book> {
        return bddPort.listBooks()
    }
}