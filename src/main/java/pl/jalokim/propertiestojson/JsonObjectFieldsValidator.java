package pl.jalokim.propertiestojson;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.JsonNullReferenceType;
import pl.jalokim.propertiestojson.object.MergableObject;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.object.PrimitiveJsonType;
import pl.jalokim.propertiestojson.path.PathMetadata;
import pl.jalokim.propertiestojson.util.exception.CannotOverrideFieldException;

import static pl.jalokim.propertiestojson.Constants.EMPTY_STRING;
import static pl.jalokim.propertiestojson.Constants.NORMAL_DOT;

public class JsonObjectFieldsValidator {

    public static void checkThatFieldCanBeSet(ObjectJsonType currentObjectJson, PathMetadata currentPathMetaData, String propertyKey) {
        if(currentObjectJson.containsField(currentPathMetaData.getFieldName())) {
            AbstractJsonType abstractJsonType = currentObjectJson.getField(currentPathMetaData.getFieldName());
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

    public static void checkEarlierWasJsonObject(String propertyKey, PathMetadata currentPathMetaData, AbstractJsonType jsonType) {
         if (!isObjectJson(jsonType)) {
             throw new CannotOverrideFieldException(currentPathMetaData.getCurrentFullPath(), jsonType, propertyKey);
         }
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
