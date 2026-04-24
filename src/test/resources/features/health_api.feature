Feature: Service Health Check via Actuator Endpoint

  As an API consumer
  I want to verify the health status of the service
  So that I can ensure the service is operational and responding correctly

  Scenario: Verify the service returns a healthy status
    Given I am authenticated with a valid session
    When a request is made to the actuator health endpoint
    Then the service should respond with a status code of 200
    And the response should indicate a healthy state