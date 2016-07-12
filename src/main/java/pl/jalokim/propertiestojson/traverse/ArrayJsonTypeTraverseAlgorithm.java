package pl.jalokim.propertiestojson.traverse;

import pl.jalokim.propertiestojson.JsonObjectFieldsValidator;
import pl.jalokim.propertiestojson.PropertyArrayHelper;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJson;
import pl.jalokim.propertiestojson.object.ObjectJson;

public class ArrayJsonTypeTraverseAlgorithm extends  TraverseAlgorithm{

    @Override
    public ObjectJson traverse(String field) {
        fetchJsonObjectAndCreateArrayWhenNotExist(field);
        return currentObjectJson;
    }

    private void fetchJsonObjectAndCreateArrayWhenNotExist(String field) {
        PropertyArrayHelper propertyArrayHelper = new PropertyArrayHelper(field);
        field = propertyArrayHelper.getArrayfieldName();
        if (isArrayExist(field)){
            fetchArrayAndAddElement(field, propertyArrayHelper);
        } else {
            createArrayAndAddElement(field, propertyArrayHelper);
        }
    }

    private boolean isArrayExist(String field) {
        return currentObjectJson.containsField(field);
    }

    private void fetchArrayAndAddElement(String field, PropertyArrayHelper propertyArrayHelper) {
        ArrayJson arrayJson = getArrayJsonWhenIsValid(field);
        if (existElementInArrayByGivenIdex(arrayJson, propertyArrayHelper.getIndexArray())){
            fetchJsonObjectWhenIsValid(field, propertyArrayHelper, arrayJson);
        } else {
            createJsonObjectAndAddToArray(propertyArrayHelper, arrayJson);
        }
    }

    private boolean existElementInArrayByGivenIdex(ArrayJson arrayJson, int index) {
        return arrayJson.getElement(index) != null;
    }

    private void createJsonObjectAndAddToArray(PropertyArrayHelper propertyArrayHelper, ArrayJson arrayJson) {
        ObjectJson nextObjectJson = new ObjectJson();
        arrayJson.addElement(propertyArrayHelper.getIndexArray(), nextObjectJson);
        currentObjectJson = nextObjectJson;
    }

    private void fetchJsonObjectWhenIsValid(String field, PropertyArrayHelper propertyArrayHelper, ArrayJson arrayJson) {
        AbstractJsonType element = arrayJson.getElement(propertyArrayHelper.getIndexArray());
        JsonObjectFieldsValidator.checkIsArrayOnlyForObjects(field, arrayJson, element, propertiesKey);
        currentObjectJson = (ObjectJson) element;
    }

    private void createArrayAndAddElement(String field, PropertyArrayHelper propertyArrayHelper) {
        ArrayJson arrayJsonObject = new ArrayJson();
        ObjectJson nextObjectJson = new ObjectJson();
        arrayJsonObject.addElement(propertyArrayHelper.getIndexArray(), nextObjectJson);
        currentObjectJson.addField(field, arrayJsonObject);
        currentObjectJson = nextObjectJson;
    }
}
