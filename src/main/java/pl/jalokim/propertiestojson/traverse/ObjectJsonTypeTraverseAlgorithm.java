package pl.jalokim.propertiestojson.traverse;

import pl.jalokim.propertiestojson.JsonObjectFieldsValidator;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ObjectJson;

public class ObjectJsonTypeTraverseAlgorithm extends TraverseAlgorithm{


    @Override
    public ObjectJson traverse(String field) {
        fetchJsonObjectOrCreate(field);
        return currentObjectJson;
    }

    private void fetchJsonObjectOrCreate(String field) {
        if (currentObjectJson.containsField(field)) {
            fetchJsonObjectWhenIsNotPrimitive(field);
        } else {
            createNewJsonObjectAndAssignToCurrent(field);
        }
    }

    private void createNewJsonObjectAndAssignToCurrent(String field) {
        ObjectJson nextObjectJson = new ObjectJson();
        currentObjectJson.addField(field, nextObjectJson);
        currentObjectJson = nextObjectJson;
    }

     private void fetchJsonObjectWhenIsNotPrimitive(String field) {
        AbstractJsonType jsonType = currentObjectJson.getJsonTypeByFieldName(field);
        JsonObjectFieldsValidator.checkEarlierWasJsonObject(propertiesKey,field, jsonType);
         currentObjectJson = (ObjectJson) jsonType;
    }
}
