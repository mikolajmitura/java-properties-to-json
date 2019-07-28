package pl.jalokim.propertiestojson.util

import groovy.json.JsonSlurper
import pl.jalokim.propertiestojson.helper.PropertyKeysOrderResolverForTest
import pl.jalokim.propertiestojson.resolvers.primitives.BooleanJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.NumberJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.ObjectFromTextJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveArrayJsonTypeResolver
import pl.jalokim.propertiestojson.util.exception.CannotOverrideFieldException
import spock.lang.Specification

import static PropertiesToJsonParsePropertiesExceptionTest.setUpMockPickupKeysOrder

class PropertiesToJsonConverterArraysTest extends Specification {

    def jsonSlurper = new JsonSlurper()
    
    def "create array without problem with different types on every index"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        def json = converter.convertPropertiesFromFileToJson("src/test/resources/arrays/mixin_types_in_array.properties")
        print(json)
        then:
        false
    }

    def "multi dimensional array with simple values"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        PropertyKeysOrderResolverForTest keyOrderResolver = new PropertyKeysOrderResolverForTest()
        keyOrderResolver.setUpMockKeys(
                "arrayWitObjects[0][0].somefield.nextField",
                "arrayWitObjects[0][1].somefield.nextField1",
                "arrayWitObjects[0][1].somefield.nextField2",
                "arrayWitObjects[1][0].somefield.nextField",
                "arrayWitObjects[1][1].somefield.nextField1",
                "arrayWitObjects[1][1].somefield.nextField2",
        )
        converter.setPropertyKeysOrderResolver(keyOrderResolver)

        def json = converter.convertPropertiesFromFileToJson("src/test/resources/arrays/multi_dim_array_in_path_object_values.properties")
        print(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.arrayWitObjects[0][0].somefield.nextField=="test00"
        jsonObject.arrayWitObjects[0][1].somefield.nextField1=="test01_field1"
        jsonObject.arrayWitObjects[0][1].somefield.nextField2=="test01_field2"
        jsonObject.arrayWitObjects[1][0].somefield.nextField=="test10"
        jsonObject.arrayWitObjects[1][1].somefield.nextField1=="test11_field1"
        jsonObject.arrayWitObjects[1][1].somefield.nextField2=="test11_field2"
    }

    def "multi dimensional array with object values"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        def json = converter.convertPropertiesFromFileToJson("src/test/resources/arrays/multi_dim_array_in_path_object_values.properties")
        print(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.arrayWitObjects[0][0].somefield.nextField=="test00"
        jsonObject.arrayWitObjects[0][1].somefield.nextField1=="test01_field1"
        jsonObject.arrayWitObjects[0][1].somefield.nextField2=="test01_field2"
        jsonObject.arrayWitObjects[1][0].somefield.nextField=="test10"
        jsonObject.arrayWitObjects[1][1].somefield.nextField1=="test11_field1"
        jsonObject.arrayWitObjects[1][1].somefield.nextField2=="test11_field2"
    }

    def "primitive arrays elements resolved to multi dim array"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        def json = converter.convertPropertiesFromFileToJson("src/test/resources/arrays/arrays_as_value_in_array_elements.properties")
        print(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.array[0][0]=="value1"
        jsonObject.array[0][1]=="value2"

        jsonObject.array[1][0]=="value3"
        jsonObject.array[1][1]=="value4"

        jsonObject.array[2][0]=="value5"
        jsonObject.array[2][1]=="value6"
    }

    def "return array with mixin types (primitives and objects)"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        def json = converter.convertPropertiesFromFileToJson('src/test/resources/arraysMixinTypes.properties')
        print(json)
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

    def "primitive array as first and next will be populated by indexed elements"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        setUpMockPickupKeysOrder(converter,
                "object.test",
                "object.test[7]",
                "object.test[6]",
                "object.test[101]",
                "object.test[102]",
                "object.test[9]",
                "test")
        String json = converter.convertPropertiesFromFileToJson("src/test/resources/arrayCombinations.properties")
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.object.test[0] == "0_"
        jsonObject.object.test[1] == "1_"
        jsonObject.object.test[2] == "2_"
        jsonObject.object.test[3] == "3_"
        jsonObject.object.test[4] == "4_"
        jsonObject.object.test[5] == "asdf6"
        jsonObject.object.test[6] == "asdf7"
        jsonObject.object.test[7] == "asdf9"
        jsonObject.object.test[8] == "asdf101"
        jsonObject.object.test[9] == [1, 2, 3, 4]
    }

    def "indexed elements as first will be merged with primitive array"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        setUpMockPickupKeysOrder(converter,
                "object.test[7]",
                "object.test[6]",
                "object.test[101]",
                "object.test[9]",
                "object.test",
                "test")
        converter.convertPropertiesFromFileToJson("src/test/resources/arrayCombinations.properties")
        then:
        CannotOverrideFieldException e = thrown()
        e.getMessage() == "Cannot override value at path: 'object.test', current value is: '[\"asdf6\",\"asdf7\",\"asdf9\",\"asdf101\"]', problematic property key: 'object.test'"
    }

    def "return array with text elements when provided others resolvers and PrimitiveArrayJsonTypeResolver(false)"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
                new PrimitiveArrayJsonTypeResolver(false),
                new ObjectFromTextJsonTypeResolver(),
                new NumberJsonTypeResolver(),
                new BooleanJsonTypeResolver()
        )
        def json = converter.convertPropertiesFromFileToJson('src/test/resources/arraysMixinTypes.properties')
        print(json)
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
        jsonObject.otherArray[2] == "true"
        jsonObject.otherArray[3] == "11.1"
        jsonObject.otherArray[4] == "test"
        jsonObject.otherArray[5] == "1"
        jsonObject.otherArray[6] == "false"
        jsonObject.otherArray[7] == "FAlSE"
        jsonObject.otherArray[8] == "FALSSE"
        jsonObject.otherArray[9] == "\"in quotation marks\""
    }
}