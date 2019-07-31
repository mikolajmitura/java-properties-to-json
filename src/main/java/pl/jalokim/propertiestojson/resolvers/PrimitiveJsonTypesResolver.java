package pl.jalokim.propertiestojson.resolvers;

import com.google.common.collect.ImmutableList;
import pl.jalokim.propertiestojson.JsonObjectFieldsValidator;
import pl.jalokim.propertiestojson.PropertyArrayHelper;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.path.PathMetadata;
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver;
import pl.jalokim.propertiestojson.util.exception.CannotOverrideFieldException;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.util.List;

import static java.lang.String.format;
import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.checkThatGivenArrayHasExpectedStructure;
import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.isArrayJson;
import static pl.jalokim.propertiestojson.object.JsonNullReferenceType.NULL_OBJECT;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.CANNOT_FIND_TYPE_RESOLVER_MSG;

public class PrimitiveJsonTypesResolver extends JsonTypeResolver {

    private final List<PrimitiveJsonTypeResolver> primitiveResolvers;

    public PrimitiveJsonTypesResolver(List<PrimitiveJsonTypeResolver> primitiveResolvers) {
        this.primitiveResolvers = ImmutableList.copyOf(primitiveResolvers);
    }

    @Override
    public ObjectJsonType traverse(PathMetadata currentPathMetaData) {
        addPrimitiveFieldWhenIsValid(currentPathMetaData);
        return null;
    }

    private void addPrimitiveFieldWhenIsValid(PathMetadata currentPathMetaData) {
        JsonObjectFieldsValidator.checkThatFieldCanBeSet(currentObjectJsonType, currentPathMetaData, propertyKey);
        addPrimitiveFieldToCurrentJsonObject(currentPathMetaData);
    }

    private void addPrimitiveFieldToCurrentJsonObject(PathMetadata currentPathMetaData) {
        String field = currentPathMetaData.getFieldName();
        Object propertyValue = properties.get(propertyKey);
        if (currentPathMetaData.isArrayField()) {
            addFieldToArray(currentPathMetaData, propertyValue);
        } else {
            if (currentObjectJsonType.containsField(field) && isArrayJson(currentObjectJsonType.getJsonTypeByFieldName(field))) {
                AbstractJsonType abstractJsonType = resolvePrimitiveTypeAndReturn(propertyValue, primitiveResolvers);
                ArrayJsonType currentArrayInObject = currentObjectJsonType.getJsonArray(field);
                if (isArrayJson(abstractJsonType)) {
                    ArrayJsonType newArray = (ArrayJsonType) abstractJsonType;
                    List<AbstractJsonType> abstractJsonTypes = newArray.convertToListWithoutRealNull();
                    for(int i = 0; i < abstractJsonTypes.size(); i++) {
                        currentArrayInObject.addElement(i, abstractJsonTypes.get(i));
                    }
                } else {
                    throw new CannotOverrideFieldException(currentPathMetaData.getCurrentFullPath(), currentArrayInObject, propertyKey);
                }
            } else {
                currentObjectJsonType.addField(field, resolvePrimitiveTypeAndReturn(propertyValue, primitiveResolvers));
            }
        }
    }

    public Object getResolvedObject(String propertyValue) {
        Object object = null;
        for (PrimitiveJsonTypeResolver primitiveResolver : primitiveResolvers) {
            if (object == null) {
                object = primitiveResolver.returnConvertedValueForClearedText(this, propertyValue);
            }
        }
        return object;
    }

    public AbstractJsonType resolvePrimitiveTypeAndReturn(Object propertyValue, List<PrimitiveJsonTypeResolver> resolvers) {
        if (propertyValue == null) {
            return NULL_OBJECT;
        }
        Class<?> propertyValueClass = propertyValue.getClass();
        for (PrimitiveJsonTypeResolver<?> resolver : resolvers) {
            if (resolver.canResolveThisObject(propertyValueClass)) {
                return resolver.returnJsonType(this, propertyValue);
            }
        }
        throw new ParsePropertiesException(format(CANNOT_FIND_TYPE_RESOLVER_MSG, propertyValue.getClass()));
    }

    public AbstractJsonType resolvePrimitiveTypeAndReturn(Object propertyValue) {
        return resolvePrimitiveTypeAndReturn(propertyValue, primitiveResolvers);
    }

    protected void addFieldToArray(PathMetadata currentPathMetaData, Object propertyValue) {
        if (arrayWithGivenFieldNameExist(currentPathMetaData.getFieldName())) {
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
        addElementToArray(propertyValue, currentPathMetaData.getPropertyArrayHelper(), arrayJsonTypeObject);
        currentObjectJsonType.addField(currentPathMetaData.getFieldName(), arrayJsonTypeObject);
    }

    private void fetchArrayAndAddElement(PathMetadata currentPathMetaData, Object propertyValue) {
        PropertyArrayHelper propertyArrayHelper = currentPathMetaData.getPropertyArrayHelper();
        ArrayJsonType arrayJsonType = getArrayJsonWhenIsValid(currentPathMetaData);
        checkThatGivenArrayHasExpectedStructure(propertyKey, currentPathMetaData, arrayJsonType);
        addElementToArray(propertyValue, propertyArrayHelper, arrayJsonType);
    }

    private void addElementToArray(Object propertyValue, PropertyArrayHelper propertyArrayHelper, ArrayJsonType arrayJsonTypeObject) {
        arrayJsonTypeObject.addElement(propertyArrayHelper, resolvePrimitiveTypeAndReturn(propertyValue));
    }

}
