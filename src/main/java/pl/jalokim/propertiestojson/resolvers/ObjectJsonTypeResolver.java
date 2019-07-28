package pl.jalokim.propertiestojson.resolvers;

import pl.jalokim.propertiestojson.JsonObjectFieldsValidator;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.path.PathMetadata;

public class ObjectJsonTypeResolver extends JsonTypeResolver {


    @Override
    public ObjectJsonType traverse(PathMetadata currentPathMetaData) {
        fetchJsonObjectOrCreate(currentPathMetaData);
        return currentObjectJsonType;
    }

    private void fetchJsonObjectOrCreate(PathMetadata currentPathMetaData) {
        if (currentObjectJsonType.containsField(currentPathMetaData.getFieldName())) {
            fetchJsonObjectWhenIsNotPrimitive(currentPathMetaData);
        } else {
            createNewJsonObjectAndAssignToCurrent(currentPathMetaData.getFieldName());
        }
    }

    private void createNewJsonObjectAndAssignToCurrent(String field) {
        ObjectJsonType nextObjectJsonType = new ObjectJsonType();
        currentObjectJsonType.addField(field, nextObjectJsonType);
        currentObjectJsonType = nextObjectJsonType;
    }

    private void fetchJsonObjectWhenIsNotPrimitive(PathMetadata currentPathMetaData) {
        AbstractJsonType jsonType = currentObjectJsonType.getJsonTypeByFieldName(currentPathMetaData.getFieldName());
        JsonObjectFieldsValidator.checkEarlierWasJsonObject(propertyKey, currentPathMetaData, jsonType);
        currentObjectJsonType = (ObjectJsonType) jsonType;
    }
}
