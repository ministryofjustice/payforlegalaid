@smoke
Feature: Generate and Retrieve Excel Financial Report - CIS to CCMS

  As a user of the system
  I want to generate and retrieve an Excel report
  So that I can analyze and work with financial data in a structured and familiar format

  Scenario Outline: Successfully generate and retrieve 'CCMS Invoice Analysis' Excel report with valid authentication
    Given I am authenticated with a valid session
    When  a request is made to the Excel endpoint with the report ID "<id>"
    Then the service should respond with a status code of 200
    And the report is not empty
    And the report should contain all mandatory fields
    Examples:
      | id                                   | template                                                |
      | b36f9bbb-1178-432c-8f99-8090e285f2d3 | CCMS Invoice Analysis (CIS to CCMS)                     |