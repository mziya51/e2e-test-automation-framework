Feature: User E2E Testing
  @wip @db @ui
  Scenario: verify created user able to login
    Given I logged in Bookit api as a "teacher"
    And request accept type is "application/json"
    And I create a random student as a body
    When I sent POST request to "/api/students/student" endpoint
    Then status code should be 201
    And  response content type is "application/json"
    And  "entryType" should be "Student"
    And  "entryiId" field should not be null
    And  created user following information should match with database
      | first-name      |
      | last-name       |
      | email           |
      | campus-location |
      | batch-number    |
      | team-name       |
    And created user should be able to login Bookit UI
    And created user name should appear in Self Page
    And I sent DELETE request to "/api/students/{id}" endpoint
    And status code should be 204



