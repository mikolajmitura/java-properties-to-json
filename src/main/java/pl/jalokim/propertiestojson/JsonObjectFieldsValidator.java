package pl.jalokim.propertiestojson;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJson;
import pl.jalokim.propertiestojson.object.ObjectJson;
import pl.jalokim.propertiestojson.object.StringJson;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.*;


public class JsonObjectFieldsValidator {


    public static void checkEarlierWasJsonString(ObjectJson currentObjectJson, String field, String propertiesKey) {

        if (currentObjectJson.containsField(field)) {
            AbstractJsonType jsonType = currentObjectJson.getJsonTypeByFieldName(field);
            whenWasArrayTypeThenThrowException(currentObjectJson, field, propertiesKey, jsonType);
            whenWasObjectTypeThenThrowException(propertiesKey, field, jsonType);
        }
    }

    public static void checkEalierWasArrayJson(String propertiesKey, String field, AbstractJsonType jsonType) {

        whenWasStringTypeThenThrowException(propertiesKey, field, jsonType);
        whenWasObjectTypeThenThrowException(propertiesKey, field, jsonType);

    }

    public static void checkEarlierWasJsonObject(String propertiesKey, String field, AbstractJsonType jsonType) {

        whenWasStringTypeThenThrowException(propertiesKey, field, jsonType);
    }

    public static void checkIsListOnlyForPrimitive(String propertiesKey, String field, ArrayJson arrayJson, int index) {

        if (arrayJson.getElement(index) != null && !(arrayJson.getElement(index) instanceof StringJson)) {
            throwException(EXPECTED_ARRAY_WITH_JSON_OBJECT_TYPES, field, propertiesKey, arrayJson);
        }
    }

    public static void checkIsArrayOnlyForObjects(String field, ArrayJson arrayJson, AbstractJsonType element, String propertiesKey) {
        if (!(element instanceof ObjectJson)) {
            throwException(EXPECTED_ARRAY_WITH_PRIMITIVE_TYPES, field, propertiesKey, arrayJson);
        }
    }

    private static void throwException(String message, String field, String propertiesKey, AbstractJsonType jsonType) {
        throw new ParsePropertiesException(String.format(message, field, propertiesKey, jsonType.toStringJson()));
    }

    private static void whenWasStringTypeThenThrowException(String propertiesKey, String field, AbstractJsonType jsonType) {
        if (isExpectedType(jsonType, StringJson.class)) {
            throwException(EXPECTED_PRIMITIVE_JSON_TYPE, field, propertiesKey, jsonType);
        }
    }

    private static void whenWasObjectTypeThenThrowException(String propertiesKey, String field, AbstractJsonType jsonType) {
        if (isExpectedType(jsonType, ObjectJson.class)) {
            throwException(EXPECTED_OBJECT_JSON_TYPE, field, propertiesKey, jsonType);
        }
    }

    private static void whenWasArrayTypeThenThrowException(ObjectJson currentObjectJson, String field, String propertiesKey, AbstractJsonType jsonType) {
        if (isExpectedType(jsonType, ArrayJson.class)) {
            throwException(EXPECTED_ARRAY_JSON_TYPE, field, propertiesKey, currentObjectJson.getJsonTypeByFieldName(field));
        }
    }

    private static boolean isExpectedType(AbstractJsonType object, Class<?> type) {
        return object.getClass().equals(type);
    }
}
