Feature: Retrieve a Single Report

  As a user of the system
  I want to retrieve details of a specific report
  So that I can view and analyze the report's information

  Scenario: Successfully retrieve details of an existing report with valid authentication
    Given I am authenticated with a valid session
    And a request is made to the reports endpoint
    When a request is made to the reports endpoint with a random report
    Then the service should respond with a status code of 200
    And the response should include valid details for the resource

  Scenario: Redirect to login when attempting to retrieve reports with invalid authentication
    Given I am authenticated with an invalid session
    When a request is made to the reports endpoint with the report ID "01010101-0101-0101-0101-010101010101"
    Then the service should respond with a status code of 401

  Scenario: Return an error when attempting to retrieve a report with an unrecognized ID
    Given I am authenticated with a valid session
    When a request is made to the reports endpoint with the report ID "01010101-0101-0101-0101-010101010101"
    Then the service should respond with a status code of 404
    And the response should include the error message "Report with unrecognised ID"

#  Scenario: Redirect to login when attempting to retrieve a report with invalid authentication
#    When an invalid authentication cookie is provided
#    And a request is made to the reports endpoint with the report ID "0fbec75b-2d72-44f5-a0e3-2dcb29d92f79"
#    Then the service should respond with a status code of 302
#    And the user should be redirected to the login page
