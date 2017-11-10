package pl.jalokim.propertiestojson.resolvers;

import pl.jalokim.propertiestojson.JsonObjectFieldsValidator;
import pl.jalokim.propertiestojson.PropertyArrayHelper;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;

public class ArrayJsonTypeResolver extends JsonTypeResolver {

    @Override
    public ObjectJsonType traverse(String field) {
        fetchJsonObjectAndCreateArrayWhenNotExist(field);
        return currentObjectJsonType;
    }

    private void fetchJsonObjectAndCreateArrayWhenNotExist(String field) {
        PropertyArrayHelper propertyArrayHelper = new PropertyArrayHelper(field);
        field = propertyArrayHelper.getArrayFieldName();
        if (isArrayExist(field)) {
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
        if (existElementInArrayByGivenIndex(arrayJsonType, propertyArrayHelper.getIndexArray())) {
            fetchJsonObjectWhenIsValid(field, propertyArrayHelper, arrayJsonType);
        } else {
            createJsonObjectAndAddToArray(propertyArrayHelper, arrayJsonType);
        }
    }

    private boolean existElementInArrayByGivenIndex(ArrayJsonType arrayJsonType, int index) {
        return arrayJsonType.getElement(index) != null;
    }

    private void createJsonObjectAndAddToArray(PropertyArrayHelper propertyArrayHelper, ArrayJsonType arrayJsonType) {
        ObjectJsonType nextObjectJsonType = new ObjectJsonType();
        arrayJsonType.addElement(propertyArrayHelper.getIndexArray(), nextObjectJsonType);
        currentObjectJsonType = nextObjectJsonType;
    }

    private void fetchJsonObjectWhenIsValid(String field, PropertyArrayHelper propertyArrayHelper, ArrayJsonType arrayJsonType) {
        AbstractJsonType element = arrayJsonType.getElement(propertyArrayHelper.getIndexArray());
        JsonObjectFieldsValidator.checkThatArrayElementIsObjectJsonType(field, arrayJsonType, element, propertiesKey, propertyArrayHelper.getIndexArray());
        currentObjectJsonType = (ObjectJsonType) element;
    }

    private void createArrayAndAddElement(String field, PropertyArrayHelper propertyArrayHelper) {
        ArrayJsonType arrayJsonTypeObject = new ArrayJsonType();
        ObjectJsonType nextObjectJsonType = new ObjectJsonType();
        arrayJsonTypeObject.addElement(propertyArrayHelper.getIndexArray(), nextObjectJsonType);
        currentObjectJsonType.addField(field, arrayJsonTypeObject);
        currentObjectJsonType = nextObjectJsonType;
    }
}
