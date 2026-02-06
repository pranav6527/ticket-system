@smoke
Feature: Ticket management
  Scenario: Authenticated user can create and list tickets
    Given a new user with password "Pass123!"
    And the user is authenticated
    When the user creates a ticket with subject "Need help" and description "Something broke"
    Then the response status should be 200
    And the ticket subject is "Need help"
    When the user requests their tickets
    Then the response status should be 200
    And the tickets list contains subject "Need help"
