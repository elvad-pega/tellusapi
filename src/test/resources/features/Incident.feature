Feature: Create new incident

  Scenario: Obtain access token using client credentials
    Given the OAuth2 token endpoint is available
    When I request an access token with client_id "xxx" and client_secret "xxx"
    Then I should receive an access token in the response

  Scenario: Create a new incident case
    Given the Create Case API endpoint is available
    When I create a new incident case
    Then the response status code should be 201
    And the response should include case ID
    And the response should include next assignment ID "CREATEFORM_DEFAULT"
    And the open link href should be "CREATEFORM_DEFAULT"

  Scenario: User determines incident category for assignment
    Given the DetermineCategory API endpoint is available
    When I submit the following incident details:
      | IncidentType           | IncidentSubType                          |
      | Customer service issue | Delays or no response to communications  |    
    Then the response status code should be 200
    And the response should include assignment ID "CREATEFORM_DEFAULT"
    And the open link href should be "CREATEFORM_DEFAULT"

  Scenario: Submit service details for assignment
    Given the ServiceDetails API endpoint is available
    When I submit the following service details:
      | CommunicationChannel               | What                        | When       | Where                | WhereThereAnyOtherPeopleAffected |
      | E-mail                            | https://portal.pega.com/    | 2025-05-06 | My Worklist link.    | false                           |    
    Then the response status code should be 200
    And the response should include assignment ID "CREATEFORM_DEFAULT"
    And the open link href should be "CREATEFORM_DEFAULT"

  Scenario: Submit contact info for assignment
    Given the ContactInfo API endpoint is available
    When I submit the following contact details:
      | FName        | LName        | EMail         | PhoneNumber   | AddressMode | pyCountry | pyCity | pyStreet                     | pyPostalCode | pyState |
      | Demo         | API-testing  | test@test.com | +44666666666  | Manually    | Spain     | Madrid | Calle de José Abascal, 41    | 28003        |         |
    Then the response status code should be 200
    And the response should include assignment ID "CREATEFORM_DEFAULT"
    And the open link href should be "CREATEFORM_DEFAULT"

  Scenario: Submit review with user consent and privacy policy agreement
    Given the Review API endpoint is available
    When I submit review consent as follows:
      | UserConsent | PrivacyPolicy |
      | true        | true          |
    Then the response status code should be 200
    And the case status is "Pending-Dispatch"
    And the case urgency is "10"
    And the response should contain confirmation note "Thank you! The next step in this case has been routed appropriately."
    And the getNextWork link href should be "/assignments/next/"
  
