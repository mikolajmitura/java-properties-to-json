package pl.jalokim.propertiestojson.resolvers;

import pl.jalokim.propertiestojson.JsonObjectFieldsValidator;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;

public class ObjectJsonTypeResolver extends JsonTypeResolver {


    @Override
    public ObjectJsonType traverse(String field) {
        fetchJsonObjectOrCreate(field);
        return currentObjectJsonType;
    }

    private void fetchJsonObjectOrCreate(String field) {
        if (currentObjectJsonType.containsField(field)) {
            fetchJsonObjectWhenIsNotPrimitive(field);
        } else {
            createNewJsonObjectAndAssignToCurrent(field);
        }
    }

    private void createNewJsonObjectAndAssignToCurrent(String field) {
        ObjectJsonType nextObjectJsonType = new ObjectJsonType();
        currentObjectJsonType.addField(field, nextObjectJsonType);
        currentObjectJsonType = nextObjectJsonType;
    }

    private void fetchJsonObjectWhenIsNotPrimitive(String field) {
        AbstractJsonType jsonType = currentObjectJsonType.getJsonTypeByFieldName(field);
        JsonObjectFieldsValidator.checkEarlierWasJsonObject(propertiesKey, field, jsonType);
        currentObjectJsonType = (ObjectJsonType) jsonType;
    }
}
