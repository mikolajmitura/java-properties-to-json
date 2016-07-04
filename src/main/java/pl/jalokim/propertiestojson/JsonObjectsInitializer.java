package pl.jalokim.propertiestojson;

import pl.jalokim.propertiestojson.object.*;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.util.Map;

import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.checkIsListOnlyForPrimitive;
import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.checkIsArrayJsonObject;
import static pl.jalokim.propertiestojson.util.NumberUtil.getInt;
import static pl.jalokim.propertiestojson.util.NumberUtil.isInteger;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.EXPECTED_ARRAY_WITH_PRIMITIVE_TYPES;


public class JsonObjectsInitializer {

    private Map<String, String> properties;
    private String propertiesKey;
    private String[] fields;
    private ObjectJson currentObjectJson;
    private String arrayDelimeter=",";

    public JsonObjectsInitializer(Map<String, String> properties, String propertiesKey, String[] fields, ObjectJson coreObjectJson) {
        this.properties = properties;
        this.propertiesKey = propertiesKey;
        this.fields = fields;
        this.currentObjectJson = coreObjectJson;
    }

    public void addFieldForCurrentJsonObject() {
        for (int index = 0; index < fields.length; index++) {
            String field = fields[index];
            // TODO huge because this code not clear, make two impls for traverse and init for Objects ane for primitve
            if (isLastInFieldsSequence(index)) {
                addPrimitiveFieldWhenIsValid(field);
            } else if (isArrayField(field)){
                fetchJsonObjectAndCreateArrayWhenNotExist(field);
            }else {
                currentObjectJson = fetchJsonObjectOrCreate(field);
            }
        }
    }

    private void fetchJsonObjectAndCreateArrayWhenNotExist(String field) {
        PropertyArrayHelper propertyArrayHelper = new PropertyArrayHelper(field);
        field = propertyArrayHelper.getArrayfieldName();
        if (currentObjectJson.containsField(field)){
            ArrayJson arrayJson = getArrayJsonWhenIsValid(field);
            if (arrayJson.getElement(propertyArrayHelper.getIndexArray()) == null){
                ObjectJson nextObjectJson = new ObjectJson();
                arrayJson.addElement(propertyArrayHelper.getIndexArray(), nextObjectJson);
                currentObjectJson = nextObjectJson;
            } else {
                AbstractJsonType element = arrayJson.getElement(propertyArrayHelper.getIndexArray());
                if (!(element instanceof  ObjectJson)){
                    throw new ParsePropertiesException(String.format(EXPECTED_ARRAY_WITH_PRIMITIVE_TYPES, field, propertiesKey, arrayJson.toStringJson()));
                }
                currentObjectJson = (ObjectJson) element;
            }
        } else {
            ArrayJson arrayJsonObject = new ArrayJson();
            ObjectJson nextObjectJson = new ObjectJson();
            arrayJsonObject.addElement(propertyArrayHelper.getIndexArray(), nextObjectJson);
            currentObjectJson.addField(field, arrayJsonObject);
            currentObjectJson = nextObjectJson;
        }
    }

    private ArrayJson getArrayJsonWhenIsValid(String field) {
        AbstractJsonType jsonType = currentObjectJson.getJsonTypeByFieldName(field);
        checkIsArrayJsonObject(propertiesKey,field, jsonType);
        return (ArrayJson) jsonType;
    }

    private boolean isArrayField(String field) {
        return field.contains("[")&&field.contains("]");
    }

    private ObjectJson fetchJsonObjectOrCreate(String field) {
        if (currentObjectJson.containsField(field)) {
            currentObjectJson = fetchJsonObjectWhenIsNotPrimitive(field);
        } else {
            currentObjectJson = createNewJsonObjectAndAssignToCurrent(field);
        }
        return currentObjectJson;
    }

    private ObjectJson createNewJsonObjectAndAssignToCurrent(String field) {
        ObjectJson nextObjectJson = new ObjectJson();
        currentObjectJson.addField(field, nextObjectJson);
        currentObjectJson = nextObjectJson;
        return currentObjectJson;
    }

    private ObjectJson fetchJsonObjectWhenIsNotPrimitive(String field) {
        AbstractJsonType jsonType = currentObjectJson.getJsonTypeByFieldName(field);
        JsonObjectFieldsValidator.checkIsJsonObject(propertiesKey,field, jsonType);
        return (ObjectJson) jsonType;
    }

    private void addPrimitiveFieldWhenIsValid(String field) {
        JsonObjectFieldsValidator.checkThatFieldNotExistYet(currentObjectJson, field, propertiesKey);
        addPrimitiveFieldToCurrentJsonObject(field);
    }

    private void addPrimitiveFieldToCurrentJsonObject(String field) {
        String propertyValue = properties.get(propertiesKey);
        if (isSimpleArray(propertyValue)){
            currentObjectJson.addField(field, new ArrayJson(propertyValue.split(arrayDelimeter)));
        } else if (isArrayField(field)){
            PropertyArrayHelper propertyArrayHelper = new PropertyArrayHelper(field);
            field = propertyArrayHelper.getArrayfieldName();
            if (currentObjectJson.containsField(field)){
                ArrayJson arrayJson = getArrayJsonWhenIsValid(field);
                checkIsListOnlyForPrimitive( propertiesKey, field, arrayJson,propertyArrayHelper.getIndexArray());
                arrayJson.addElement(propertyArrayHelper.getIndexArray(),new StringJson(propertyValue));
            } else {
                ArrayJson arrayJsonObject = new ArrayJson();
                arrayJsonObject.addElement(propertyArrayHelper.getIndexArray(), new StringJson(propertyValue));
                currentObjectJson.addField(field, arrayJsonObject);
            }
        } else if (isInteger(propertyValue)) {
            currentObjectJson.addField(field, new IntegerJson(getInt(propertyValue)));
        } else {
            currentObjectJson.addField(field, new StringJson(propertyValue));
        }
    }

    private boolean isSimpleArray(String propValue) {
        return propValue.contains(arrayDelimeter);
    }

    private boolean isLastInFieldsSequence(int index) {
        int lastIndex = fields.length - 1;
        return index == lastIndex;
    }

}
