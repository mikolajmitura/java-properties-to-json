package pl.jalokim.propertiestojson.util

import groovy.json.JsonSlurper
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException
import spock.lang.Specification

import static pl.jalokim.propertiestojson.util.PropertiesToJsonParserExceptionTest.setUpMockPickupKeysOrder

class PropertiesToJsonConverterArraysTest extends Specification {

    def "return array with mixin types (primitives and objects)"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        def json = converter.parsePropertiesFromFileToJson('src/test/resources/arraysMixinTypes.properties')
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.array[0].name == "Walter"
        jsonObject.array[0].surname == "White"
        jsonObject.array[1].name == "Freddy"
        jsonObject.array[1].surname == "Krueger"
        jsonObject.array[1].nick == "Freddy_k1"
        jsonObject.array[2] == "simpleString"
        jsonObject.array[3] == true
        jsonObject.array[4] == 1
        jsonObject.array[5] == 1.1
        jsonObject.array[6].surname == "Mick"
        jsonObject.array[6].nick == "Freddy_k1"
        jsonObject.array[7].array[0] == "test1"
        jsonObject.array[7].array[1] == "test2"
        jsonObject.otherArray[0] == "test"
        jsonObject.otherArray[1] == "boolean"
        jsonObject.otherArray[2] == true
        jsonObject.otherArray[3] == 11.1
        jsonObject.otherArray[4] == "test"
        jsonObject.otherArray[5] == 1
        jsonObject.otherArray[6] == false
        jsonObject.otherArray[7] == false
        jsonObject.otherArray[8] == "FALSSE"
        jsonObject.otherArray[9] == "\"in quotation marks\""
    }

    def "primitive array as first will be override by indexed elements"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        setUpMockPickupKeysOrder(converter,
                "object.test",
                "object.test[7]",
                "object.test[3]",
                "object.test[6]",
                "object.test[101]",
                "object.test[102]",
                "object.test[9]",
                "test")
        String json = converter.parsePropertiesFromFileToJson("src/test/resources/arrayCombinations.properties")
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.object.test[0]=="0_"
        jsonObject.object.test[1]=="1_"
        jsonObject.object.test[2]=="2_"
        jsonObject.object.test[3]==3
        jsonObject.object.test[4]=="4_"
        jsonObject.object.test[5]=="asdf6"
        jsonObject.object.test[6]=="asdf7"
        jsonObject.object.test[7]=="asdf9"
        jsonObject.object.test[8]=="asdf101"
        jsonObject.object.test[9]==[1,2,3,4]
    }

    def "indexed elements as first will not be override by primitive array will throw error because how to override???"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        setUpMockPickupKeysOrder(converter,
                "object.test[7]",
                "object.test[3]",
                "object.test[6]",
                "object.test[101]",
                "object.test[9]",
                "object.test",
                "test")
        String json = converter.parsePropertiesFromFileToJson("src/test/resources/arrayCombinations.properties")
        then:
        ParsePropertiesException e = thrown()
        e.getMessage() == "Current field: 'test' is already considered as array JSON type because already this field has value: [3,\"asdf6\",\"asdf7\",\"asdf9\",\"asdf101\"] \n" +
                "(error for given wrong property key: 'object.test')"
    }

}