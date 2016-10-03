Feature: William Hill

Scenario: A happy holiday maker
          Given I like to holiday in "Sydney"
          And I only like to holiday on "Thursday"
          When I look up the weather forecast
          Then I receive the weather forecast
          And the temperature is warmer than "10.0f" degrees
          
