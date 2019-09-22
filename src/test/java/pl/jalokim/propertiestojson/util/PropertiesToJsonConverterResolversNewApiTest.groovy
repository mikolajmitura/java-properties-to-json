package pl.jalokim.propertiestojson.util


import groovy.json.JsonSlurper
import pl.jalokim.propertiestojson.object.*
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver
import pl.jalokim.propertiestojson.resolvers.primitives.object.AbstractObjectToJsonTypeConverter
import pl.jalokim.propertiestojson.resolvers.primitives.object.NumberToJsonTypeConverter
import pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class PropertiesToJsonConverterResolversNewApiTest extends Specification {

    def "all AbstractJsonType converted simply to json"() {
        def jsonSlurper = new JsonSlurper()
        when:
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter()
        Properties properties = new Properties()
        properties.put("some.object.numberAsText", new StringJsonType("12"))
        properties.put("some.object.number", new NumberJsonType(12))

        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.some.object.numberAsText == "12"
        jsonObject.some.object.number == 12
    }


    def "from one to object resolver create 3 types of bean, 3 converters for them"() {
        def jsonSlurper = new JsonSlurper()
        when:
        List<ObjectToJsonTypeConverter> toJsonResolvers = [new LocalDateTimeToJson(),
                                                           new LocalDateToJson(),
                                                           new LocalTimeToJson()]

        List<TextToConcreteObjectResolver> toObjectsResolvers = [new DatesOrTimeResolver()]

        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(toJsonResolvers, toObjectsResolvers)
        Map<String, String> properties = new HashMap<>()
        properties.put("some.object.datetime", "123")
        properties.put("some.object.date", "123")
        properties.put("some.object.time", "123")

        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.some.object.datetime == "2019-09-29T10:12:00"
        jsonObject.some.object.date == "2019-09-29"
        jsonObject.some.object.time == "10:12:00"
    }

    private static class LocalDateTimeToJson extends AbstractObjectToJsonTypeConverter<LocalDateTime> {

        @Override
        Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                            LocalDateTime convertedValue,
                                                            String propertyKey) {
            return Optional.of(new StringJsonType(convertedValue.format(DateTimeFormatter.ISO_DATE_TIME)))
        }
    }

    private static class LocalDateToJson extends AbstractObjectToJsonTypeConverter<LocalDate> {

        @Override
        Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                            LocalDate convertedValue,
                                                            String propertyKey) {
            return Optional.of(new StringJsonType(convertedValue.format(DateTimeFormatter.ISO_DATE)))
        }
    }

    private static class LocalTimeToJson extends AbstractObjectToJsonTypeConverter<LocalTime> {

        @Override
        Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                            LocalTime convertedValue,
                                                            String propertyKey) {
            return Optional.of(new StringJsonType(convertedValue.format(DateTimeFormatter.ISO_TIME)))
        }
    }

    private static class DatesOrTimeResolver implements TextToConcreteObjectResolver {

        @Override
        Optional<Object> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                       String propertyValue,
                                                       String propertyKey) {

            LocalTime localTime = LocalTime.of(10, 12)
            LocalDate localDate = LocalDate.of(2019, 9, 29)
            LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime)

            if (propertyKey.contains("datetime")) {
                return Optional.of(localDateTime)
            }
            if (propertyKey.contains("date")) {
                return Optional.of(localDate)
            }
            if (propertyKey.contains("time")) {
                return Optional.of(localTime)
            }
            return Optional.empty()
        }
    }

    def "additional number resolver"() {
        def jsonSlurper = new JsonSlurper()
        when:
        List<ObjectToJsonTypeConverter> toJsonResolvers = [new NumberToNullJsonTypeConverter(),
                                                           new NumberToStringJsonTypeConverter(),
                                                           new NumberToJsonTypeConverter()]

        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(toJsonResolvers, [])
        Properties properties = new Properties()
        properties.put("some.object.numberAsText", 12)
        properties.put("some.object.number1", 13)
        properties.put("some.object.numberAsText2", 14)
        properties.put("some.object.number3", 15)
        properties.put("some.object.null_", 15)

        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)
        then:
        jsonObject.some.object.numberAsText == "12"
        jsonObject.some.object.number1 == 13
        jsonObject.some.object.numberAsText2 == "14"
        jsonObject.some.object.number3 == 15
        jsonObject.some.object.null_ == null
        JsonCheckerUtil.leafOfPathHasNullValue("some.object.null_", json)
    }

    def "skipped field will not be added to json"() {
        def jsonSlurper = new JsonSlurper()
        when:
        List<ObjectToJsonTypeConverter> toJsonResolvers = [new SkipableJsonTypeConverter(),
                                                           new NumberToStringJsonTypeConverter(),
                                                           new NumberToJsonTypeConverter()]

        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(toJsonResolvers, [])
        Properties properties = new Properties()
        properties.put("some.object.numberAsText", 12)
        properties.put("some.object.number3", 15)
        properties.put("some.object2.skip.field", 13)
        properties.put("some.skip.field", 14)

        String json = converter.convertToJson(properties)
        def jsonObject = jsonSlurper.parseText(json)

        then:
        jsonObject.some.object.numberAsText == "12"
        jsonObject.some.object.number3 == 15
        jsonObject.some.object2.skip.field == null
        jsonObject.some.skip.field == null
        JsonCheckerUtil.leafOfPathIsNotPresent("some.object2.skip.field", json)
        JsonCheckerUtil.leafOfPathIsNotPresent("some.skip.field", json)
    }

    private static class SkipableJsonTypeConverter extends NumberToJsonTypeConverter {
        @Override
        Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                            Number convertedValue,
                                                            String propertyKey) {
            if (propertyKey.contains("skip.field")) {
                return Optional.of(SkipJsonField.SKIP_JSON_FIELD)
            }
            return Optional.empty()
        }
    }

    private static class NumberToStringJsonTypeConverter extends NumberToJsonTypeConverter {

        @Override
        Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                            Number convertedValue,
                                                            String propertyKey) {
            if (propertyKey.contains("numberAsText")) {
                return Optional.of(new StringJsonType(convertedValue.toString()))
            }
            return Optional.empty()
        }
    }

    private static class NumberToNullJsonTypeConverter extends NumberToJsonTypeConverter {

        @Override
        Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                            Number convertedValue,
                                                            String propertyKey) {
            if (propertyKey.contains("null")) {
                return Optional.of(JsonNullReferenceType.NULL_OBJECT)
            }
            return Optional.empty()
        }
    }
}