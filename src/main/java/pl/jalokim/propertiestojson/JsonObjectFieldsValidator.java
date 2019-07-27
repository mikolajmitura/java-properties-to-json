package pl.jalokim.propertiestojson;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.object.StringJsonType;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.util.List;

import static pl.jalokim.propertiestojson.util.ListUtil.isLastIndex;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.*;


public class JsonObjectFieldsValidator {


    public static void checkEarlierWasJsonPrimitiveType(ObjectJsonType currentObjectJson, String field, String propertiesKey) {

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

    /*
        TODO maybe it is not necessary method... but only checkThatArrayElementIsPrimitiveType is neccessary
        and checking should be on add level, new field, new element in array...
     */
    public static void checkThatGivenArrayHasExpectedStructure(String propertiesKey, String field, ArrayJsonType arrayJsonType,
                                                               PropertyArrayHelper propertyArrayHelper) {
        List<Integer> indexes = propertyArrayHelper.getDimensionalIndexes();
        int size = propertyArrayHelper.getDimensionalIndexes().size();
        ArrayJsonType currentArray = arrayJsonType;
        for (int i = 0; i < size; i++) {
            if (isLastIndex(propertyArrayHelper.getDimensionalIndexes(), i)) {
                checkThatArrayElementIsPrimitiveType(propertiesKey, field, arrayJsonType, indexes.get(i));
            }  else {
                AbstractJsonType element = currentArray.getElement(indexes.get(i));
                if(element == null) {
                    return;
                }
                if (element instanceof ArrayJsonType) {
                    currentArray = (ArrayJsonType) element;
                } else {
                    // TODO done it and test this one
                    // expected type which is in (AbstractJsonType element)  at given array in given path...
                    //throwException();
                    List<Integer> currentIndexes = indexes.subList(0, i);
                    throw new RuntimeException("expected type " + element.getClass() +  " at given array in given path " + currentIndexes);
                }
            }
        }
    }

    public static void checkThatArrayElementIsPrimitiveType(String propertiesKey, String field, ArrayJsonType arrayJsonType,
                                                            int index) {

        // TODO why only arrayJsonType.getElement(index) instanceof StringJsonType ??? all primitive should be...
        // TODO test another types....
        if (arrayJsonType.getElement(index) != null && !(arrayJsonType.getElement(index) instanceof StringJsonType)) {
            throwException(EXPECTED_ELEMENT_ARRAY_JSON_OBJECT_TYPES, field, index, propertiesKey, arrayJsonType);
        }
    }

    public static void checkThatArrayElementIsObjectJsonType(String field, ArrayJsonType arrayJsonType, AbstractJsonType element,
                                                             String propertiesKey, int index) {
        if (!(element instanceof ObjectJsonType)) {
            throwException(EXPECTED_ELEMENT_ARRAY_PRIMITIVE_TYPES, field, index, propertiesKey, arrayJsonType);
        }
    }

    private static void throwException(String message, String field, String propertiesKey, AbstractJsonType jsonType) {
        throw new ParsePropertiesException(String.format(message, field, jsonType.toStringJson(), propertiesKey));
    }

    private static void throwException(String message, String field, int index, String propertiesKey, AbstractJsonType jsonType) {
        throw new ParsePropertiesException(String.format(message, field, index, ((ArrayJsonType) jsonType).getElement(index).toStringJson(), propertiesKey));
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
