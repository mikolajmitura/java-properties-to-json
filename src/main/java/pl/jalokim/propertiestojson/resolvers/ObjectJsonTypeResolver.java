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
            createNewJsonObjectAndAssignToCurrent(currentPathMetaData);
        }
    }

    private void createNewJsonObjectAndAssignToCurrent(PathMetadata currentPathMetaData) {
        ObjectJsonType nextObjectJsonType = new ObjectJsonType();
        currentObjectJsonType.addField(currentPathMetaData.getFieldName(), nextObjectJsonType, currentPathMetaData);
        currentObjectJsonType = nextObjectJsonType;
    }

    private void fetchJsonObjectWhenIsNotPrimitive(PathMetadata currentPathMetaData) {
        AbstractJsonType jsonType = currentObjectJsonType.getField(currentPathMetaData.getFieldName());
        JsonObjectFieldsValidator.checkEarlierWasJsonObject(propertyKey, currentPathMetaData, jsonType);
        currentObjectJsonType = (ObjectJsonType) jsonType;
    }
}
