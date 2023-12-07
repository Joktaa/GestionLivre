package fr.jorisrouziere.gestionlivre.infrastructure.driver.dto

class BookDTO(var title: String, var author: String) {
    init {
        if (title.isEmpty() || author.isEmpty()) throw Exception("Title and author must not be empty")
    }
}