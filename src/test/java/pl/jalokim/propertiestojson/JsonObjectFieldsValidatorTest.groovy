package pl.jalokim.propertiestojson

import pl.jalokim.propertiestojson.object.AbstractJsonType
import pl.jalokim.propertiestojson.object.ArrayJsonType
import pl.jalokim.propertiestojson.object.JsonNullReferenceType
import pl.jalokim.propertiestojson.object.NumberJsonType
import pl.jalokim.propertiestojson.object.ObjectJsonType
import pl.jalokim.propertiestojson.object.StringJsonType
import spock.lang.Specification

import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.isArrayJson
import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.isObjectJson
import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.isPrimitiveValue

class JsonObjectFieldsValidatorTest extends Specification {

    def "is type of json object"(AbstractJsonType someObject, boolean expectedResult) {
        when:
        boolean result = isObjectJson(someObject)
        then:
        result == expectedResult
        where:
        someObject                 | expectedResult
        new ObjectJsonType()       | true
        new StringJsonType("test") | false
        new ArrayJsonType()        | false
        new OwnObjectJsonType()    | true
        new JsonNullReferenceType()    | false
    }

    def "is type of json array"(AbstractJsonType someObject, boolean expectedResult) {
        when:
        boolean result = isArrayJson(someObject)
        then:
        result == expectedResult
        where:
        someObject                 | expectedResult
        new ObjectJsonType()       | false
        new StringJsonType("test") | false
        new ArrayJsonType()        | true
        new OwnObjectJsonType()    | false
        new OwnArrayJsonType()     | true
        new JsonNullReferenceType()     | false
    }

    def "is type of primitive value"(AbstractJsonType someObject, boolean expectedResult) {
        when:
        boolean result = isPrimitiveValue(someObject)
        then:
        result == expectedResult
        where:
        someObject                  | expectedResult
        new ObjectJsonType()        | false
        new StringJsonType("test")  | true
        new NumberJsonType(12)      | true
        new ArrayJsonType()         | false
        new OwnObjectJsonType()     | false
        new OwnArrayJsonType()      | false
        new JsonNullReferenceType() | true
    }

    private static class OwnObjectJsonType extends ObjectJsonType {

    }

    private static class OwnArrayJsonType extends ArrayJsonType {

    }
}
