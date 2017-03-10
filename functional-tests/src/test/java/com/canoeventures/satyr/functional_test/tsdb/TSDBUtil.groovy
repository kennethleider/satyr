package com.canoeventures.satyr.functional_test.tsdb

import com.canoeventures.common.test.ProfilePropertyProvider
import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.testng.Assert

import java.time.Instant

@Component
class TSDBUtil {

    @Autowired
    private ProfilePropertyProvider propertyProvider

    private String startTime = Instant.now().toString()

    public void setStartTime(Instant time = Instant.now()) {
        println("Updating TSDB start time to ${time}")
        startTime = time.getEpochSecond().toString()
    }

    public String[] query(String metric, String tags) {
        String deployment = propertyProvider.getSpringProperty("deployment").toLowerCase()
        String baseURL = propertyProvider.getSpringProperty("tsdb.url")
        URL url = new URL("${baseURL}/api/query?start=${startTime}&m=sum:${deployment}/${metric}{${tags}}")
        println("Querying tsdb: ${url}")
        HttpURLConnection connection = url.openConnection()
        try {
            def text = new JsonSlurper().parseText(connection.inputStream.text)
            LazyMap datapoints = text.dps[0]
            if (datapoints) {
                return datapoints.entrySet().collect { entry -> [entry.key.toInteger(), entry.value] }.toSorted {a, b -> a[0] <=> b[0] }.collect { it[1].toString() }.toArray()
            } else {
                Assert.fail("No values recorded for metric '${metric}' with tags '${tags}'")
            }
        } catch(IOException e) {
            def text = new JsonSlurper().parseText(connection.errorStream.text)
            Assert.fail(text.error.message)
        }
    }
}