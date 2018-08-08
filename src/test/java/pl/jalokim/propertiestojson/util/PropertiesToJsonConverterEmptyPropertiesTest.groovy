package pl.jalokim.propertiestojson.util

import groovy.json.JsonSlurper
import spock.lang.Specification

class PropertiesToJsonConverterEmptyPropertiesTest extends Specification {

    def "json has empty string for empty property and null value for null value for property"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        String json = converter.parsePropertiesFromFileToJson("src/test/resources/primitiveTypes.properties")
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.complexObject.nullValue == null
        jsonObject.complexObject.empty == ""
    }
}
