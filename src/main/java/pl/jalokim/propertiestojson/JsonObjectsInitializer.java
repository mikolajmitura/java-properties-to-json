package pl.jalokim.propertiestojson;

import pl.jalokim.propertiestojson.object.*;

import java.util.Map;

import static pl.jalokim.propertiestojson.util.NumberUtil.getInt;
import static pl.jalokim.propertiestojson.util.NumberUtil.isInteger;


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

            if (isPrimitiveType(index)) {
                addPrimitiveFieldWhenIsValid(field);
            } else {
                currentObjectJson = fetchJsonObjectOrCreate(field);
            }
        }
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
        JsonObjectFieldsValidator.checkIsJsonObject(propertiesKey, jsonType);
        return (ObjectJson) jsonType;
    }

    private void addPrimitiveFieldWhenIsValid(String field) {
        JsonObjectFieldsValidator.checkThatFieldNotExistYet(currentObjectJson, field, propertiesKey);
        addPrimitiveFieldToCurrentJsonObject(field);
    }

    private void addPrimitiveFieldToCurrentJsonObject(String field) {
        String propValue = properties.get(propertiesKey);
        if (isArray(propValue)){
            currentObjectJson.addField(field, new ArrayJson(propValue.split(arrayDelimeter)));
        } else if (isInteger(propValue)) {
            currentObjectJson.addField(field, new IntegerJson(getInt(propValue)));
        } else {
            currentObjectJson.addField(field, new StringJson(propValue));
        }
    }

    private boolean isArray(String propValue) {
        return propValue.contains(arrayDelimeter);
    }

    private boolean isPrimitiveType(int index) {
        int lastIndex = fields.length - 1;
        return index == lastIndex;
    }

}
