@ui
Feature: Be able to view UI

  As a user of the system
  I want to use the UI to see which reports are available
  So that I can access and analyse data

  Scenario: Load the UI
    Given The user is authorised in the UI
    When I load the GLAD page
    Then the page title is correct
    And the heading is set
    And there is at least one report row
    And there are download links
    And the download links match the file format descriptor

  Scenario: UI needs auth or will error
    Given The user is not authorised in the UI
    When I load the GLAD page
    Then return 401 unauthorised

  Scenario: UI result page has no accessibility errors
    Given The user is authorised in the UI
    When I load the GLAD page
    Then there are no accessibility errors