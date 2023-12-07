package fr.jorisrouziere.gestionlivre

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.cucumber.java.Before
import io.cucumber.java.Scenario
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.spring.CucumberContextConfiguration
import io.restassured.RestAssured
import io.restassured.path.json.JsonPath
import io.restassured.response.ValidatableResponse
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.lifecycle.Startables


@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CucumberRunnerTest {

    @LocalServerPort
    private val port = 0

    private lateinit var books: ValidatableResponse

    companion object {
        @Container
        private val postgreSQLContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:13-alpine")

        init {
            Startables.deepStart(postgreSQLContainer).join()
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
        }
    }

    @Before
    fun setup(scenario: Scenario) {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @Given("the user creates a book with title {string} and author {string}")
    fun theUserCreatesABookWithTitleAndAuthor(title: String, author: String) {
        RestAssured.given()
            .contentType("application/json")
            .and()
            .body(
                """
                {
                    "title": "$title",
                    "author": "$author"
                }
                """.trimIndent()
            )
            .`when`()
            .post("/books")
            .then()
            .statusCode(200)
    }

    @When("the user get all books")
    fun theUserGetAllBooks() {
        books = RestAssured.given()
            .`when`()
            .get("/books")
            .then()
            .statusCode(200)
    }

    @Then("the user should see those books")
    fun theUserShouldSeeThoseBooks() {
        val expectedResponse: String = """
            [
                {
                    "title": "Les misérables",
                    "author": "Victor Hugo"
                },
                {
                    "title": "La légende des siècles",
                    "author": "Victor Hugo"
                }
            ]
        """.trimIndent()

        assertThat(books.extract().body().jsonPath().prettify())
            .isEqualTo(JsonPath(expectedResponse).prettify())
    }

}