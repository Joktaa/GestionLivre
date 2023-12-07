Feature: store and get book
  Scenario: the user create two entries and retrieve both
    Given the user creates a book with title "Les misérables" and author "Victor Hugo"
    And the user creates a book with title "La légende des siècles" and author "Victor Hugo"
    When the user get all books
    Then the user should see the following books
      | title                  | author       |
      | Les misérables         | Victor Hugo  |
      | La légende des siècles | Victor Hugo  |