Feature: List All Available S3 Storage Reports

  As a user of the system
  I want to retrieve a list of all available reports
  So that I can view and manage the reports effectively

  @Role=Reconciliation
  Scenario: Successfully retrieve report details for a Reconciliation report with valid authentication
    Given I am authenticated with a valid session
    When a request is made to get the report details with the report ID "cc55e276-97b0-4dd8-a919-26d4aa373266"
    Then the service should respond with a status code of 200
    And the response should include the file

  @Role=REP000
  Scenario: Successfully retrieve report details for REP000 report with valid authentication
    Given I am authenticated with a valid session
    When a request is made to get the report details with the report ID "523f38f0-2179-4824-b885-3a38c5e149e8"
    Then the service should respond with a status code of 200
    And the response should include the file

  @Role=Financial
  Scenario: Return forbidden when requesting REP000 report with Financial role
    Given I am authenticated with a valid session
    When a request is made to get the report details with the report ID "523f38f0-2179-4824-b885-3a38c5e149e8"
    Then the service should respond with a status code of 403
    And the response should include the error message "You cannot access report with ID: 523f38f0-2179-4824-b885-3a38c5e149e8"