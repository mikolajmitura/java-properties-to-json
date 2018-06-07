package pl.jalokim.propertiestojson.util

import spock.lang.Specification
import spock.lang.Unroll

class PropertiesToJsonParserWithoutArraysTest extends Specification {

    @Unroll
    def "parseToJsonWithoutArrays #value #expectedJson"() {
        given:
        Properties properties = new Properties()
        properties.put("key", value)

        when:
        def json = PropertiesToJsonParser.parseToJsonWithoutArrays(properties)

        then:
        expectedJson == json

        where:
        value            || expectedJson
        "key1,key2,key3" || "{\"key\":\"key1,key2,key3\"}"
        "key1"           || "{\"key\":\"key1\"}"
    }

    def "parsePropertiesFromFileToJson"() {
        given:

        when:
        def json = PropertiesToJsonParser.parsePropertiesFromFileToJsonWithoutArrays('src/test/resources/withoutarrays.properties')

        then:
        "{\"example\":\"Dogs, cats, mouses, lions\"}" == json
    }

}