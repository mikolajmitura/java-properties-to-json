package pl.jalokim.propertiestojson;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ObjectJson;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.UNEXPECTED_JSON_OBJECT;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.UNEXPECTED_PRIMITIVE_TYPE;

public class JsonObjectFieldsValidator {

    public static void checkThatFieldNotExistYet(ObjectJson currentObjectJson, String field, String propertiesKey) {
        if (currentObjectJson.containsField(field)) {
            throw new ParsePropertiesException(String.format(UNEXPECTED_PRIMITIVE_TYPE, propertiesKey));
        }
    }

    public static void checkIsJsonObject(String key, AbstractJsonType jsonType) {
        if (isNotJsonPrimitiveType(jsonType)) {
            throw new ParsePropertiesException(String.format(UNEXPECTED_JSON_OBJECT, key));
        }
    }

    private static boolean isNotJsonPrimitiveType(AbstractJsonType jsonType) {
        return !jsonType.getClass().equals(ObjectJson.class);
    }
}
