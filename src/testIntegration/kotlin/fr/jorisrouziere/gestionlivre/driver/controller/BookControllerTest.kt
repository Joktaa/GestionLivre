package fr.jorisrouziere.gestionlivre.driver.controller

import com.ninjasquad.springmockk.MockkBean
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
    fun `Un post avec erreur sur books est 4xx`() {
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
            status { is4xxClientError() }
        }
    }

    @Test
    fun `Un post avec exception sur books est 5xx`() {
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
            status { is5xxServerError() }
        }
    }
}