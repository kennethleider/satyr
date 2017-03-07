@Ken
Feature: Samples

  Scenario: Check TSDB
    # Submit data
    # Wait for processing
    # Checking results
#    Then the last value of "counter/osa.sessions/count" is "0"
    Then the last value of "counter/osa.sessions/count" with tags "operator=comcast" is "0"
#    Then the last values of "counter/osa.opportunity/count" are:
#      | 439 | 442 | 451 | 450 | 0 |
#    Then the last values of "counter/osa.opportunity/count" with tag "operator=comcast" are:
#      | 439 | 442 | 451 | 450 | 0 |
