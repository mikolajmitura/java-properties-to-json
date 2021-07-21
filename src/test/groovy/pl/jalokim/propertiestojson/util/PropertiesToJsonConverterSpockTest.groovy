package pl.jalokim.propertiestojson.util

import groovy.json.JsonSlurper
import spock.lang.Specification

class PropertiesToJsonConverterSpockTest extends Specification {

    def "not split all by dot"() {
        given:

        String propertyKey = "field.nextfield.array[0][5].anotherField[external.key.leaf].anotherField[[external.key.leaf][some.strange.things]].fieldLeaf"
        def map = [(propertyKey): "someValue"]
        when:
        def converter = PropertiesToJsonConverterBuilder.builder().build()
        def json = converter.convertFromValuesAsObjectMap(map)
        def jsonSlurper = new JsonSlurper()
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.field.nextfield.array[0][5]["anotherField[external.key.leaf]"]["anotherField[[external.key.leaf][some.strange.things]]"].fieldLeaf ==
            "someValue"
    }
}
