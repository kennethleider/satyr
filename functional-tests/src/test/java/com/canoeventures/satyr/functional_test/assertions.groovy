package com.canoeventures.satyr.functional_test

import com.canoeventures.common.cucumber.validation.AndValidation
import com.canoeventures.common.cucumber.validation.SearchCommandEnum
import com.canoeventures.common.cucumber.validation.Validation
import com.canoeventures.common.cucumber.validation.ValidationResult
import com.canoeventures.common.test.ProfilePropertyProvider
import cucumber.api.DataTable
import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import org.testng.Assert

import static com.canoeventures.common.cucumber.CucumberUtils.OnceBefore
import static cucumber.api.groovy.EN.Then

ProfilePropertyProvider propertyProvider

OnceBefore {
    propertyProvider = applicationContext.getBean("profilePropertyProvider")
}

Then (~'^the last value of "([^"]*?)" is "([^"]*?)"') { String metric, String value ->
    String[] datapoints = getDatapoints(metric)
    ValidationResult result = new Validation(metric, value, SearchCommandEnum.EQUALS, datapoints.last()).validate()
    Assert.assertTrue(result.passed, result.report)
}

Then (~'^the last value of "([^"]*?)" with tag(?:s)? "([^"]*?)" is "([^"]*?)"') { String metric, String tags, String value ->
    String key = "${metric}{$tags}"
    String[] datapoints = getDatapoints(key)
    ValidationResult result = new Validation(key, value, SearchCommandEnum.EQUALS, datapoints.last()).validate()
    Assert.assertTrue(result.passed, result.report)
}

Then(~/^the last values of "([^"]*?)" are:$/) { String metric, DataTable values ->
    String[] datapoints = getDatapoints(metric)
    String[] expected = values.asList(String.class)
    Integer size = expected.size()
    def validations = expected.toList().withIndex().collect { value, i ->
        new Validation("${metric}[${i}]", value, SearchCommandEnum.EQUALS, datapoints[i - size])
    }
    ValidationResult result = new AndValidation(validations).validate()

    Assert.assertTrue(result.passed, result.report)
}

Then(~/^the last values of "([^"]*?)" with tag(?:s)? "([^"]*?)" are:$/) { String metric, String tags, DataTable values ->
    String key = "${metric}{$tags}"
    String[] datapoints = getDatapoints(key)
    String[] expected = values.asList(String.class)
    Integer size = expected.size()
    def validations = expected.toList().withIndex().collect { value, i ->
        new Validation("${key}[${i}]", value, SearchCommandEnum.EQUALS, datapoints[i - size])
        }
    ValidationResult result = new AndValidation(validations).validate()

    Assert.assertTrue(result.passed, result.report)
}

def getDatapoints(String key) {
    URL url = new URL("${propertyProvider.getSpringProperty("tsdb.url")}/api/query?start=4w-ago&m=sum:${propertyProvider.getSpringProperty("deployment")}/${key}")
    println("Querying tsdb: ${url}")
    HttpURLConnection connection = url.openConnection()
    try {
        def text = new JsonSlurper().parseText(connection.inputStream.text)
        LazyMap datapoints = text.dps[0]
        if (datapoints) {
            return datapoints.entrySet().collect { entry -> [entry.key.toInteger(), entry.value] }.toSorted {a, b -> a[0] <=> b[0] }.collect { it[1].toString() }.toArray()
        } else {
            Assert.fail("No values recorded for ${key} within the last minute")
        }
    } catch(IOException e) {
        def text = new JsonSlurper().parseText(connection.errorStream.text)
        Assert.fail(text.error.message)
    }
}