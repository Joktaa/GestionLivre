package fr.jorisrouziere.gestionlivre.domain.model

class Book(var title: String, var author: String) {

    init {
        if (title.isEmpty() || author.isEmpty()) throw Exception("Title and author must not be empty")
    }
}