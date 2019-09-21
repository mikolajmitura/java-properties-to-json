package pl.jalokim.propertiestojson.resolvers;

import com.google.common.collect.ImmutableList;
import pl.jalokim.propertiestojson.JsonObjectFieldsValidator;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.path.PathMetadata;
import pl.jalokim.propertiestojson.resolvers.hierarchy.JsonTypeResolversHierarchyResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver;
import pl.jalokim.propertiestojson.util.exception.CannotOverrideFieldException;

import java.util.List;
import java.util.Optional;

import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.isArrayJson;
import static pl.jalokim.propertiestojson.object.JsonNullReferenceType.NULL_OBJECT;

public class PrimitiveJsonTypesResolver extends JsonTypeResolver {

    private final List<TextToConcreteObjectResolver> toObjectsResolvers;
    private final JsonTypeResolversHierarchyResolver resolversHierarchyResolver;

    public PrimitiveJsonTypesResolver(List<ObjectToJsonTypeResolver> toJsonResolvers,
                                      List<TextToConcreteObjectResolver> toObjectsResolvers) {
        this.toObjectsResolvers = ImmutableList.copyOf(toObjectsResolvers);
        this.resolversHierarchyResolver = new JsonTypeResolversHierarchyResolver(toJsonResolvers);
    }

    @Override
    public ObjectJsonType traverse(PathMetadata currentPathMetaData) {
        addPrimitiveFieldWhenIsValid(currentPathMetaData);
        return null;
    }

    private void addPrimitiveFieldWhenIsValid(PathMetadata currentPathMetaData) {
        currentPathMetaData.setValue(properties.get(propertyKey));
        JsonObjectFieldsValidator.checkThatFieldCanBeSet(currentObjectJsonType, currentPathMetaData, propertyKey);
        addPrimitiveFieldToCurrentJsonObject(currentPathMetaData);
    }

    private void addPrimitiveFieldToCurrentJsonObject(PathMetadata currentPathMetaData) {
        String field = currentPathMetaData.getFieldName();
        Object propertyValue = properties.get(propertyKey);
        if(currentPathMetaData.isArrayField()) {
            addFieldToArray(currentPathMetaData, propertyValue);
        } else {
            if(currentObjectJsonType.containsField(field) && isArrayJson(currentObjectJsonType.getField(field))) {
                AbstractJsonType abstractJsonType = resolvePrimitiveTypeAndReturn(propertyValue, propertyKey);
                ArrayJsonType currentArrayInObject = currentObjectJsonType.getJsonArray(field);
                if(isArrayJson(abstractJsonType)) {
                    ArrayJsonType newArray = (ArrayJsonType) abstractJsonType;
                    List<AbstractJsonType> abstractJsonTypes = newArray.convertToListWithoutRealNull();
                    for(int i = 0; i < abstractJsonTypes.size(); i++) {
                        currentArrayInObject.addElement(i, abstractJsonTypes.get(i), currentPathMetaData);
                    }
                } else {
                    throw new CannotOverrideFieldException(currentPathMetaData.getCurrentFullPath(), currentArrayInObject, propertyKey);
                }
            } else {
                currentObjectJsonType.addField(field, resolvePrimitiveTypeAndReturn(propertyValue, propertyKey), currentPathMetaData);
            }
        }
    }

    public Object getResolvedObject(String propertyValue, String propertyKey) {
        Optional<?> objectOptional = Optional.empty();
        for(TextToConcreteObjectResolver primitiveResolver : toObjectsResolvers) {
            if(!objectOptional.isPresent()) {
                objectOptional = primitiveResolver.returnConvertedValueForClearedText(this, propertyValue, propertyKey);
            }
        }
        return objectOptional.orElse(null);
    }

    public AbstractJsonType resolvePrimitiveTypeAndReturn(Object propertyValue, String propertyKey) {
        if(propertyValue == null) {
            return NULL_OBJECT;
        }
        ObjectToJsonTypeResolver<?> primitiveJsonTypeResolver = resolversHierarchyResolver.returnConcreteResolver(propertyValue);
        return primitiveJsonTypeResolver.returnJsonType(this, propertyValue, propertyKey);
    }

    protected void addFieldToArray(PathMetadata currentPathMetaData, Object propertyValue) {
        if(arrayWithGivenFieldNameExist(currentPathMetaData.getFieldName())) {
            fetchArrayAndAddElement(currentPathMetaData, propertyValue);
        } else {
            createArrayAndAddElement(currentPathMetaData, propertyValue);
        }
    }

    private boolean arrayWithGivenFieldNameExist(String field) {
        return currentObjectJsonType.containsField(field);
    }

    private void createArrayAndAddElement(PathMetadata currentPathMetaData, Object propertyValue) {
        ArrayJsonType arrayJsonTypeObject = new ArrayJsonType();
        addElementToArray(propertyValue, currentPathMetaData, arrayJsonTypeObject);
        currentObjectJsonType.addField(currentPathMetaData.getFieldName(), arrayJsonTypeObject, currentPathMetaData);
    }

    private void fetchArrayAndAddElement(PathMetadata currentPathMetaData, Object propertyValue) {
        ArrayJsonType arrayJsonType = getArrayJsonWhenIsValid(currentPathMetaData);
        addElementToArray(propertyValue, currentPathMetaData, arrayJsonType);
    }

    private void addElementToArray(Object propertyValue, PathMetadata currentPathMetaData, ArrayJsonType arrayJsonTypeObject) {
        arrayJsonTypeObject.addElement(currentPathMetaData.getPropertyArrayHelper(), resolvePrimitiveTypeAndReturn(propertyValue, propertyKey), currentPathMetaData);
    }

}
