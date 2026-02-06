@smoke
Feature: Authentication
  Scenario: User can sign up and log in
    Given a new user with password "Pass123!"
    When the user signs up
    Then the response status should be 200
    And the response contains an access token
    When the user logs in
    Then the response status should be 200
    And the response contains an access token
