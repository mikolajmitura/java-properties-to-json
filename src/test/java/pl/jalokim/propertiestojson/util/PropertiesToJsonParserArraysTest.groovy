package pl.jalokim.propertiestojson.util

import groovy.json.JsonSlurper
import pl.jalokim.propertiestojson.helper.PropertyKeysPickup
import spock.lang.Specification

class PropertiesToJsonParserArraysTest extends Specification {

    def "return array with mixin types (primitives and objects)"() {
        def jsonSlurper = new JsonSlurper()
        PropertiesToJsonParser.setPropertyKeysPickup(new PropertyKeysPickup())
        when:
        def json = PropertiesToJsonParser.parsePropertiesFromFileToJson('src/test/resources/arraysMixinTypes.properties')
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

}