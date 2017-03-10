Feature: Samples

  Scenario: Create and send
    When a "session" record is sent to the "requests" topic:
      | operator      | comcast |
      | providerId    | abc     |
      | platform      | stb     |
      | region        | west    |
      | VodBackOffice | Xi3     |
    And I wait "3" seconds for tsdb to be updated.
    Then the last value of metric "counter/osa.sessions/count" is "1"
    Then the last value of metric "counter/osa.sessions/count" with tags "operator=comcast" is "1"
    Then the last value of metric "counter/osa.sessions/count" with tags "providerId=abc" is "1"
    Then the last value of metric "counter/osa.sessions/count" with tags "platform=stb" is "1"
    Then the last value of metric "counter/osa.sessions/count" with tags "region=west" is "1"
    Then the last value of metric "counter/osa.sessions/count" with tags "backOffice=Xi3" is "1"

  @Ken
  Scenario: Create with template
    When a "session" record is created from template "default.session.json":
      | operator      | comcast |
      | providerId    | abc     |
      | platform      | stb     |
      | region        | west    |
      | VodBackOffice | Xi3     |
    And the last "session" record is sent to the "requests" topic
    And the last "session" record is sent to the "requests" topic
    And I wait "3" seconds for tsdb to be updated.
    Then the last value of metric "counter/osa.sessions/count" is "2"
    Then the last value of metric "counter/osa.sessions/count" with tags "operator=comcast" is "2"
    Then the last value of metric "counter/osa.sessions/count" with tags "providerId=abc" is "2"
    Then the last value of metric "counter/osa.sessions/count" with tags "platform=stb" is "2"
    Then the last value of metric "counter/osa.sessions/count" with tags "region=west" is "2"
    Then the last value of metric "counter/osa.sessions/count" with tags "backOffice=Xi3" is "2"


  Scenario: Multiple
    When multiple "session" records are sent to the "requests" topic:
      | operator   | comcast | comcast | twc  |
      | providerId | hbc     | nbc     | food |
    And I wait "3" seconds for tsdb to be updated.
    Then the last value of metric "counter/osa.sessions/count" with tags "operator=comcast" is "2"


