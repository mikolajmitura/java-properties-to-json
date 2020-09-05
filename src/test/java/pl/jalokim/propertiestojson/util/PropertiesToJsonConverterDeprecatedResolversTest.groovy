package pl.jalokim.propertiestojson.util

import static pl.jalokim.propertiestojson.object.JsonNullReferenceType.NULL_OBJECT

import groovy.json.JsonSlurper
import pl.jalokim.propertiestojson.object.AbstractJsonType
import pl.jalokim.propertiestojson.object.ArrayJsonType
import pl.jalokim.propertiestojson.object.BooleanJsonType
import pl.jalokim.propertiestojson.object.JsonNullReferenceType
import pl.jalokim.propertiestojson.object.ObjectJsonType
import pl.jalokim.propertiestojson.object.StringJsonType
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver
import pl.jalokim.propertiestojson.resolvers.primitives.BooleanJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.CharacterJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.EmptyStringJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.JsonNullReferenceTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.NumberJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.ObjectFromTextJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveArrayJsonTypeResolver
import pl.jalokim.propertiestojson.resolvers.primitives.StringJsonTypeResolver
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException
import spock.lang.Specification

class PropertiesToJsonConverterDeprecatedResolversTest extends Specification {

    def jsonMapper = new JsonSlurper()

    def "extended BooleanJsonTypeResolver"() {
        given:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
            new ExtendedBooleanJsonTypeResolver()
        )
        Map<String, String> mapString = new HashMap<>()
        mapString.put("object.boolAsText", "true")
        mapString.put("object.boolType", "true")
        mapString.put("object.alwaysFalse", "true")
        when:
        def json = converter.convertToJson(mapString)
        def jsonObject = jsonMapper.parseText(json)
        then:
        jsonObject.object.boolAsText == "true"
        jsonObject.object.boolType == true
        jsonObject.object.alwaysFalse == false
    }

    private class ExtendedBooleanJsonTypeResolver extends BooleanJsonTypeResolver {
        @Override
        Optional<Boolean> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            String propertyValue,
            String propertyKey) {
            if (propertyKey == "object.boolAsText") {
                return Optional.empty()
            }
            return super.returnConcreteValueWhenCanBeResolved(primitiveJsonTypesResolver, propertyValue, propertyKey);
        }

        @Override
        AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            Boolean propertyValue,
            String propertyKey) {
            if (propertyKey.contains("alwaysFalse")) {
                return new BooleanJsonType(false)
            }
            return new BooleanJsonType(propertyValue)
        }
    }

    def "extended CharacterJsonTypeResolver"() {
        given:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
            new ExtendedCharacterJsonTypeResolver()
        )
        Map<String, String> mapString = new HashMap<>()
        mapString.put("object.alwaysAsC", "_")
        mapString.put("object.asTrueStringFromT", "t")
        mapString.put("object.normalChar", "d")
        when:
        def json = converter.convertToJson(mapString)
        def jsonObject = jsonMapper.parseText(json)
        then:
        jsonObject.object.alwaysAsC == "c"
        jsonObject.object.asTrueStringFromT == "true"
        jsonObject.object.normalChar == "d"
    }

    private class ExtendedCharacterJsonTypeResolver extends CharacterJsonTypeResolver {
        @Override
        Optional<Character> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            String propertyValue,
            String propertyKey) {
            if (propertyKey == "object.alwaysAsC") {
                Character someChar = 'c'
                return Optional.of(someChar)
            }
            return super.returnConcreteValueWhenCanBeResolved(primitiveJsonTypesResolver, propertyValue, propertyKey)
        }

        @Override
        AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            Character propertyValue,
            String propertyKey) {
            if (propertyKey == "object.asTrueStringFromT") {
                return new StringJsonType("true")
            }
            return super.returnConcreteJsonType(primitiveJsonTypesResolver, propertyValue, propertyKey)
        }
    }

    def "extended EmptyStringJsonTypeResolver"() {
        given:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
            new ExtendedEmptyStringJsonTypeResolver()
        )
        Map<String, String> mapString = new HashMap<>()
        mapString.put("object.asEmpty", "_")
        mapString.put("object.secret", "some_secret")
        mapString.put("object.normalEmpty", "")
        when:
        def json = converter.convertToJson(mapString)
        def jsonObject = jsonMapper.parseText(json)
        then:
        jsonObject.object.asEmpty == "###:_empty_"
        jsonObject.object.secret == "###:*****"
        jsonObject.object.normalEmpty == "###:"
    }

    private class ExtendedEmptyStringJsonTypeResolver extends EmptyStringJsonTypeResolver {
        @Override
        Optional<String> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            String propertyValue,
            String propertyKey) {
            if (propertyKey == "object.asEmpty") {
                return Optional.of("_empty_")
            }
            if (propertyKey == "object.secret") {
                return Optional.of("*****")
            }
            return super.returnConcreteValueWhenCanBeResolved(primitiveJsonTypesResolver, propertyValue, propertyKey)
        }

        @Override
        AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            String propertyValue,
            String propertyKey) {
            return new StringJsonType("###:" + propertyValue)
        }
    }

    def "extended JsonNullReferenceTypeResolver"() {
        given:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
            new ExtendedJsonNullReferenceTypeResolver()
        )
        Map<String, String> mapString = new HashMap<>()
        mapString.put("object.undefinedAsText", "undefined")
        mapString.put("object.undefinedAsNull", "undefined")
        mapString.put("object.normalNull", "null")
        mapString.put("object.nullAsText", "null")
        when:
        def json = converter.convertToJson(mapString)
        then:
        ParsePropertiesException exception = thrown()
        exception.message.contains('for typeclass pl.jalokim.propertiestojson.object.JsonNullReferenceType expected only one!')
    }

    private class ExtendedJsonNullReferenceTypeResolver extends JsonNullReferenceTypeResolver {
        @Override
        Optional<JsonNullReferenceType> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            String propertyValue,
            String propertyKey) {
            if (propertyKey == "object.undefinedAsNull") {
                return Optional.of(NULL_OBJECT)
            }

            if (propertyKey == "object.nullAsText") {
                return Optional.empty()
            }
            return super.returnConcreteValueWhenCanBeResolved(primitiveJsonTypesResolver, propertyValue, propertyKey)
        }

        @Override
        AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            JsonNullReferenceType propertyValue,
            String propertyKey) {
            if (propertyKey == "object.asTrueStringFromT") {
                return new StringJsonType("true")
            }
            return super.returnConcreteJsonType(primitiveJsonTypesResolver, propertyValue, propertyKey)
        }
    }

    def "extended NumberJsonTypeResolver"() {
        given:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
            new ExtendedNumberJsonTypeResolver()
        )
        Map<String, String> mapString = new HashMap<>()
        mapString.put("object.alwaysZero", "10.0")
        mapString.put("object.numberAsText", "10.01")
        mapString.put("object.normalNumber", "10.0")
        when:
        def json = converter.convertToJson(mapString)
        def jsonObject = jsonMapper.parseText(json)
        then:
        jsonObject.object.alwaysZero == 0.0
        jsonObject.object.numberAsText == "10.01"
        jsonObject.object.normalNumber == 10.0
    }

    private class ExtendedNumberJsonTypeResolver extends NumberJsonTypeResolver {
        @Override
        Optional<Number> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            String propertyValue,
            String propertyKey) {
            if (propertyKey == "object.alwaysZero") {
                return Optional.of(BigDecimal.ZERO)
            }
            return super.returnConcreteValueWhenCanBeResolved(primitiveJsonTypesResolver, propertyValue, propertyKey)
        }

        @Override
        AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            Number propertyValue,
            String propertyKey) {
            if (propertyKey == "object.numberAsText") {
                return new StringJsonType(propertyValue.toString())
            }
            return super.returnConcreteJsonType(primitiveJsonTypesResolver, propertyValue, propertyKey)
        }
    }

    def "extended ObjectFromTextJsonTypeResolver"() {
        given:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
            new ExtendedObjectFromTextJsonTypeResolver()
        )
        Map<String, String> mapString = new HashMap<>()
        mapString.put("object.asText", "{\"field\":12}")
        mapString.put("object.object", "{\"field\":11}")
        mapString.put("object.objectWrapper", "text_value")
        when:
        def json = converter.convertToJson(mapString)
        def jsonObject = jsonMapper.parseText(json)
        then:
        jsonObject.object.asText == "{\"field\":12}"
        jsonObject.object.object.field == 11
        jsonObject.object.objectWrapper.value == "text_value"
    }

    private class ExtendedObjectFromTextJsonTypeResolver extends ObjectFromTextJsonTypeResolver {
        @Override
        Optional<Object> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            String propertyValue,
            String propertyKey) {
            if (propertyKey == "object.objectWrapper") {
                ObjectJsonType objectJsonType = new ObjectJsonType()
                StringJsonType numberJsonType = new StringJsonType(propertyValue)
                objectJsonType.addField("value", numberJsonType, null)
                return Optional.of(objectJsonType)
            }
            return super.returnConcreteValueWhenCanBeResolved(primitiveJsonTypesResolver, propertyValue, propertyKey)
        }

        @Override
        AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            Object propertyValue,
            String propertyKey) {
            if (propertyKey == "object.asText") {
                return new StringJsonType(propertyValue.toString())
            }
            return super.returnConcreteJsonType(primitiveJsonTypesResolver, propertyValue, propertyKey)
        }
    }

    def "extended PrimitiveArrayJsonTypeResolver"() {
        given:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
            new ExtendedPrimitiveArrayJsonTypeResolver(),
            new NumberJsonTypeResolver()
        )
        Map<String, String> mapString = new HashMap<>()
        mapString.put("object.asText", "1, 2, 3, text")
        mapString.put("object.asNormalList", "1, 2, 3, text")
        mapString.put("object.onlyFirst3", "1, 2, 3, 4, 5, 6")
        when:
        def json = converter.convertToJson(mapString)
        def jsonObject = jsonMapper.parseText(json)
        then:
        jsonObject.object.asText == "1, 2, 3, text"
        jsonObject.object.asNormalList == [1, 2, 3, "text"]
        jsonObject.object.onlyFirst3 == [1, 2, 3]
    }

    private class ExtendedPrimitiveArrayJsonTypeResolver extends PrimitiveArrayJsonTypeResolver {
        @Override
        Optional<Collection<?>> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            String propertyValue,
            String propertyKey) {
            if (propertyKey == "object.asText") {
                return Optional.of(Arrays.asList(propertyValue))
            }
            return super.returnConcreteValueWhenCanBeResolved(primitiveJsonTypesResolver, propertyValue, propertyKey)
        }

        @Override
        AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            Collection<?> propertyValue,
            String propertyKey) {
            if (propertyKey == "object.onlyFirst3") {
                def list = new ArrayList<>(propertyValue)
                Collection<?> elements = list.subList(0, 3)
                return new ArrayJsonType(primitiveJsonTypesResolver, elements, null, propertyKey)
            }
            if (propertyKey == "object.asText") {
                String text = new ArrayList<>(propertyValue).get(0)
                return new StringJsonType(text)
            }
            return super.returnConcreteJsonType(primitiveJsonTypesResolver, propertyValue, propertyKey)
        }
    }

    def "extended StringJsonTypeResolver"() {
        given:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
            new ExtendedStringJsonTypeResolver()
        )
        Map<String, String> mapString = new HashMap<>()
        mapString.put("object.normalText", " #text text ")
        mapString.put("object.removeSpaces", " # remove spaces ")
        when:
        def json = converter.convertToJson(mapString)
        def jsonObject = jsonMapper.parseText(json)
        then:
        jsonObject.object.normalText == "###:#text text"
        jsonObject.object.removeSpaces == "###:#_remove_spaces"
    }

    private class ExtendedStringJsonTypeResolver extends StringJsonTypeResolver {
        @Override
        Optional<String> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            String propertyValue,
            String propertyKey) {
            if (propertyKey == "object.removeSpaces") {
                return Optional.of(propertyValue.replace(" ", "_"))
            }
            return super.returnConcreteValueWhenCanBeResolved(primitiveJsonTypesResolver, propertyValue, propertyKey)
        }

        @Override
        AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            String propertyValue,
            String propertyKey) {
            return new StringJsonType("###:" + propertyValue)
        }
    }
}