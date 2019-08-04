package pl.jalokim.propertiestojson.util

import groovy.json.JsonSlurper
import pl.jalokim.propertiestojson.helper.PropertyKeysOrderResolverForTest
import pl.jalokim.propertiestojson.resolvers.primitives.BooleanJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.NumberJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.ObjectFromTextJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveArrayJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.StringJsonTypeResolver
import spock.lang.Specification

import static PropertiesToJsonParsePropertiesExceptionTest.setUpMockPickupKeysOrder

class PropertiesToJsonConverterArraysTest extends Specification {

    def jsonSlurper = new JsonSlurper()

    def "create array without problem with different types on every index"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        def json = converter.convertPropertiesFromFileToJson("src/test/resources/arrays/mixin_types_in_array.properties")
        print(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.someObject.array[0][0] == [12, 13, 14, "test", true]
        jsonObject.someObject.array[0][1] == [1, 1.1, true, "arrayValue1"]
        jsonObject.someObject.array[0][2] == [2, 2.2, true, "arrayValue2"]
        jsonObject.someObject.array[1] == "test"
        jsonObject.someObject.array[2][0] == "test1"
        jsonObject.someObject.array[2][1] == "test2"
        jsonObject.someObject.array[3][0].field == "value1"
        jsonObject.someObject.array[3][1].field == "value2"
        jsonObject.someObject.array[3][2] == "simpleText"
        jsonObject.someObject.array[3][3][0] == 1
        jsonObject.someObject.array[3][3][1] == 2
        jsonObject.someObject.array[3][3][2] == [true, "boolean"]

        jsonObject.someObject.array[3][3][2][0] == true
        jsonObject.someObject.array[3][3][2][1] == "boolean"

        jsonObject.someObject.array[3][3][3].nextObjectField == "value_02"
        jsonObject.someObject.array[4].some_field == "value3"
        jsonObject.someObject.array[5] == ["test", true]
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
        jsonObject.arrayWitObjects[0][0].somefield.nextField == "test00"
        jsonObject.arrayWitObjects[0][1].somefield.nextField1 == "test01_field1"
        jsonObject.arrayWitObjects[0][1].somefield.nextField2 == "test01_field2"
        jsonObject.arrayWitObjects[1][0].somefield.nextField == "test10"
        jsonObject.arrayWitObjects[1][1].somefield.nextField1 == "test11_field1"
        jsonObject.arrayWitObjects[1][1].somefield.nextField2 == "test11_field2"
    }

    def "multi dimensional array with object values"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        def json = converter.convertPropertiesFromFileToJson("src/test/resources/arrays/multi_dim_array_in_path_object_values.properties")
        print(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.arrayWitObjects[0][0].somefield.nextField == "test00"
        jsonObject.arrayWitObjects[0][1].somefield.nextField1 == "test01_field1"
        jsonObject.arrayWitObjects[0][1].somefield.nextField2 == "test01_field2"
        jsonObject.arrayWitObjects[1][0].somefield.nextField == "test10"
        jsonObject.arrayWitObjects[1][1].somefield.nextField1 == "test11_field1"
        jsonObject.arrayWitObjects[1][1].somefield.nextField2 == "test11_field2"
    }

    def "primitive arrays elements resolved to multi dim array"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        def json = converter.convertPropertiesFromFileToJson("src/test/resources/arrays/arrays_as_value_in_array_elements.properties")
        print(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.array[0][0] == "value1"
        jsonObject.array[0][1] == "value2"

        jsonObject.array[1][0] == "value3"
        jsonObject.array[1][1] == "value4"

        jsonObject.array[2][0] == "value5"
        jsonObject.array[2][1] == "value6"
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
                "object.test[6]", \
                 "object.test[7]",
                "object.test[9]",
                "object.test[10]",
                "object.test[11]",
                "test")
        String json = converter.convertToJson(getOverrideArraysProperties())
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.object.test[0] == "0_"
        jsonObject.object.test[1] == "1_"
        jsonObject.object.test[2] == "2_"
        jsonObject.object.test[3] == "3_"
        jsonObject.object.test[4] == "4_"
        jsonObject.object.test[5] == null
        jsonObject.object.test[6] == "asdf6"
        jsonObject.object.test[7] == "asdf7"
        jsonObject.object.test[8] == null
        jsonObject.object.test[9] == "asdf9"
        jsonObject.object.test[10] == "asdf101"
        jsonObject.object.test[11] == [1, 2, 3, 4]
    }

    def "indexed elements as first will be merged with primitive array"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        setUpMockPickupKeysOrder(converter,
                "object.test[7]",
                "object.test[6]",
                "object.test[10]",
                "object.test[9]",
                "object.test",
                "object.test[11]",
                "test")
        String json = converter.convertToJson(getOverrideArraysProperties())
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.object.test[0] == "0_"
        jsonObject.object.test[1] == "1_"
        jsonObject.object.test[2] == "2_"
        jsonObject.object.test[3] == "3_"
        jsonObject.object.test[4] == "4_"
        jsonObject.object.test[5] == null
        jsonObject.object.test[6] == "asdf6"
        jsonObject.object.test[7] == "asdf7"
        jsonObject.object.test[8] == null
        jsonObject.object.test[9] == "asdf9"
        jsonObject.object.test[10] == "asdf101"
        jsonObject.object.test[11] == [1, 2, 3, 4]
    }

    def "merge object in 2 dim array element"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        setUpMockPickupKeysOrder(converter,
                "object.test[2][2]",
                "object.test[2][2].field2")

        Map<String, String> properties = new HashMap<>()
        properties.put("object.test[2][2]", "{\"field1\":\"field_text\"}")
        properties.put("object.test[2][2].field2", "2")

        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.object.test[0] == null
        jsonObject.object.test[1] == null
        jsonObject.object.test[2][2].field1 == "field_text"
        jsonObject.object.test[2][2].field2 == 2
    }

    def "merge object in array element"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        setUpMockPickupKeysOrder(converter,
                "object.test[2]",
                "object.test[2].field2")

        Map<String, String> properties = new HashMap<>()
        properties.put("object.test[2]", "{\"field1\":\"field_text\"}")
        properties.put("object.test[2].field2", "2")

        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.object.test[0] == null
        jsonObject.object.test[1] == null
        jsonObject.object.test[2].field1 == "field_text"
        jsonObject.object.test[2].field2 == 2
    }

    def "merge array as second element in 2 dim array (first primitive array)"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        setUpMockPickupKeysOrder(converter,
                "object.test[2][2]",
                "object.test[2][2][3]")

        Map<String, String> properties = new HashMap<>()
        properties.put("object.test[2][2]", "[0, 1, 2]")
        properties.put("object.test[2][2][3]", "true")

        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.object.test[0] == null
        jsonObject.object.test[1] == null
        jsonObject.object.test[2][2] == [0, 1, 2, true]
    }

    def "merge array as second element in 2 dim array (first array index)"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        setUpMockPickupKeysOrder(converter,
                "object.test[2][2][3]",
                "object.test[2][2]")

        Map<String, String> properties = new HashMap<>()
        properties.put("object.test[2][2]", "[0, 1, 2]")
        properties.put("object.test[2][2][3]", "true")

        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.object.test[0] == null
        jsonObject.object.test[1] == null
        jsonObject.object.test[2][2] == [0, 1, 2, true]
    }

    def "array json from text as first will be merged with indexed elements"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        setUpMockPickupKeysOrder(converter,
                "object.test[11]",
                "object.test[11][4]",
                "test")
        def properties = getOverrideArraysProperties()
        properties.put("object.test[11][4]", "[\"next_value\", 12, true]")
        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.object.test[11] == [1, 2, 3, 4, ["next_value", 12, true]]
        jsonObject.object.test[11][4] == ["next_value", 12, true]
    }

    def "indexed elements as first will be merged with array json from text"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        setUpMockPickupKeysOrder(converter,
                "object.test[11][4]",
                "object.test[11][5]",
                "object.test[11]",
                "test")
        def properties = getOverrideArraysProperties()
        properties.put("object.test[11][4]", "[\"next_value\", 12, true]")
        properties.put("object.test[11][5]", "{\"field\":\"test\", \"field2\": \"test_2\"}")
        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.object.test[11][0] == 1
        jsonObject.object.test[11][1] == 2
        jsonObject.object.test[11][2] == 3
        jsonObject.object.test[11][3] == 4
        jsonObject.object.test[11][4] == ["next_value", 12, true]
        jsonObject.object.test[11][5].field == "test"
        jsonObject.object.test[11][5].field2 == "test_2"
    }

    private Map<String, String> getOverrideArraysProperties() {
        Map<String, String> map = new HashMap<>()
        map.put("object.test[7]", "asdf7")
        map.put("object.test[6]", "asdf6")
        map.put("object.test[9]", "asdf9")
        map.put("object.test[10]", "asdf101")
        map.put("object.test[11]", "1,2,3,4")
        map.put("object.test", "0_, 1_, 2_ , 3_ , 4_")
        map.put("test", "0_, 1_, 2_ , 3_ , 4_")
        return map
    }

    def "only simple text resolver but objects and array will have numbers, boolean etc"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
                new ObjectFromTextJsonTypeResolver(),
                new StringJsonTypeResolver())

        Map<String, String> map = new HashMap<>()
        map.put("field", "true")
        map.put("number", "9")
        map.put("object.array", "[true, \"test\", 12, 12.0]")
        map.put("object.array[4]", "true")
        map.put("object.nextObject", "{\"numberField\": 12, \"boolField\": true, \"textField\": \"Some_text\"}")

        def json = converter.convertToJson(map)
        print(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.field == "true"
        jsonObject.number == "9"
        jsonObject.object.array == [true, "test", 12, 12.0, "true"]
        jsonObject.object.nextObject.numberField == 12
        jsonObject.object.nextObject.boolField == true
        jsonObject.object.nextObject.textField == "Some_text"
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