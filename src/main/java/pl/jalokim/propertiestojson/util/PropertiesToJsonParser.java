package pl.jalokim.propertiestojson.util;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.IntegerJson;
import pl.jalokim.propertiestojson.object.ObjectJson;
import pl.jalokim.propertiestojson.object.StringJson;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.util.Map;
import java.util.Set;

import static pl.jalokim.propertiestojson.util.NumberUtil.getInt;
import static pl.jalokim.propertiestojson.util.NumberUtil.isInteger;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.UNEXPECTED_JSON_OBJECT;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.UNEXPECTED_PRIMITIVE_TYPE;

public class PropertiesToJsonParser {

    public static final String DOT = "\\.";

    public static String parseToJson(Map<String, String> properties) {
        ObjectJson coreObjectJson = new ObjectJson();
        for (String propertiesKey : getAllKeysFromMap(properties)) {
            addFieldsToJsonObject(properties, coreObjectJson, propertiesKey);
        }
        return coreObjectJson.toStringJson();
    }

    private static void addFieldsToJsonObject(Map<String, String> properties, ObjectJson coreObjectJson, String propertiesKey) {
        String[] fields = propertiesKey.split(DOT);
        ObjectJson currentObjectJson = coreObjectJson;
        addFieldForCurrentJsonObject(properties, propertiesKey, fields, currentObjectJson);
    }

    private static void addFieldForCurrentJsonObject(Map<String, String> properties, String propertiesKey, String[] fields, ObjectJson currentObjectJson) {
        for (int index = 0; index < fields.length; index++) {
            String field = fields[index];

            if (isPrimitiveType(fields, index)) {
                addPrimitiveFieldWhenIsValid(properties, propertiesKey, currentObjectJson, field);
            } else {
                currentObjectJson = fetchJsonObjectOrCreate(propertiesKey, currentObjectJson, field);
            }
        }
    }

    private static ObjectJson fetchJsonObjectOrCreate(String key, ObjectJson currentObjectJson, String field) {
        if (alreadHasField(currentObjectJson, field)) {
            currentObjectJson = fetchJsonObjectWhenIsNotPrimitive(key, currentObjectJson, field);
        } else {
            currentObjectJson = createNewJsonObjectAndAssignToCurrent(currentObjectJson, field);
        }
        return currentObjectJson;
    }

    private static ObjectJson createNewJsonObjectAndAssignToCurrent(ObjectJson currentObjectJson, String field) {
        ObjectJson nextObjectJson = new ObjectJson();
        currentObjectJson.addField(field, nextObjectJson);
        currentObjectJson = nextObjectJson;
        return currentObjectJson;
    }

    private static ObjectJson fetchJsonObjectWhenIsNotPrimitive(String key, ObjectJson currentObjectJson, String field) {
        AbstractJsonType jsonType = currentObjectJson.getJsonTypeByFieldName(field);
        checkIsJsonObject(key, jsonType);
        return (ObjectJson) jsonType;
    }

    private static void checkIsJsonObject(String key, AbstractJsonType jsonType) {
        if (isNotJsonPrimitiveType(jsonType)) {
            throw new ParsePropertiesException(String.format(UNEXPECTED_JSON_OBJECT, key));
        }
    }

    private static boolean alreadHasField(ObjectJson currentObjectJson, String field) {
        return currentObjectJson.containsField(field);
    }

    private static void addPrimitiveFieldWhenIsValid(Map<String, String> properties, String propertiesKey, ObjectJson currentObjectJson, String field) {
        checkThatFieldNotExistYet(propertiesKey, currentObjectJson, field);
        addPrimitiveFieldToCurrnetJsonObject(properties, propertiesKey, currentObjectJson, field);
    }

    private static void checkThatFieldNotExistYet(String propertiesKey, ObjectJson currentObjectJson, String field) {
        if (alreadHasField(currentObjectJson, field)) {
            throw new ParsePropertiesException(String.format(UNEXPECTED_PRIMITIVE_TYPE, propertiesKey));
        }
    }

    private static void addPrimitiveFieldToCurrnetJsonObject(Map<String, String> properties, String propertiesKey, ObjectJson currentObjectJson, String field) {
        String propValue = properties.get(propertiesKey);
        if (isInteger(propValue)) {
            currentObjectJson.addField(field, new IntegerJson(getInt(propValue)));
        } else {
            currentObjectJson.addField(field, new StringJson(propValue));
        }
    }

    private static boolean isNotJsonPrimitiveType(AbstractJsonType jsonType) {
        return !jsonType.getClass().equals(ObjectJson.class);
    }

    private static boolean isPrimitiveType(String[] fields, int index) {
        int lastIndex = fields.length - 1;
        return index == lastIndex;
    }

    private static Set<String> getAllKeysFromMap(Map<String, String> properties) {
        return properties.keySet();
    }


}
