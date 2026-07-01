@performance
Feature: UI Performance SLAs

  Scenario: Report listing page loads within SLA
    Given I am authenticated with a valid session
    When the user navigates to the reports listing page
    Then the page should load within 2000 milliseconds

  Scenario: Small CSV download meets SLA
    Given I am authenticated with a valid session
    When the user downloads the small csv report via the UI
    Then the time to first byte should be within 2000 milliseconds
    And the download should complete within 10000 milliseconds
#
  Scenario: Small Excel download meets SLA
    Given I am authenticated with a valid session
    When the user downloads the small excel report via the UI
    Then the time to first byte should be within 2000 milliseconds
    And the download should complete within 10000 milliseconds

  Scenario: Large CSV download meets SLA
    Given I am authenticated with a valid session
    When the user downloads the large csv report via the UI
    Then the time to first byte should be within 2000 milliseconds
    And the download should complete within 10000 milliseconds

  Scenario: Large Excel download meets SLA
    Given I am authenticated with a valid session
    When the user downloads the large excel report via the UI
    Then the time to first byte should be within 2000 milliseconds
    And the download should complete within 10000 milliseconds