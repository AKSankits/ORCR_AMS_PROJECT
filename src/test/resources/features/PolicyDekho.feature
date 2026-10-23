Feature: PolicyDekho Insurance Portal

  Background:
    Given the user is on the PolicyDekho home page

  Scenario: Verify page title
    Then the page title should be "PolicyDekho | TesterRank"

  Scenario: Verify page URL
    Then the page URL should contain "testerrank.com/insurance"

  @renewal
  Scenario: Verify policy renewal
    When the user clicks on the Renew Policy link
    And the user logs in with valid credentials
    And the user clicks on the Renew Policy link
    And the user renews the first available policy
    And the user confirms the renewal
    Then the renewal confirmation should be displayed

  @calculator
  Scenario: Premium calculator validates required fields
    When the user opens the premium calculator
    And the user submits the calculator without entering details
    Then the calculator should show the validation message "Select a policy type"
    And the calculator should show the validation message "Select a plan"
    And the calculator should show the validation message "Enter date of birth"
    And the calculator should show the validation message "Select sum insured"
    And the calculator should show the validation message "Select coverage term"

  @calculator
  Scenario: Premium calculator rejects an underage applicant
    When the user opens the premium calculator
    And the user enters random calculator details with an underage date of birth
    And the user calculates the premium
    Then the calculator should show the validation message "Age must be between 18 and 75"

  @calculator
  Scenario: Premium calculator calculates a random valid quote
    When the user opens the premium calculator
    And the user enters random valid calculator details
    And the user calculates the premium
    Then the premium calculation result should be displayed

  @ignore
  Scenario: Verify legacy policy renewal
    When the user clicks on Renew
    And the user logs in with valid credentials
    And the user clicks on Renew again
    And the user selects the first plan and confirms renewal
    Then the page URL should contain "insurance"

  Scenario: Verify logout
    When the user clicks on Logout
    Then the page URL should contain "insurance"

  Scenario: Verify raise support ticket
    When the user clicks on Support
    And the user logs in with valid credentials
    And the user clicks on Support again
    And the user raises a ticket with subject "Policy Renewal Assistance" and description "I need assistance with renewing my insurance policy."
    Then the page URL should contain "insurance"

  Scenario: Verify buy new policy
    When the user clicks on Buy Policy
    And the user selects Vehicle Insurance
    And the user selects a plan
    And the user continues from plan selection
    And the user fills applicant details
    And the user fills vehicle details
    And the user fills nominee details
    And the user pays and submits
    Then the user should be redirected to the dashboard

  Scenario: Delete the first nominee
    When the user clicks on Nominees
    And the user deletes the first nominee
    Then the nominee count should decrease by one
    When the user adds a nominee with random details
    Then the nominee count should return to its original value

  Scenario: Add a nominee with random details
    When the user clicks on Nominees
    And the user makes room for a nominee
    And the user adds a nominee with random details
    Then the nominee count should increase by one
