package pl.jalokim.propertiestojson.resolvers;

import pl.jalokim.propertiestojson.JsonObjectFieldsValidator;
import pl.jalokim.propertiestojson.PropertyArrayHelper;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;

import java.util.List;

import static pl.jalokim.propertiestojson.object.ArrayJsonType.getNextDimensionOfArray;
import static pl.jalokim.propertiestojson.util.ListUtil.isLastIndex;

public class ArrayJsonTypeResolver extends JsonTypeResolver {

    @Override
    public ObjectJsonType traverse(String field) {
        fetchJsonObjectAndCreateArrayWhenNotExist(field);
        return currentObjectJsonType;
    }

    private void fetchJsonObjectAndCreateArrayWhenNotExist(String field) {
        PropertyArrayHelper propertyArrayHelper = new PropertyArrayHelper(field);
        field = propertyArrayHelper.getArrayFieldName();
        if(isArrayExist(field)) {
            fetchArrayAndAddElement(field, propertyArrayHelper);
        } else {
            createArrayAndAddElement(field, propertyArrayHelper);
        }
    }

    private boolean isArrayExist(String field) {
        return currentObjectJsonType.containsField(field);
    }

    private void fetchArrayAndAddElement(String field, PropertyArrayHelper propertyArrayHelper) {
        ArrayJsonType arrayJsonType = getArrayJsonWhenIsValid(field);
        List<Integer> indexes = propertyArrayHelper.getDimensionalIndexes();
        ArrayJsonType currentArray = arrayJsonType;
        for(int index = 0; index < indexes.size(); index++) {
            if(isLastIndex(indexes, index)) {
                if(currentArray.existElementByGivenIndex(indexes.get(index))) {
                    fetchJsonObjectWhenIsValid(field, indexes.get(index), currentArray);
                } else {
                    createJsonObjectAndAddToArray(propertyArrayHelper, currentArray);
                }
            } else {
                currentArray = getNextDimensionOfArray(currentArray, indexes, index);
            }
        }
    }

    private void createJsonObjectAndAddToArray(PropertyArrayHelper propertyArrayHelper, ArrayJsonType arrayJsonType) {
        ObjectJsonType nextObjectJsonType = new ObjectJsonType();
        arrayJsonType.addElement(propertyArrayHelper, nextObjectJsonType);
        currentObjectJsonType = nextObjectJsonType;
    }

    private void fetchJsonObjectWhenIsValid(String field, int index, ArrayJsonType arrayJsonType) {
        AbstractJsonType element = arrayJsonType.getElement(index);
        JsonObjectFieldsValidator.checkThatArrayElementIsObjectJsonType(field, arrayJsonType, element, propertiesKey, index);
        currentObjectJsonType = (ObjectJsonType) element;
    }

    private void createArrayAndAddElement(String field, PropertyArrayHelper propertyArrayHelper) {
        ArrayJsonType arrayJsonTypeObject = new ArrayJsonType();
        ObjectJsonType nextObjectJsonType = new ObjectJsonType();
        arrayJsonTypeObject.addElement(propertyArrayHelper, nextObjectJsonType);
        currentObjectJsonType.addField(field, arrayJsonTypeObject);
        currentObjectJsonType = nextObjectJsonType;
    }
}
