package pl.jalokim.propertiestojson.util

import groovy.json.JsonSlurper
import pl.jalokim.propertiestojson.helper.PropertyKeysOrderResolverForTest
import spock.lang.Specification

import static pl.jalokim.propertiestojson.util.PropertiesToJsonParsePropertiesExceptionTest.setUpMockPickupKeysOrder

class PropertiesToJsonConverterMixinObjectPropertiesTest extends Specification {

    private static final String CITY_VALUE = "New York"
    private static final String MAN_ADDRESS_PATH = "man.address"
    private static final String MAN_ADDRESS_CITY_PATH = "man.address.city"

    def jsonSlurper = new JsonSlurper()

    def "can merge json object with json object from text(from primitive value)"() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        PropertyKeysOrderResolverForTest propertiesResolver = new PropertyKeysOrderResolverForTest()
        propertiesResolver.setUpMockKeys(MAN_ADDRESS_CITY_PATH, MAN_ADDRESS_PATH)
        converter.setPropertyKeysOrderResolver(propertiesResolver)
        String json = converter.convertToJson(getProperties())
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.man.address.city == CITY_VALUE
        jsonObject.man.address.barCode == "01-103"
        jsonObject.man.address.street == "12"

    }

    def throwWhenCannotOverrideArrayElementByObjectType() {
        given:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        setUpMockPickupKeysOrder(converter,
                "some.someArray[0].someObject.someArray[4]",
                "some.someArray[0].someObject.someArray[3]",
                "some.someArray[0].field",
                "some.someArray[0]",
                "some.someArray[0].someObject",
                "some.someArray[0].objectField",
                "some.someArray[0].someObject.anotherField"
        )

        Map<String, String> properties = new HashMap<>()
        properties.put("some.someArray[0].field", "simpleText")
        properties.put("some.someArray[0]", "{\"objectField\": {\"nextField\": 2},\n\"someSimpleField\": \"text\"\n}")
        properties.put("some.someArray[0].objectField", "{\"nextField_3\": 3,\n  \"someSimpleField_T\": true}")
        properties.put("some.someArray[0].someObject.anotherField", "{\"someField\": 2.2}")
        properties.put("some.someArray[0].someObject.someArray[3]", "{\"field\": true}")
        properties.put("some.someArray[0].someObject.someArray[4]", "{\"text\": \"test_*8\"}")
        properties.put("some.someArray[0].someObject", "{\"someArray\": [true, 12], \"nextSimpField\": 12.123}")

        when:
        String json = converter.convertToJson(properties)
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        def someElement = jsonObject.some.someArray[0]
        someElement.field == "simpleText"
        someElement.objectField.nextField == 2
        someElement.objectField.nextField_3 == 3
        someElement.objectField.someSimpleField_T == true
        someElement.someSimpleField == "text"

        someElement.someObject.anotherField.someField == 2.2
        someElement.someObject.someArray[0] == true
        someElement.someObject.someArray[1] == 12
        someElement.someObject.someArray[2] == null
        someElement.someObject.someArray[3].field == true
        someElement.someObject.someArray[4].text == "test_*8"
        someElement.someObject.nextSimpField == 12.123
    }

    def "can merge json object from text(from primitive value) with json object "() {
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        PropertyKeysOrderResolverForTest propertiesResolver = new PropertyKeysOrderResolverForTest()
        propertiesResolver.setUpMockKeys(MAN_ADDRESS_PATH, MAN_ADDRESS_CITY_PATH)
        converter.setPropertyKeysOrderResolver(propertiesResolver)
        String json = converter.convertToJson(getProperties())
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.man.address.city == CITY_VALUE
        jsonObject.man.address.barCode == "01-103"
        jsonObject.man.address.street == "12"
    }

    private Map<String, String> getProperties() {
        Map<String, String> map = new HashMap<>()
        map.put(MAN_ADDRESS_CITY_PATH, CITY_VALUE)
        map.put(MAN_ADDRESS_PATH, "{\"barCode\":\"01-103\", \"street\": \"12\"}")
        map
    }
}
