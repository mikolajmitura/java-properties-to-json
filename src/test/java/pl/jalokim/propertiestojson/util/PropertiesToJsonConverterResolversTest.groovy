package pl.jalokim.propertiestojson.util

import groovy.json.JsonSlurper
import pl.jalokim.propertiestojson.resolvers.primitives.IntegerJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.ObjectFromTextJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveArrayJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.StringJsonTypeResolver
import spock.lang.Specification

class PropertiesToJsonConverterResolversTest extends Specification {

    def "all resolvers (primitive arrays test)"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        String json = converter.parsePropertiesFromFileToJson("src/test/resources/arrayCombinations.properties", "arraytexts")
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.arraytexts[0] == 1
        jsonObject.arraytexts[1] == 23.0
        jsonObject.arraytexts[2] == 5
        jsonObject.arraytexts[3] == false
        jsonObject.arraytexts[4] == "text"
    }

    def "only integers, string and primitive arrays"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(new PrimitiveArrayJsonTypeResolver(),
                new IntegerJsonTypeResolver(), new StringJsonTypeResolver())
        String json = converter.parsePropertiesFromFileToJson("src/test/resources/arrayCombinations.properties", "arraytexts")
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.arraytexts[0] == 1
        jsonObject.arraytexts[1] == "23.0"
        jsonObject.arraytexts[2] == 5
        jsonObject.arraytexts[3] == "false"
        jsonObject.arraytexts[4] == "text"
    }

    def "only string without primitive arrays"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(new StringJsonTypeResolver())
        String json = converter.parsePropertiesFromFileToJson("src/test/resources/arrayCombinations.properties", "arraytexts")
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.arraytexts == "1,23.0,5,false,text"
    }

    def "only json from text and string resolver without primitive arrays"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(new ObjectFromTextJsonTypeResolver(), new StringJsonTypeResolver())
        String json = converter.parsePropertiesFromFileToJson("src/test/resources/arrayCombinations.properties", "arraytexts", "jsonObject", "jsonArray")
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.arraytexts == "1,23.0,5,false,text"
        jsonObject.jsonObject.fieldName == 2
        jsonObject.jsonObject.text == "textValue"
        jsonObject.jsonArray[0] == 123
        jsonObject.jsonArray[1] == 1234
    }
}
