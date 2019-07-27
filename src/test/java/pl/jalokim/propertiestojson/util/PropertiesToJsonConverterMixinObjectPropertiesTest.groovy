package pl.jalokim.propertiestojson.util

import groovy.json.JsonSlurper
import spock.lang.Specification

class PropertiesToJsonConverterMixinObjectPropertiesTest extends Specification {

    def "json has empty string for empty property and null value for null value for property"() {
        // TODO expected that will not mixin object types. will show error message about that.
        // two tests with different order

        // man.address.city=New York
        // nan.address={"barCode":"01-103", "street": "12"}

        // second one
        // nan.address={"barCode":"01-103", "street": "12"}
        // man.address.city=New York

        // test without use of file

        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        String json = converter.convertPropertiesFromFileToJson("src/test/resources/mixin_object.properties")
        println(json)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.complexObject.nullValue == null
        jsonObject.complexObject.empty == ""
    }
}
