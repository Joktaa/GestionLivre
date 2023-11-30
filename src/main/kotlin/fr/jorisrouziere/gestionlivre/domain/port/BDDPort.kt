package fr.jorisrouziere.gestionlivre.domain.port

import fr.jorisrouziere.gestionlivre.domain.model.Book

interface BDDPort {
    fun addBook(book: Book)
    fun listBooks(): List<Book>
}