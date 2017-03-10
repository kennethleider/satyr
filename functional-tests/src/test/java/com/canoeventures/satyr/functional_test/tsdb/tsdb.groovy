package com.canoeventures.satyr.functional_test.tsdb

import com.canoeventures.common.cucumber.validation.AndValidation
import com.canoeventures.common.cucumber.validation.SearchCommandEnum
import com.canoeventures.common.cucumber.validation.Validation
import com.canoeventures.common.cucumber.validation.ValidationResult
import cucumber.api.DataTable
import org.testng.Assert

import static com.canoeventures.common.cucumber.CucumberUtils.OnceBefore
import static cucumber.api.groovy.EN.Then
import static cucumber.api.groovy.Hooks.Before

TSDBUtil tsdbUtil
OnceBefore {
    tsdbUtil = applicationContext.getBean(TSDBUtil.class)
}

Before {
    tsdbUtil.setStartTime()
}

Then (~'^the last value of metric "([^"]*?)" is "([^"]*?)"') { String metric, String value ->
    String[] dataPoints = tsdbUtil.query(metric,"")
    ValidationResult result = new Validation(metric, value, SearchCommandEnum.EQUALS, dataPoints.last()).validate()
    Assert.assertTrue(result.passed, result.report)
}

Then (~'^the last value of metric "([^"]*?)" with tag(?:s)? "([^"]*?)" is "([^"]*?)"') { String metric, String tags, String value ->
    String[] dataPoints = tsdbUtil.query(metric, tags)
    ValidationResult result = new Validation("${metric}{$tags}", value, SearchCommandEnum.EQUALS, dataPoints.last()).validate()
    Assert.assertTrue(result.passed, result.report)
}

Then(~/^the last values of metric "([^"]*?)" are:$/) { String metric, DataTable values ->
    String[] dataPoints = tsdbUtil.query(metric,"")
    String[] expected = values.asList(String.class)
    Integer size = expected.size()
    def validations = expected.toList().withIndex().collect { value, i ->
        new Validation("${metric}[${i}]", value, SearchCommandEnum.EQUALS, dataPoints[i - size])
    }
    ValidationResult result = new AndValidation(validations).validate()

    Assert.assertTrue(result.passed, result.report)
}

Then(~/^the last values of metric "([^"]*?)" with tag(?:s)? "([^"]*?)" are:$/) { String metric, String tags, DataTable values ->
    String[] dataPoints = tsdbUtil.query(metric,key)
    String[] expected = values.asList(String.class)
    Integer size = expected.size()
    def validations = expected.toList().withIndex().collect { value, i ->
        new Validation("${metric}{$tags}[${i}]", value, SearchCommandEnum.EQUALS, dataPoints[i - size])
        }
    ValidationResult result = new AndValidation(validations).validate()

    Assert.assertTrue(result.passed, result.report)
}



