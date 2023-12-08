package fr.jorisrouziere.gestionlivre.driver.controller

import com.ninjasquad.springmockk.MockkBean
import fr.jorisrouziere.gestionlivre.domain.exceptions.BookAlreadyReservedException
import fr.jorisrouziere.gestionlivre.domain.exceptions.BookNotFoundException
import fr.jorisrouziere.gestionlivre.domain.model.Book
import fr.jorisrouziere.gestionlivre.domain.usecase.BookUseCases
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@ExtendWith(SpringExtension::class)
@WebMvcTest
class BookControllerTest {
    @MockkBean
    private lateinit var bookUseCases: BookUseCases

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `Un get sur books est ok`() {
        every { bookUseCases.listBooks() } returns listOf(Book("Les misérables", "Victor Hugo"))

        mockMvc.get("/books") {
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON)  }
            content { json("""[{"title":"Les misérables","author":"Victor Hugo"}]""") }
        }
    }

    @Test
    fun `Un post sur books est ok`() {
        every { bookUseCases.addBook(any(), any()) } returns Unit

        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                        {
                            "title": "Les misérables",
                            "author": "Victor Hugo"
                        }
                    """.trimIndent()
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `Un post avec erreur sur books est 500`() {
        every { bookUseCases.addBook(any(), any()) } returns Unit

        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                        {
                            "titl": "Les misérables",
                            "autho": "Victor Hugo"
                        }
                    """.trimIndent()
        }.andExpect {
            status { isInternalServerError() }
        }
    }

    @Test
    fun `Un post avec exception sur books est 500`() {
        every { bookUseCases.addBook(any(), any()) } throws RuntimeException()

        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                        {
                            "title": "Les misérables",
                            "author": "Victor Hugo"
                        }
                    """.trimIndent()
        }.andExpect {
            status { isInternalServerError() }
        }
    }

    @Test
    fun `Un post sur reserve est ok`() {
        every { bookUseCases.reserveBook(any(), any()) } returns Unit

        mockMvc.post("/books/reserve") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                        {
                            "title": "Les misérables",
                            "author": "Victor Hugo"
                        }
                    """.trimIndent()
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `Un post avec erreur sur reserve est 500`() {
        every { bookUseCases.reserveBook(any(), any()) } returns Unit

        mockMvc.post("/books/reserve") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                        {
                            "titl": "Les misérables",
                            "autho": "Victor Hugo"
                        }
                    """.trimIndent()
        }.andExpect {
            status { isInternalServerError() }
        }
    }

    @Test
    fun `Un post avec BookAlreadyReservedException sur reserve est 409`() {
        every { bookUseCases.reserveBook(any(), any()) } throws BookAlreadyReservedException()

        mockMvc.post("/books/reserve") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                        {
                            "title": "Les misérables",
                            "author": "Victor Hugo"
                        }
                    """.trimIndent()
        }.andExpect {
            status { isConflict() }
            content { string("Book already reserved") }
        }
    }

    @Test
    fun `Un post avec BookNotFoundException sur reserve est 404`() {
        every { bookUseCases.reserveBook(any(), any()) } throws BookNotFoundException()

        mockMvc.post("/books/reserve") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                        {
                            "title": "Les misérables",
                            "author": "Victor Hugo"
                        }
                    """.trimIndent()
        }.andExpect {
            status { isNotFound() }
            content { string("Book not found") }
        }
    }
}