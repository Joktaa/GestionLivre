package fr.jorisrouziere.gestionlivre.infrastructure.driven.dao

import fr.jorisrouziere.gestionlivre.domain.model.Book
import fr.jorisrouziere.gestionlivre.domain.port.BDDPort
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class BookDAO (private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate): BDDPort {

    override fun addBook(book: Book) {
        namedParameterJdbcTemplate.
            update("INSERT INTO book (title, author) VALUES (:title, :author)", mapOf(
                "title" to book.title,
                "author" to book.author
            ))
    }

    override fun listBooks(): List<Book> {
        return namedParameterJdbcTemplate
            .query("SELECT * FROM book", MapSqlParameterSource()) { rs, _ ->
            Book(
                title = rs.getString("title"),
                author = rs.getString("author"),
                reserved = rs.getBoolean("reserved")
            )
        }
    }

    override fun reserveBook(book: Book) {
        namedParameterJdbcTemplate.
            update("UPDATE book SET reserved = true WHERE title = :title AND author = :author", mapOf(
                "title" to book.title,
                "author" to book.author
            ))
    }
}