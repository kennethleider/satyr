package com.canoeventures.satyr.functional_test.avro

import com.canoeventures.common.cucumber.AvroUtil
import com.canoeventures.common.test.XmlUtil
import cucumber.api.DataTable

import static cucumber.api.groovy.EN.When
import static cucumber.api.groovy.Hooks.Before

AvroUtil avro
XmlUtil xmlUtil

Before {
    avro = applicationContext.getBean(AvroUtil.class)
    xmlUtil = applicationContext.getBean("xmlUtil")
}

/**
 * Defines a ConsumerRecord from a json template modified by the rules in attached table
 *
 * @project Common Lib
 * @module Kafka
 * @type Action
 * @name CREATE RECORD
 * @ident 02bfc9b460671340
 * @example 1 Given a request is created from template "HP_TWC_Response_KW.xml" modified with:
 * | Placement.Tracking            | [PEID-100]              |
 * | Placement[1].Tracking         | [PEID-100]              |
 * | Placement.Content.Tracking    | {Placement.Tracking}    |
 * | Placement[1].Content.Tracking | {Placement[1].Tracking} |
 * | PlacementDecision[2]          | [MISSING]               |
 */
When (~'^a "([^"]*?)" record is created from template "([^"]*?)":') { String schemaName, String fileName, DataTable modifications ->
    avro.createRecords(schemaName, fileName, modifications).each {
        avro.addRecord(schemaName, it.second)
    }
}
