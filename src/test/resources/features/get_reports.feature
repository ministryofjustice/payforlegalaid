Feature: List All Available Reports

  As a user of the system
  I want to retrieve a list of all available reports
  So that I can view and manage the reports effectively

  Scenario: Successfully retrieve a list of all reports with valid authentication
    Given I am authenticated with a valid session
    When a request is made to the reports endpoint
    Then the service should respond with a status code of 200
    And the response should include a list of all available reports

  Scenario: Redirect to login when attempting to retrieve reports with invalid authentication
    Given I am authenticated with an invalid session
    When a request is made to the reports endpoint
    Then the service should respond with a status code of 401
