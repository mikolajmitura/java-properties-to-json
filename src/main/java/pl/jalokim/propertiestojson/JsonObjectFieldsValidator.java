package pl.jalokim.propertiestojson;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.JsonNullReferenceType;
import pl.jalokim.propertiestojson.object.MergableObject;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.object.PrimitiveJsonType;
import pl.jalokim.propertiestojson.object.StringJsonType;
import pl.jalokim.propertiestojson.path.PathMetadata;
import pl.jalokim.propertiestojson.util.exception.CannotOverrideFieldException;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import static pl.jalokim.propertiestojson.Constants.EMPTY_STRING;
import static pl.jalokim.propertiestojson.Constants.NORMAL_DOT;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.EXPECTED_OBJECT_JSON_TYPE;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.EXPECTED_PRIMITIVE_JSON_TYPE;

public class JsonObjectFieldsValidator {

    public static void checkThatFieldCanBeSet(ObjectJsonType currentObjectJson, PathMetadata currentPathMetaData, String propertyKey) {
        if(currentObjectJson.containsField(currentPathMetaData.getFieldName())) {
            AbstractJsonType abstractJsonType = currentObjectJson.getJsonTypeByFieldName(currentPathMetaData.getFieldName());
            if(currentPathMetaData.isArrayField()) {
                if(isArrayJson(abstractJsonType)) {
                    ArrayJsonType jsonArray = currentObjectJson.getJsonArray(currentPathMetaData.getFieldName());
                    AbstractJsonType elementByDimArray = jsonArray.getElementByGivenDimIndexes(currentPathMetaData);
                    if(elementByDimArray != null) {
                        throwErrorWhenCannotMerge(currentPathMetaData, propertyKey, elementByDimArray);
                    }
                } else {
                    String parentFullPath = currentPathMetaData.isRoot() ? EMPTY_STRING : currentPathMetaData.getParent().getCurrentFullPath() + NORMAL_DOT;
                    throw new CannotOverrideFieldException(parentFullPath + currentPathMetaData.getFieldName(), abstractJsonType, propertyKey);
                }
            } else {
                throwErrorWhenCannotMerge(currentPathMetaData, propertyKey, abstractJsonType);
            }
        }
    }

    private static void throwErrorWhenCannotMerge(PathMetadata currentPathMetaData, String propertyKey, AbstractJsonType oldJsonValue) {
        if (!isMergableJsonType(oldJsonValue) ) {
            throw new CannotOverrideFieldException(currentPathMetaData.getCurrentFullPath(), oldJsonValue, propertyKey);
        }
    }

    public static void checkEarlierWasArrayJson(String propertyKey, PathMetadata currentPathMetaData, AbstractJsonType jsonType) {
        whenWasStringTypeThenThrowException(propertyKey, currentPathMetaData, jsonType);
        whenWasObjectTypeThenThrowException(propertyKey, currentPathMetaData, jsonType);
    }

    public static void checkEarlierWasJsonObject(String propertyKey, PathMetadata currentPathMetaData, AbstractJsonType jsonType) {
         if (!isObjectJson(jsonType)) {
             throw new CannotOverrideFieldException(currentPathMetaData.getCurrentFullPath(), jsonType, propertyKey);
         }
    }

    /*
        TODO maybe it is not necessary method... but only checkThatArrayElementIsPrimitiveType is neccessary
        and checking should be on add level, new field, new element in array...
     */
    public static void checkThatGivenArrayHasExpectedStructure(String propertyKey, PathMetadata currentPathMetaData, ArrayJsonType arrayJsonType) {
        AbstractJsonType abstractJsonType = arrayJsonType.getElementByGivenDimIndexes(currentPathMetaData);
        if(abstractJsonType != null) {
//            checkThatGivenObjectIsPrimitiveType(propertiesKey, currentPathMetaData.getFieldName(), arrayJsonType, indexes.get(i));
        }
    }

    private static void throwException(String message, String field, String propertyKey, AbstractJsonType jsonType) {
        throw new ParsePropertiesException(String.format(message, field, jsonType.toStringJson(), propertyKey));
    }

    private static void whenWasStringTypeThenThrowException(String propertyKey, PathMetadata currentPathMetaData, AbstractJsonType jsonType) {
        // TODO why only string type? all primitive maybe?
        if(isExpectedType(jsonType, StringJsonType.class)) {
            throwException(EXPECTED_PRIMITIVE_JSON_TYPE, currentPathMetaData.getCurrentFullPath(), propertyKey, jsonType);
        }
    }

    private static void whenWasObjectTypeThenThrowException(String propertyKey, PathMetadata currentPathMetaData, AbstractJsonType jsonType) {
        if(isExpectedType(jsonType, ObjectJsonType.class)) {
            throwException(EXPECTED_OBJECT_JSON_TYPE, currentPathMetaData.getFieldName(), propertyKey, jsonType);
        }
    }

    private static boolean isExpectedType(AbstractJsonType object, Class<?> type) {
        return object.getClass().equals(type);
    }

    public static boolean isObjectJson(AbstractJsonType jsonType) {
        return ObjectJsonType.class.isAssignableFrom(jsonType.getClass());
    }

    public static boolean isPrimitiveValue(AbstractJsonType jsonType) {
        return PrimitiveJsonType.class.isAssignableFrom(jsonType.getClass()) || JsonNullReferenceType.class.isAssignableFrom(jsonType.getClass());
    }

    public static boolean isArrayJson(AbstractJsonType jsonType) {
        return ArrayJsonType.class.isAssignableFrom(jsonType.getClass());
    }

    public static boolean isMergableJsonType(Object jsonType) {
        return MergableObject.class.isAssignableFrom(jsonType.getClass());
    }
}
