package pl.jalokim.propertiestojson.util

import groovy.json.JsonSlurper
import spock.lang.Specification

class PropertiesToJsonConverterEmptyPropertiesTest extends Specification {

    def "json has empty string for empty property and null value for null value for property"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        String json = converter.convertPropertiesFromFileToJson("src/test/resources/primitiveTypes.properties")
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.complexObject.nullValue == null
        jsonObject.complexObject.empty == ""
    }

    def "json has null for null value of element in map"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        Map<String, String> map = new HashMap<>()
        map.put("man.nullValue", null)
        map.put("man.nullToo", "null")
        map.put("man.empty", "")
        String json = converter.convertToJson(map)
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.man.nullValue == null
        jsonObject.man.nullToo == null
        jsonObject.man.empty == ""
    }
}
