package fr.jorisrouziere.gestionlivre.infrastructure.driver.controller

import fr.jorisrouziere.gestionlivre.domain.model.Book
import fr.jorisrouziere.gestionlivre.domain.usecase.BookUseCases
import fr.jorisrouziere.gestionlivre.infrastructure.driver.dto.BookDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController {
    @Autowired
    private lateinit var bookUseCase: BookUseCases

    @GetMapping
    fun getBooks(): List<Book> {
        return bookUseCase.listBooks()
    }

    @PostMapping
    fun createBook(
            @RequestBody book: BookDTO
    ) {
        bookUseCase.addBook(book.title, book.author)
    }
}