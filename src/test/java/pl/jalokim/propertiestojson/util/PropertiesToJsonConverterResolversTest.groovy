package pl.jalokim.propertiestojson.util

import groovy.json.JsonSlurper
import pl.jalokim.propertiestojson.resolvers.primitives.*
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException
import spock.lang.Specification

class PropertiesToJsonConverterResolversTest extends Specification {

    def "all resolvers (primitive arrays test)"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        String json = converter.convertPropertiesFromFileToJson("src/test/resources/arrayCombinations.properties", "arraytexts")
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
                new NumberJsonTypeResolver(), new StringJsonTypeResolver())
        String json = converter.convertPropertiesFromFileToJson("src/test/resources/arrayCombinations.properties", "arraytexts")
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.arraytexts[0] == 1
        jsonObject.arraytexts[1] == 23.0
        jsonObject.arraytexts[2] == 5
        jsonObject.arraytexts[3] == "false"
        jsonObject.arraytexts[4] == "text"
    }

    def "only string without primitive arrays"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(new StringJsonTypeResolver())
        String json = converter.convertPropertiesFromFileToJson("src/test/resources/arrayCombinations.properties", "arraytexts")
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.arraytexts == "1,23.0,5,false,text"
    }

    def "only json from text and string resolver without primitive arrays"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(new ObjectFromTextJsonTypeResolver(), new StringJsonTypeResolver())
        String json = converter.convertPropertiesFromFileToJson("src/test/resources/arrayCombinations.properties", "arraytexts", "jsonObject", "jsonArray")
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.arraytexts == "1,23.0,5,false,text"
        jsonObject.jsonObject.fieldName == 2
        jsonObject.jsonObject.text == "textValue"
        jsonObject.jsonArray[0] == 123
        jsonObject.jsonArray[1] == 1234
        jsonObject.jsonArray[2] == null
        jsonObject.jsonArray[3] == ""
    }

    def "when properties with established string with number value will be converted as string not number"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        Properties properties = createProperties()
        String json = converter.convertToJson(properties)
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.man.stringNumber == "123"
        jsonObject.man.doubleValue == 1.132
        jsonObject.other.doubleValueString == "12.122"
    }

    def "when given simple array then expected array in json"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        Properties properties = new Properties()
        properties.put("man.someArray", [true, false, "string", 12.0, "{\"field\":\"fieldValue\"}", null, null])
        properties.put("man.doubleValue", 1.132)
        String json = converter.convertToJson(properties)
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.man.someArray[0] == true
        jsonObject.man.someArray[1] == false
        jsonObject.man.someArray[2] == "string"
        jsonObject.man.someArray[3] == 12.0
        jsonObject.man.someArray[4] == "{\"field\":\"fieldValue\"}"
        jsonObject.man.someArray[5] == null
        jsonObject.man.someArray[6] == null
        jsonObject.man.doubleValue == 1.132
    }

    def "when given simple array as text then expected array in json"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        String json = converter.convertPropertiesFromFileToJson("src/test/resources/resolvers.properties")
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.man.someArray[0] == true
        jsonObject.man.someArray[1] == false
        jsonObject.man.someArray[2] == "\"string\""
        jsonObject.man.someArray[3] == 12.0
        jsonObject.man.someArray[4] == "\"{\"field\":\"fieldValue\"}\""
        jsonObject.man.someArray[5] == null
        jsonObject.man.someArray[6] == null
        jsonObject.man.someArray[7] == "normal text"
        jsonObject.man.someArray[8].field == "fieldValue"

        jsonObject.man.someArray2[0] == true
        jsonObject.man.someArray2[1] == false
        jsonObject.man.someArray2[2] == "\"string\""
        jsonObject.man.someArray2[3] == 12.0
        jsonObject.man.someArray[4] == "\"{\"field\":\"fieldValue\"}\""
        jsonObject.man.someArray2[5] == null
        jsonObject.man.someArray2[7] == "normal text"
        jsonObject.man.someArray2[8].field == "fieldValue"
    }

    def "when given simple array as text then expected array in json with expected values"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(new PrimitiveArrayJsonTypeResolver(), new BooleanJsonTypeResolver())
        String json = converter.convertPropertiesFromFileToJson("src/test/resources/resolvers.properties")
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.man.someArray[0] == true
        jsonObject.man.someArray[1] == false
        jsonObject.man.someArray[2] == "\"string\""
        jsonObject.man.someArray[3] == "12.0"
        jsonObject.man.someArray[4] == "\"{\"field\":\"fieldValue\"}\""
        jsonObject.man.someArray[5] == null
        jsonObject.man.someArray[6] == null
        jsonObject.man.someArray[7] == "normal text"
        jsonObject.man.someArray[8] == "{\"field\":\"fieldValue\"}"

        jsonObject.man.someArray2[0] == true
        jsonObject.man.someArray2[1] == false
        jsonObject.man.someArray2[2] == "\"string\""
        jsonObject.man.someArray2[3] == "12.0"
        jsonObject.man.someArray[4] == "\"{\"field\":\"fieldValue\"}\""
        jsonObject.man.someArray2[5] == null
        jsonObject.man.someArray2[6] == null
        jsonObject.man.someArray2[7] == "normal text"
        jsonObject.man.someArray2[8] == "{\"field\":\"fieldValue\"}"
    }

    def "when given simple array then expected parse error"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(new PrimitiveArrayJsonTypeResolver(), new BooleanJsonTypeResolver())
        Properties properties = new Properties()
        properties.put("man.someArray", [true, false, "string", 12.0, "{\"field\":\"fieldValue\"}"])
        String json = converter.convertToJson(properties)
        println(json)
        then:
        Exception ex = thrown()
        ex.message == "Cannot find valid JSON type resolver for class: 'class java.math.BigDecimal'. \n" +
                "Please consider add sufficient resolver o your resolvers."
    }


    private Properties createProperties() {
        Properties properties = new Properties()
        properties.put("man.stringNumber", "123")
        properties.put("man.doubleValue", 1.132)
        properties.put("other.doubleValueString", "12.122")
        return properties
    }

    def "when given is Map<String, String> with established string with number value will be converted as number"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        String json = converter.convertToJson(createStringValueMap())
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.man.stringNumber == 123
        jsonObject.man.doubleValue == 1.132
        jsonObject.man.text == "text"
        jsonObject.man.array[0] == "text1"
        jsonObject.man.array[1] == ""
        jsonObject.man.array[2] == 44
        jsonObject.man.array[3] == 15.0
        jsonObject.man.array[4] == true
        jsonObject.man.array[5] == false
        jsonObject.man.array[6] == null
        jsonObject.man.objectArray[0] == "text1"
        jsonObject.man.objectArray[1] == ""
        jsonObject.man.objectArray[2] == 44
        jsonObject.man.objectArray[3] == 15.0
        jsonObject.man.objectArray[4] == true
        jsonObject.man.objectArray[5] == false
        jsonObject.man.objectArray[6] == null
        jsonObject.other.doubleValueString == 12.122
    }

    private Map<String, String> createStringValueMap() {
        Map<String, String> map = new HashMap<>()
        map.put("man.stringNumber", "123")
        map.put("man.doubleValue", "1.132")
        map.put("man.text", "text")
        map.put("man.array", "text1,  ,44 , 15.0, true, FALSE, null")
        map.put("man.objectArray", "[text1, \"\" ,44 , 15.0, true, FALSE, null]")
        map.put("other.doubleValueString", "12.122")
        return map
    }

    def "when Properties has Raw Pojo object will be converted to json too"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        Properties properties = createExtendedProperties()
        String json = converter.convertToJson(properties)
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.man.stringNumber == "123"
        jsonObject.man.doubleValue == 1.132
        jsonObject.other.doubleValueString == "12.122"
        jsonObject.man.pojoObject.value1 == "test1"
        jsonObject.man.pojoObject.value2 == 12.3
    }

    def "when Properties has Raw array will convert to array"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        Properties properties = new Properties()
        Object[] objects = [12, 123, "test", [12, 14]]
        Integer[] integers = [12, 123]
        String[] strings = ["test", "test2"]
        properties.put("man.array", objects)
        properties.put("man.integers", integers)
        properties.put("man.strings", strings)
        String json = converter.convertToJson(properties)
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.man.array == [12, 123, "test", [12, 14]]
        jsonObject.man.integers == [12, 123]
        jsonObject.man.strings == ["test", "test2"]
    }

    def "given only ObjectFromTextJsonTypeResolver and BooleanJsonTypeResolver convert from Properties then expected will convert simple value to string or boolean"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
                new ObjectFromTextJsonTypeResolver(),
                new BooleanJsonTypeResolver()
        )

        Properties properties = new Properties()
        properties.put("object.number", 12)
        properties.put("object.boolean", true)
        properties.put("object.booleanAsText", "true")
        properties.put("object.array", [12, true, "test"])
        properties.put("object.text", "value")

        String json = converter.convertToJson(properties)
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.object.number == "12"
        jsonObject.object.boolean == true
        jsonObject.object.booleanAsText == "true"
        jsonObject.object.array == [12, true, "test"]
        jsonObject.object.text == "value"
    }

    def "given only ObjectFromTextJsonTypeResolver and BooleanJsonTypeResolver convert from Map<String, Object> then expected will convert simple value to string or boolean"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
                new ObjectFromTextJsonTypeResolver(),
                new BooleanJsonTypeResolver()
        )

        Map<String, Object> properties = new HashMap<>()
        properties.put("object.number", 12)
        properties.put("object.boolean", true)
        properties.put("object.booleanAsText", "true")
        properties.put("object.array", [12, true, "test"])
        properties.put("object.text", "value")

        String json = converter.convertFromValuesAsObjectMap(properties)
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.object.number == "12"
        jsonObject.object.boolean == true
        jsonObject.object.booleanAsText == "true"
        jsonObject.object.array == [12, true, "test"]
        jsonObject.object.text == "value"
    }

    def "given only ObjectFromTextJsonTypeResolver and BooleanJsonTypeResolver convert from Map<String, String> then expected will convert simple value to string or boolean"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
                new ObjectFromTextJsonTypeResolver(),
                new BooleanJsonTypeResolver()
        )

        Map<String, String> properties = new HashMap<>()
        properties.put("object.number", "12")
        properties.put("object.boolean", "true")
        properties.put("object.booleanAsText", "true")
        properties.put("object.arrayInvalid1", "[12, true, test]")
        properties.put("object.arrayInvalid2", "12, true, test")
        properties.put("object.array", "[12, true, \"test\"]")
        properties.put("object.text", "value")

        String json = converter.convertToJson(properties)
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.object.number == "12"
        jsonObject.object.boolean == true
        jsonObject.object.booleanAsText == true
        jsonObject.object.arrayInvalid1 == [12, true, "test"]
        jsonObject.object.arrayInvalid2 == "12, true, test"
        jsonObject.object.array == [12, true, "test"]
        jsonObject.object.text == "value"
    }

    private Properties createExtendedProperties() {
        Properties properties = createProperties()
        properties.put("man.pojoObject", new PojoObject("test1", "12.3"))
        return properties
    }

    private static class PojoObject {
        PojoObject(String value1, String value2) {
            this.value1 = value1
            this.value2 = new BigDecimal(value2)
        }
        private String value1
        private BigDecimal value2
    }

    def "when don't have provided resolver for raw objects"() {
        given:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
                new NumberJsonTypeResolver(),
                new BooleanJsonTypeResolver()
        )
        Properties properties = createExtendedProperties()
        when:
        converter.convertToJson(properties)
        then:
        ParsePropertiesException ex = thrown()
        ex.message == String.format(ParsePropertiesException.CANNOT_FIND_TYPE_RESOLVER_MSG, PojoObject.class)
    }
}