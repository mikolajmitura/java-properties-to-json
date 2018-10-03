package pl.jalokim.propertiestojson.util

import groovy.json.JsonSlurper
import pl.jalokim.propertiestojson.resolvers.primitives.NumberJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.ObjectFromTextJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveArrayJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.StringJsonTypeResolver
import spock.lang.Specification

import java.beans.Transient

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
                new NumberJsonTypeResolver(), new StringJsonTypeResolver())
        String json = converter.parsePropertiesFromFileToJson("src/test/resources/arrayCombinations.properties", "arraytexts")
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

    def "when properties with established string with number value will be converted as string not number"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        Properties properties = createProperties()
        String json = converter.parseToJson(properties)
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.man.stringNumber == "123"
        jsonObject.man.doubleValue == 1.132
        jsonObject.other.doubleValueString == "12.122"
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
        String json = converter.parseToJson(createStringValueMap())
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
        jsonObject.other.doubleValueString == 12.122
    }

    private Map<String, String> createStringValueMap() {
        Map<String, String> map = new HashMap<>()
        map.put("man.stringNumber", "123")
        map.put("man.doubleValue", "1.132")
        map.put("man.text", "text")
        map.put("man.array", "text1,  ,44 , 15.0, true, FALSE, null")
        map.put("other.doubleValueString", "12.122")
        return map
    }

    def "when Properties has Raw Pojo object will be converted to json too"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        Properties properties = createExtendedProperties()
        String json = converter.parseToJson(properties)
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.man.stringNumber == "123"
        jsonObject.man.doubleValue == 1.132
        jsonObject.other.doubleValueString == "12.122"
        jsonObject.man.pojoObject.value1 == "test1"
        jsonObject.man.pojoObject.value2 == 12.3
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
}