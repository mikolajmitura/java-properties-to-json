package pl.jalokim.propertiestojson;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.object.StringJsonType;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.*;


public class JsonObjectFieldsValidator {


    public static void checkEarlierWasJsonString(ObjectJsonType currentObjectJson, String field, String propertiesKey) {

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

    public static void checkIsListOnlyForPrimitive(String propertiesKey, String field, ArrayJsonType arrayJsonType, int index) {

        if (arrayJsonType.getElement(index) != null && !(arrayJsonType.getElement(index) instanceof StringJsonType)) {
            throwException(EXPECTED_ARRAY_WITH_JSON_OBJECT_TYPES, field, propertiesKey, arrayJsonType);
        }
    }

    public static void checkIsArrayOnlyForObjects(String field, ArrayJsonType arrayJsonType, AbstractJsonType element, String propertiesKey) {
        if (!(element instanceof ObjectJsonType)) {
            throwException(EXPECTED_ARRAY_WITH_PRIMITIVE_TYPES, field, propertiesKey, arrayJsonType);
        }
    }

    private static void throwException(String message, String field, String propertiesKey, AbstractJsonType jsonType) {
        throw new ParsePropertiesException(String.format(message, field, propertiesKey, jsonType.toStringJson()));
    }

    private static void whenWasStringTypeThenThrowException(String propertiesKey, String field, AbstractJsonType jsonType) {
        if (isExpectedType(jsonType, StringJsonType.class)) {
            throwException(EXPECTED_PRIMITIVE_JSON_TYPE, field, propertiesKey, jsonType);
        }
    }

    private static void whenWasObjectTypeThenThrowException(String propertiesKey, String field, AbstractJsonType jsonType) {
        if (isExpectedType(jsonType, ObjectJsonType.class)) {
            throwException(EXPECTED_OBJECT_JSON_TYPE, field, propertiesKey, jsonType);
        }
    }

    private static void whenWasArrayTypeThenThrowException(ObjectJsonType currentObjectJson, String field, String propertiesKey, AbstractJsonType jsonType) {
        if (isExpectedType(jsonType, ArrayJsonType.class)) {
            throwException(EXPECTED_ARRAY_JSON_TYPE, field, propertiesKey, currentObjectJson.getJsonTypeByFieldName(field));
        }
    }

    private static boolean isExpectedType(AbstractJsonType object, Class<?> type) {
        return object.getClass().equals(type);
    }
}
