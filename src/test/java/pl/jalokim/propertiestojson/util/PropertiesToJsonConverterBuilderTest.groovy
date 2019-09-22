package pl.jalokim.propertiestojson.util

import groovy.json.JsonSlurper
import pl.jalokim.propertiestojson.object.AbstractJsonType
import pl.jalokim.propertiestojson.object.JsonNullReferenceType
import pl.jalokim.propertiestojson.object.SkipJsonField
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver
import pl.jalokim.propertiestojson.resolvers.primitives.object.NullToJsonTypeConverter
import pl.jalokim.propertiestojson.resolvers.primitives.object.NumberToJsonTypeConverter
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToEmptyStringResolver
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToJsonNullReferenceResolver
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToNumberResolver
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException
import spock.lang.Specification

import static java.lang.Boolean.FALSE
import static pl.jalokim.propertiestojson.object.JsonNullReferenceType.NULL_OBJECT

class PropertiesToJsonConverterBuilderTest extends Specification {

    def "default builder contains all defaults resolvers and converters, will not skip nulls test for 2 steps"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder().build()
        Map<String, String> properties = new HashMap<>()
        properties.put("someObject.number", "123")
        properties.put("someObject.bool", "true")
        properties.put("someObject.text", "text")
        properties.put("someObject.emptyText", "")
        properties.put("someObject.nullField", "null")
        properties.put("someObject.simpleArray", "text, 10.0, FALSE, test")
        properties.put("someObject.object", "{\"name\": \"John\", \"surname\": \"Doe\"}")

        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.someObject.number == 123
        jsonObject.someObject.bool == true
        jsonObject.someObject.text == "text"
        jsonObject.someObject.emptyText == ""
        jsonObject.someObject.nullField == null
        JsonCheckerUtil.leafOfPathHasNullValue("someObject.nullField", json)
        jsonObject.someObject.simpleArray == ["text", 10.0, false, "test"]
        jsonObject.someObject.object.name == "John"
        jsonObject.someObject.object.surname == "Doe"
    }

    def "default builder contains all defaults resolvers and converters, will not skip nulls test for 1 step"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder().build()
        Map<String, Object> properties = new HashMap<>()
        properties.put("someObject.number", 123)
        properties.put("someObject.bool", true)
        properties.put("someObject.text", "text")
        properties.put("someObject.emptyText", "")
        properties.put("someObject.nullField", null)
        properties.put("someObject.simpleArray", ["text", 10.0, FALSE, "test"])
        properties.put("someObject.object", [name: 'John', surname: 'Doe'])

        String json = converter.convertFromValuesAsObjectMap(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.someObject.number == 123
        jsonObject.someObject.bool == true
        jsonObject.someObject.text == "text"
        jsonObject.someObject.emptyText == ""
        jsonObject.someObject.nullField == null
        JsonCheckerUtil.leafOfPathHasNullValue("someObject.nullField", json)
        jsonObject.someObject.simpleArray == ["text", 10.0, false, "test"]
        jsonObject.someObject.object.name == "John"
        jsonObject.someObject.object.surname == "Doe"
    }

    // for skip null
    def "default builder contains all defaults resolvers and converters, will skip nulls test for 2 steps"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder()
                .skipNulls()
                .build()

        Map<String, String> properties = new HashMap<>()
        properties.put("someObject.number", "123")
        properties.put("someObject.bool", "true")
        properties.put("someObject.text", "text")
        properties.put("someObject.emptyText", "")
        properties.put("someObject.nullField", "null")
        properties.put("someObject.nullField2", "null")
        properties.put("someObject.simpleArray", "text, 10.0, FALSE, test")
        properties.put("someObject.object", "{\"name\": \"John\", \"surname\": \"Doe\"}")

        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.someObject.number == 123
        jsonObject.someObject.bool == true
        jsonObject.someObject.text == "text"
        jsonObject.someObject.emptyText == ""
        JsonCheckerUtil.leafOfPathIsNotPresent("someObject.nullField", json)
        JsonCheckerUtil.leafOfPathIsNotPresent("someObject.nullField2", json)
        jsonObject.someObject.simpleArray == ["text", 10.0, false, "test"]
        jsonObject.someObject.object.name == "John"
        jsonObject.someObject.object.surname == "Doe"
    }

    def "default builder contains all defaults resolvers and converters, will skip nulls test for 1 step"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder()
                .skipNulls()
                .build()

        Map<String, Object> properties = new HashMap<>()
        properties.put("someObject.number", 123)
        properties.put("someObject.bool", true)
        properties.put("someObject.text", "text")
        properties.put("someObject.emptyText", "")
        properties.put("someObject.nullField", null)
        properties.put("someObject.nullField2", null)
        properties.put("someObject.simpleArray", ["text", 10.0, FALSE, "test"])
        properties.put("someObject.object", [name: 'John', surname: 'Doe'])

        String json = converter.convertFromValuesAsObjectMap(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.someObject.number == 123
        jsonObject.someObject.bool == true
        jsonObject.someObject.text == "text"
        jsonObject.someObject.emptyText == ""
        jsonObject.someObject.nullField == null
        JsonCheckerUtil.leafOfPathIsNotPresent("someObject.nullField", json)
        JsonCheckerUtil.leafOfPathIsNotPresent("someObject.nullField2", json)
        jsonObject.someObject.simpleArray == ["text", 10.0, false, "test"]
        jsonObject.someObject.object.name == "John"
        jsonObject.someObject.object.surname == "Doe"
    }

    // tests for added converters and resolvers to defaults 2 steps
    def "default builder contains all defaults resolvers and converters + added converters, 2 steps"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder()
                .defaultAndCustomObjectToJsonTypeConverters(new SkipableNumberJsonTypeConverter())
                .build()

        Map<String, String> properties = new HashMap<>()
        properties.put("someObject.number", "123")
        properties.put("someObject.skip.number", "123")
        properties.put("someObject.bool", "true")
        properties.put("someObject.text", "text")
        properties.put("someObject.emptyText", "")
        properties.put("someObject.nullField", "null")
        properties.put("someObject.simpleArray", "text, 10.0, FALSE, test")
        properties.put("someObject.object", "{\"name\": \"John\", \"surname\": \"Doe\"}")

        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.someObject.number == 123
        jsonObject.someObject.bool == true
        jsonObject.someObject.text == "text"
        jsonObject.someObject.emptyText == ""
        jsonObject.someObject.nullField == null
        JsonCheckerUtil.leafOfPathHasNullValue("someObject.nullField", json)
        JsonCheckerUtil.leafOfPathIsNotPresent("someObject.skip", json)
        jsonObject.someObject.simpleArray == ["text", 10.0, false, "test"]
        jsonObject.someObject.object.name == "John"
        jsonObject.someObject.object.surname == "Doe"
    }

    def "default builder contains all defaults resolvers and converters + added resolvers, 2 steps"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder()
                .defaultAndCustomTextToObjectResolvers(new NotTrimText())
                .build()

        Map<String, String> properties = new HashMap<>()
        properties.put("someObject.number", "123")
        properties.put("someObject.bool", "true")
        properties.put("someObject.boolAsText", "# true ")
        properties.put("someObject.text", "# text ")
        properties.put("someObject.emptyText", "")
        properties.put("someObject.nullField", "null")
        properties.put("someObject.simpleArray", "text, 10.0, FALSE, test")
        properties.put("someObject.object", "{\"name\": \"John\", \"surname\": \"Doe\"}")

        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.someObject.number == 123
        jsonObject.someObject.bool == true
        jsonObject.someObject.boolAsText == " true "
        jsonObject.someObject.text == " text "
        jsonObject.someObject.emptyText == ""
        jsonObject.someObject.nullField == null
        JsonCheckerUtil.leafOfPathHasNullValue("someObject.nullField", json)
        jsonObject.someObject.simpleArray == ["text", 10.0, false, "test"]
        jsonObject.someObject.object.name == "John"
        jsonObject.someObject.object.surname == "Doe"
    }

    def "default builder contains all defaults resolvers and converters + added resolvers + added converters, 2 steps"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder()
                .defaultAndCustomTextToObjectResolvers(new NotTrimText())
                .defaultAndCustomObjectToJsonTypeConverters(new SkipableNumberJsonTypeConverter())
                .build()

        Map<String, String> properties = new HashMap<>()
        properties.put("someObject.number", "123")
        properties.put("someObject.skip.number", "123")
        properties.put("someObject.bool", "true")
        properties.put("someObject.boolAsText", "# true ")
        properties.put("someObject.text", "# text ")
        properties.put("someObject.emptyText", "")
        properties.put("someObject.nullField", "null")
        properties.put("someObject.simpleArray", "text, 10.0, FALSE, test")
        properties.put("someObject.object", "{\"name\": \"John\", \"surname\": \"Doe\"}")

        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.someObject.number == 123
        jsonObject.someObject.bool == true
        jsonObject.someObject.boolAsText == " true "
        jsonObject.someObject.text == " text "
        jsonObject.someObject.emptyText == ""
        jsonObject.someObject.nullField == null
        JsonCheckerUtil.leafOfPathHasNullValue("someObject.nullField", json)
        JsonCheckerUtil.leafOfPathIsNotPresent("someObject.skip", json)
        jsonObject.someObject.simpleArray == ["text", 10.0, false, "test"]
        jsonObject.someObject.object.name == "John"
        jsonObject.someObject.object.surname == "Doe"
    }

    // tests for custom converters and resolvers without defaults 2 steps
    def "default builder without defaults, only added converters, 2 steps"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder()
                .onlyCustomObjectToJsonTypeConverters(new SkipableNumberJsonTypeConverter())
                .build()

        Map<String, String> properties = new HashMap<>()
        properties.put("someObject.skip.number", "123")
        properties.put("someObject.boolAsText", "# true ")
        properties.put("someObject.text", "# text ")
        properties.put("someObject.emptyText", "")
        properties.put("someObject.nullField", "null")

        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.someObject.boolAsText == "# true"
        jsonObject.someObject.text == "# text"
        jsonObject.someObject.emptyText == ""
        JsonCheckerUtil.leafOfPathHasNullValue("someObject.nullField", json)
        JsonCheckerUtil.leafOfPathIsNotPresent("someObject.skip", json)
    }

    def "default builder without defaults, only added converters, 2 steps -> throw exception - lack of converter"() {
        when:
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder()
                .onlyCustomObjectToJsonTypeConverters(new SkipableNumberJsonTypeConverter())
                .build()

        Map<String, String> properties = new HashMap<>()
        properties.put("someObject.skip.number", "123")
        properties.put("someObject.number", "123")

        converter.convertToJson(properties)
        then:
        ParsePropertiesException ex = thrown()
        ex.message == "Cannot find valid JSON type resolver for class: 'class java.math.BigInteger'. \n" +
                " for property: someObject.number, and object value: 123 \n" +
                "Please consider add sufficient resolver to your resolvers."
    }

    def "default builder without defaults, only added resolvers, 2 steps"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder()
                .onlyCustomTextToObjectResolvers(new NotTrimText(), new TextToNumberResolver())
                .onlyCustomObjectToJsonTypeConverters(new SkipableNumberJsonTypeConverter(), new NumberToJsonTypeConverter())
                .build()

        Map<String, String> properties = new HashMap<>()
        properties.put("someObject.number", "124")
        properties.put("someObject.skip.number", "123")
        properties.put("someObject.bool", "true")
        properties.put("someObject.boolAsText", "# true ")
        properties.put("someObject.text", "# text ")
        properties.put("someObject.emptyText", "")
        properties.put("someObject.nullField", "null")
        properties.put("someObject.simpleArray", "text, 10.0, FALSE, test")
        properties.put("someObject.object", "{\"name\": \"John\", \"surname\": \"Doe\"}")

        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.someObject.number == 124
        JsonCheckerUtil.leafOfPathIsNotPresent("someObject.skip", json)
        jsonObject.someObject.bool == "true"
        jsonObject.someObject.boolAsText == " true "
        jsonObject.someObject.text == " text "
        jsonObject.someObject.emptyText == ""
        jsonObject.someObject.nullField == null
        JsonCheckerUtil.leafOfPathHasNullValue("someObject.nullField", json)
        jsonObject.someObject.simpleArray == "text, 10.0, FALSE, test"
        jsonObject.someObject.object == "{\"name\": \"John\", \"surname\": \"Doe\"}"
    }

    /*
     tests for add converters only 1 step (convert from object to json)
    */

    def "default builder with defaults and added converters ony 1 step"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder()
                .defaultAndCustomObjectToJsonTypeConverters(new SkipableNumberJsonTypeConverter(), new NumberToJsonTypeConverter())
                .build()

        Map<String, Object> properties = new HashMap<>()
        properties.put("someObject.number", 124)
        properties.put("someObject.skip.number", 123)
        properties.put("someObject.bool", true)
        properties.put("someObject.boolAsText", "# true ")
        properties.put("someObject.text", "# text ")
        properties.put("someObject.emptyText", "")
        properties.put("someObject.nullField", null)
        properties.put("someObject.simpleArray", ["text", 10.0, FALSE, "test"])
        properties.put("someObject.object", [name: 'John', surname: 'Doe'])

        String json = converter.convertFromValuesAsObjectMap(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.someObject.number == 124
        JsonCheckerUtil.leafOfPathIsNotPresent("someObject.skip", json)
        jsonObject.someObject.bool == true
        jsonObject.someObject.boolAsText == "# true "
        jsonObject.someObject.text == "# text "
        jsonObject.someObject.emptyText == ""
        jsonObject.someObject.nullField == null
        JsonCheckerUtil.leafOfPathHasNullValue("someObject.nullField", json)
        jsonObject.someObject.simpleArray == ["text", 10.0, false, "test"]
        jsonObject.someObject.object.name == "John"
        jsonObject.someObject.object.surname == "Doe"
    }

    def "default builder without defaults and only added converters ony 1 step"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder()
                .onlyCustomObjectToJsonTypeConverters(new SkipableNumberJsonTypeConverter(), new NumberToJsonTypeConverter())
                .build()

        Map<String, Object> properties = new HashMap<>()
        properties.put("someObject.number", 124)
        properties.put("someObject.skip.number", 123)
        properties.put("someObject.boolAsText", "# true ")
        properties.put("someObject.text", "# text ")
        properties.put("someObject.emptyText", "")
        properties.put("someObject.nullField", null)

        String json = converter.convertFromValuesAsObjectMap(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.someObject.number == 124
        JsonCheckerUtil.leafOfPathIsNotPresent("someObject.skip", json)
        jsonObject.someObject.boolAsText == "# true "
        jsonObject.someObject.text == "# text "
        jsonObject.someObject.emptyText == ""
        jsonObject.someObject.nullField == null
        JsonCheckerUtil.leafOfPathHasNullValue("someObject.nullField", json)
    }

    // tests for override
    def "override NullToJsonTypeConverter"() {
        when:
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder()
                .overrideNullToJsonConverter(new OwnNullToJsonTypeConverter())
                .build()

        Map<String, Object> properties = new HashMap<>()
        properties.put("someObject.another", null)
        properties.put("someObject.null", null)
        properties.put("someObject.skip.null", null)
        properties.put("someObject.skip.value", "text")
        properties.put("someObject.other.skip.null.other", null)

        String json = converter.convertFromValuesAsObjectMap(properties)
        then:
        JsonCheckerUtil.leafOfPathHasNullValue("someObject.another", json)
        JsonCheckerUtil.leafOfPathHasNullValue("someObject.null", json)
        JsonCheckerUtil.leafOfPathIsNotPresent("someObject.skip.null", json)
        JsonCheckerUtil.leafOfPathIsNotPresent("someObject.other", json)
    }

    def "override TextToJsonNullReferenceResolver"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder()
                .overrideTextToJsonNullResolver(new OwnTextToJsonNullReferenceResolver())
                .build()

        Map<String, String> properties = new HashMap<>()
        properties.put("someObject.nullAsText", "null")
        properties.put("someObject.asTextToo", "null")
        properties.put("someObject.asNull", "undefined")
        properties.put("someObject.other", "text_1")

        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.someObject.nullAsText == "null"
        jsonObject.someObject.asTextToo == "null"
        JsonCheckerUtil.leafOfPathHasNullValue("someObject.asNull", json)
        jsonObject.someObject.other == "text_1"
    }

    def "override TextToEmptyStringResolver"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder()
                .overrideTextToEmptyStringResolver(new OwnTextToEmptyStringResolverr())
                .build()

        Map<String, String> properties = new HashMap<>()
        properties.put("someObject.emptyAsNull", "")
        properties.put("someObject.asNullToo", "  ")
        properties.put("someObject.asText", "undefined")

        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        JsonCheckerUtil.leafOfPathHasNullValue("someObject.emptyAsNull", json)
        JsonCheckerUtil.leafOfPathHasNullValue("someObject.asNullToo", json)
        jsonObject.someObject.asText == "undefined"
    }

    private static class SkipableNumberJsonTypeConverter extends NumberToJsonTypeConverter {
        @Override
        Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                            Number convertedValue,
                                                            String propertyKey) {
            if (propertyKey.contains("skip.number")) {
                return Optional.of(SkipJsonField.SKIP_JSON_FIELD)
            }
            return Optional.empty()
        }
    }

    private static class NotTrimText implements TextToConcreteObjectResolver {

        @Override
        Optional<Object> returnConvertedValueForClearedText(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
            return returnObjectWhenCanBeResolved(primitiveJsonTypesResolver, propertyValue == null ? null : propertyValue, propertyKey)
        }

        @Override
        Optional<Object> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                       String propertyValue,
                                                       String propertyKey) {
            if (propertyValue.contains("#")) {
                return Optional.ofNullable(propertyValue.replace("#", ""))
            }
            return Optional.empty()
        }
    }

    private static class OwnNullToJsonTypeConverter extends NullToJsonTypeConverter {
        @Override
        Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                                   JsonNullReferenceType convertedValue,
                                                                   String propertyKey) {
            if (propertyKey.contains("skip.null")) {
                return Optional.of(SkipJsonField.SKIP_JSON_FIELD)
            }
            return Optional.of(convertedValue)
        }
    }
    private static class OwnTextToJsonNullReferenceResolver extends TextToJsonNullReferenceResolver {

        @Override
        Optional<Object> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                       String propertyValue,
                                                       String propertyKey) {
            if (propertyValue == null || propertyValue.equals("undefined")) {
                return Optional.of(NULL_OBJECT)
            }
            return Optional.empty()
        }
    }

    private static class OwnTextToEmptyStringResolverr extends TextToEmptyStringResolver {

        @Override
        Optional<Object> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                       String propertyValue,
                                                       String propertyKey) {
            if (propertyValue == null || propertyValue.equals("")) {
                return Optional.of(NULL_OBJECT)
            }
            return Optional.empty()
        }
    }
}
