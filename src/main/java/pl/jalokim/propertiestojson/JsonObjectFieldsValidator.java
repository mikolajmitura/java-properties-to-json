package pl.jalokim.propertiestojson;

import com.google.gson.JsonObject;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJson;
import pl.jalokim.propertiestojson.object.ObjectJson;
import pl.jalokim.propertiestojson.object.StringJson;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.*;


// TODO rename that methods
public class JsonObjectFieldsValidator {


    public static void checkThatFieldNotExistYet(ObjectJson currentObjectJson, String field, String propertiesKey) {

        if (currentObjectJson.containsField(field) && currentObjectJson.getJsonTypeByFieldName(field) instanceof ArrayJson) {
            throw new ParsePropertiesException(String.format(EXPECTED_ARRAY_JSON_TYPE, field, propertiesKey, currentObjectJson.getJsonTypeByFieldName(field).toStringJson()));
        }

        if (currentObjectJson.containsField(field)) {
            throw new ParsePropertiesException(String.format(EXPECTED_OBJECT_JSON_TYPE, field, propertiesKey, currentObjectJson.getJsonTypeByFieldName(field).toStringJson()));
        }

    }

    public static void checkIsJsonObject(String propertiesKey, String field, AbstractJsonType jsonType) {
        if (isNotJsonPrimitiveType(jsonType)) {
            throw new ParsePropertiesException(String.format(EXPECTED_PRIMITIVE_JSON_TYPE, field, propertiesKey, jsonType.toStringJson()));
        }
    }

    public static void checkIsListOnlyForPrimitive(String propertiesKey, String field, ArrayJson arrayJson, int index){

        if (arrayJson.getElement(index) != null && !(arrayJson.getElement(index) instanceof StringJson)){
            throw new ParsePropertiesException(String.format(EXPECTED_ARRAY_WITH_JSON_OBJECT_TYPES, field, propertiesKey, arrayJson.toStringJson()));
        }
    }

    public static void checkIsArrayJsonObject(String propertiesKey, String field, AbstractJsonType jsonType){

        if (jsonType instanceof StringJson){
            throw new ParsePropertiesException(String.format(EXPECTED_PRIMITIVE_JSON_TYPE, field, propertiesKey, jsonType.toStringJson()));
        }

        if (jsonType instanceof ObjectJson){
            throw new ParsePropertiesException(String.format(EXPECTED_OBJECT_JSON_TYPE, field, propertiesKey, jsonType.toStringJson()));
        }

    }

    private static boolean isNotJsonPrimitiveType(AbstractJsonType jsonType) {
        return !jsonType.getClass().equals(ObjectJson.class);
    }
}
