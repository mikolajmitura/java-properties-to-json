package pl.jalokim.propertiestojson.util

import groovy.json.JsonSlurper
import pl.jalokim.propertiestojson.helper.PropertyKeysOrderResolverForTest
import spock.lang.Specification

class PropertiesToJsonConverterMixinObjectPropertiesTest extends Specification {

    private static final String CITY_VALUE = "New York"
    private static final String MAN_ADDRESS_PATH = "man.address"
    private static final String MAN_ADDRESS_CITY_PATH = "man.address.city"

    def jsonSlurper = new JsonSlurper()

    // TODO to impl
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
        jsonObject.man.address.city==CITY_VALUE
        jsonObject.man.address.barCode=="01-103"
        jsonObject.man.address.street=="12"

    }

    // TODO to impl
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
        jsonObject.man.address.city==CITY_VALUE
        jsonObject.man.address.barCode=="01-103"
        jsonObject.man.address.street=="12"
    }

    private Map<String, String> getProperties() {
        Map<String, String> map = new HashMap<>()
        map.put(MAN_ADDRESS_CITY_PATH, CITY_VALUE)
        map.put(MAN_ADDRESS_PATH, "{\"barCode\":\"01-103\", \"street\": \"12\"}")
        map
    }
}
