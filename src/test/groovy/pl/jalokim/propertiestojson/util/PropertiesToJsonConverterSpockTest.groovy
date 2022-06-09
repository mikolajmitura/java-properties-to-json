package pl.jalokim.propertiestojson.util

import groovy.json.JsonSlurper
import pl.jalokim.propertiestojson.object.AbstractJsonType
import pl.jalokim.propertiestojson.object.NumberJsonType
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver
import pl.jalokim.propertiestojson.resolvers.primitives.object.AbstractObjectToJsonTypeConverter
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

    def "try resolve objects from properties when provided tryConvertStringValuesToOtherObjects=true"() {
        given:
        def date = new Date(123123)
        def properties = new Properties()
        properties.put("root.name", "some-name")
        properties.put("root.surname", "some-surname")
        properties.put("root.someBoolean1", "false")
        properties.put("someBoolean2", "true")
        properties.put("someNumber", 3.0)
        properties.put("someDate", date)

        when:
        def converter = PropertiesToJsonConverterBuilder.builder()
            .defaultAndCustomObjectToJsonTypeConverters(new DateToTimestamp())
            .build()

        def json = converter.convertToJson(properties, true)
        println json
        def jsonSlurper = new JsonSlurper()
        def jsonObject = jsonSlurper.parseText(json)

        then:
        jsonObject.root.name == "some-name"
        jsonObject.root.surname == "some-surname"
        jsonObject.root.someBoolean1 == false
        jsonObject.someBoolean2 == true
        jsonObject.someNumber == 3.0
        jsonObject.someDate == 123123
    }

    def "try resolve objects from properties when provided tryConvertStringValuesToOtherObjects=false"() {
        given:
        def date = new Date(123123)
        def properties = new Properties()
        properties.put("root.name", "some-name")
        properties.put("root.surname", "some-surname")
        properties.put("root.someBoolean1", "false")
        properties.put("someBoolean2", "true")
        properties.put("someNumber", 3.0)
        properties.put("someDate", date)

        when:
        def converter = PropertiesToJsonConverterBuilder.builder()
            .defaultAndCustomObjectToJsonTypeConverters(new DateToTimestamp())
            .build()

        def json = converter.convertToJson(properties, false)
        def jsonSlurper = new JsonSlurper()
        def jsonObject = jsonSlurper.parseText(json)

        then:
        jsonObject.root.name == "some-name"
        jsonObject.root.surname == "some-surname"
        jsonObject.root.someBoolean1 == "false"
        jsonObject.someBoolean2 == "true"
        jsonObject.someNumber == 3.0
        jsonObject.someDate == 123123
    }

    def "try resolve objects from map when provided tryConvertStringValuesToOtherObjects=true"() {
        given:
        def date = new Date(123123)
        def properties = [
            "root.name"        : "some-name",
            "root.surname"     : "some-surname",
            "root.someBoolean1": "false",
            "someBoolean2"     : "true",
            "someNumber"       : 3.0,
            "someDate"         : date,
        ]

        when:
        def converter = PropertiesToJsonConverterBuilder.builder()
            .defaultAndCustomObjectToJsonTypeConverters(new DateToTimestamp())
            .build()

        def json = converter.convertFromValuesAsObjectMap(properties, true)
        println json
        def jsonSlurper = new JsonSlurper()
        def jsonObject = jsonSlurper.parseText(json)

        then:
        jsonObject.root.name == "some-name"
        jsonObject.root.surname == "some-surname"
        jsonObject.root.someBoolean1 == false
        jsonObject.someBoolean2 == true
        jsonObject.someNumber == 3.0
        jsonObject.someDate == 123123
    }

    def "try resolve objects from map when provided tryConvertStringValuesToOtherObjects=false"() {
        given:
        def date = new Date(123123)
        def properties = [
            "root.name"        : "some-name",
            "root.surname"     : "some-surname",
            "root.someBoolean1": "false",
            "someBoolean2"     : "true",
            "someNumber"       : 3.0,
            "someDate"         : date,
        ]

        when:
        def converter = PropertiesToJsonConverterBuilder.builder()
            .defaultAndCustomObjectToJsonTypeConverters(new DateToTimestamp())
            .build()

        def json = converter.convertFromValuesAsObjectMap(properties, false)
        def jsonSlurper = new JsonSlurper()
        def jsonObject = jsonSlurper.parseText(json)

        then:
        jsonObject.root.name == "some-name"
        jsonObject.root.surname == "some-surname"
        jsonObject.root.someBoolean1 == "false"
        jsonObject.someBoolean2 == "true"
        jsonObject.someNumber == 3.0
        jsonObject.someDate == 123123
    }

    private static class DateToTimestamp extends AbstractObjectToJsonTypeConverter<Date> {

        @Override
        Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            Date convertedValue, String propertyKey) {
            Optional.of(new NumberJsonType(convertedValue.getTime()))
        }
    }

}
