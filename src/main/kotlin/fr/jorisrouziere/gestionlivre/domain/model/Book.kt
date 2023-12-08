package fr.jorisrouziere.gestionlivre.domain.model

class Book(var title: String, var author: String, var reserved: Boolean = false) {

    init {
        if (title.isEmpty() || author.isEmpty()) throw Exception("Title and author must not be empty")
    }
}