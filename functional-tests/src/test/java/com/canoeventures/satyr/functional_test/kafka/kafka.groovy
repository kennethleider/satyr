package com.canoeventures.satyr.functional_test.kafka

import com.canoeventures.common.cucumber.AvroUtil
import com.canoeventures.common.cucumber.KafkaUtil
import cucumber.api.DataTable

import static cucumber.api.groovy.EN.Given
import static cucumber.api.groovy.EN.When
import static cucumber.api.groovy.Hooks.After
import static cucumber.api.groovy.Hooks.Before

KafkaUtil kafka
AvroUtil avro

Before {
    kafka = applicationContext.getBean(KafkaUtil.class)
    avro = applicationContext.getBean(AvroUtil.class)
}

When (~'^a "([^"]*?)" record is sent to the "([^"]*?)" topic:') { String schemaName, String topic, DataTable modifications ->
    kafka.produce(topic, avro.createRecords(schemaName, modifications))
}

When (~'^multiple "([^"]*?)" records are sent to the "([^"]*?)" topic:') { String schemaName, String topic, DataTable modifications ->
    kafka.produce(topic, avro.createRecords(schemaName, modifications))
}


Given (~'^the last "([^"]*?)" record is sent to the "([^"]*?)" topic') { String schemaName, String topic ->
    kafka.produce(topic, avro.getSchema(schemaName), avro.records[schemaName].last())
}

Given (~'^all "([^"]*?)" records are sent to the "([^"]*?)" topic') { String schemaName, String topic ->
    kafka.produce(topic, avro.getSchema(schemaName), avro.records[schemaName])
}

After() {
    kafka.backup()
}