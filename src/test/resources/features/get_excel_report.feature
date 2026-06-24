Feature: Generate and Retrieve Excel Financial Report

  As a user of the system
  I want to generate and retrieve an Excel report
  So that I can analyze and work with financial data in a structured and familiar format

  Scenario Outline: Successfully generate and retrieve an Excel report with valid authentication
    Given I am authenticated with a valid session
    When a request is made to the Excel endpoint with the report ID "<id>"
    Then the service should respond with a status code of 200
    And the response should include the Excel file with "<template>" report
    Examples:
      | id                                   | template                                                |
      | b36f9bbb-1178-432c-8f99-8090e285f2d3 | CCMS Invoice Analysis (CIS to CCMS)                     |
      | f46b4d3d-c100-429a-bf9a-223305dbdbfb | CCMS General ledger extractor (small manual batches)    |
      | eee30b23-2c8d-4b4b-bb11-8cd67d07915c | CCMS and CIS Bank Account Report w Category Code (YTD)  |
      | a017241a-359f-4fdb-a0cd-7f28f1946ef1 | CCMS and CIS Bank Account Report w Category Code (MNTH) |
      | 7073dd13-e325-4863-a05c-a049a815d1f7 | Legal Help contract balances                            |
#      Needs updating when we finish the third party report LPF-1002
#      | 77ef818d-e35d-47ad-8813-74b9fa675877 | CCMS Third party report                                 |
      | 56328b13-254d-435d-813a-5863f94b996d | Mediation contract balances                             |

  Scenario: Return an error when attempting to retrieve a report with an unrecognized ID
    Given I am authenticated with a valid session
    When a request is made to the Excel endpoint with the report ID "01010101-0101-0101-0101-010101010101"
    Then the service should respond with a status code of 404
    And the response should include the error message "Report not found for ID"

  @NotReady
  Scenario: Return an error when report generation fails due non existing view
    Given I am authenticated with a valid session
    When a request is made to the Excel endpoint with the report ID "abbec75b-2d72-44f5-a0e3-2dcb29d92f79"
    Then the service should respond with a status code of 500
    And the response should include the error message "bad SQL grammar"

