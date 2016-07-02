package pl.jalokim.propertiestojson;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.IntegerJson;
import pl.jalokim.propertiestojson.object.ObjectJson;
import pl.jalokim.propertiestojson.object.StringJson;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.util.Map;

import static pl.jalokim.propertiestojson.util.NumberUtil.getInt;
import static pl.jalokim.propertiestojson.util.NumberUtil.isInteger;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.UNEXPECTED_JSON_OBJECT;

public class JsonObjectsInitializer {

    private Map<String, String> properties;
    private String propertiesKey;
    private String[] fields;
    private ObjectJson currentObjectJson;

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

    private  ObjectJson fetchJsonObjectWhenIsNotPrimitive(String field) {
        AbstractJsonType jsonType = currentObjectJson.getJsonTypeByFieldName(field);
        JsonObjectFieldsValidator.checkIsJsonObject(propertiesKey, jsonType);
        return (ObjectJson) jsonType;
    }

    private  void addPrimitiveFieldWhenIsValid(String field) {
        JsonObjectFieldsValidator.checkThatFieldNotExistYet(currentObjectJson, field, propertiesKey);
        addPrimitiveFieldToCurrentJsonObject(field);
    }

    private void addPrimitiveFieldToCurrentJsonObject(String field) {
        String propValue = properties.get(propertiesKey);
        if (isInteger(propValue)) {
            currentObjectJson.addField(field, new IntegerJson(getInt(propValue)));
        } else {
            currentObjectJson.addField(field, new StringJson(propValue));
        }
    }

    private boolean isPrimitiveType(int index) {
        int lastIndex = fields.length - 1;
        return index == lastIndex;
    }

}
