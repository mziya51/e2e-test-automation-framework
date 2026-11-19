Feature: Campus Endpoint

  Scenario: verify information about campus details
    Given I logged in Bookit api as a "team-leader"
    And request accept type is "application/json"
    When I sent GET request to "/api/campuses" endpoint
    Then status code should be 200
    And response content type is "application/json"
    And Each "id" field should not be null
    And Each "location" field should not be null