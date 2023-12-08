package fr.jorisrouziere.gestionlivre

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import io.restassured.path.json.JsonPath
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse

class CucumberInstructions {
    private lateinit var books: ValidatableResponse
    private lateinit var response: Response

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
                {
                    "title": "${line["title"]}",
                    "author": "${line["author"]}",
                    "reserved": ${line["reserved"]}
            }
            """.trimIndent()
        }

        assertThat(books.extract().body().jsonPath().prettify())
                .isEqualTo(JsonPath(expectedResponse).prettify())
    }

    @When("the user reserve a book with title {string} and author {string}")
    fun theUserReserveABookWithTitleAndAuthor(title: String, author: String) {
        response = RestAssured.given()
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
                .post("/books/reserve")
    }

    @Then("the user should see the following error message {int} {string}")
    fun theUserShouldSeeTheFollowingErrorMessage(errorStatus: Int, message: String) {
        response
            .then()
            .statusCode(errorStatus)
            .extract()
            .asString()
            .let {
                assertThat(it).isEqualTo(message)
            }
    }
}