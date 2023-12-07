package fr.jorisrouziere.gestionlivre

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import io.restassured.path.json.JsonPath
import io.restassured.response.ValidatableResponse

class CucumberInstructions {
    private lateinit var books: ValidatableResponse

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

    @Then("the user should see the following books")
    fun theUserShouldSeeTheFollowingBooks(payload: List<Map<String, Any>>) {
        val expectedResponse: String = payload.joinToString(prefix = "[", postfix = "]", separator = ",") { line ->
            """
                ${
                line.entries.joinToString(prefix = "{", postfix = "}", separator = ",") { entry ->
                    """
                            "${entry.key}": "${entry.value}"
                        """.trimIndent()
                }
            }
            """.trimIndent()
        }

        assertThat(books.extract().body().jsonPath().prettify())
                .isEqualTo(JsonPath(expectedResponse).prettify())
    }
}