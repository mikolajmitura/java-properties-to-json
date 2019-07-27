package pl.jalokim.propertiestojson.resolvers;

import com.google.common.collect.ImmutableList;
import pl.jalokim.propertiestojson.JsonObjectFieldsValidator;
import pl.jalokim.propertiestojson.PropertyArrayHelper;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.util.List;

import static java.lang.String.format;
import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.checkThatGivenArrayHasExpectedStructure;
import static pl.jalokim.propertiestojson.JsonObjectsTraverseResolver.isArrayField;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.CANNOT_FIND_TYPE_RESOLVER_MSG;

public class PrimitiveJsonTypesResolver extends JsonTypeResolver {

    private final List<PrimitiveJsonTypeResolver> primitiveResolvers;

    public PrimitiveJsonTypesResolver(List<PrimitiveJsonTypeResolver> primitiveResolvers) {
        this.primitiveResolvers = ImmutableList.copyOf(primitiveResolvers);
    }

    @Override
    public ObjectJsonType traverse(String field) {
        addPrimitiveFieldWhenIsValid(field);
        return null;
    }

    private void addPrimitiveFieldWhenIsValid(String field) {
        JsonObjectFieldsValidator.checkEarlierWasJsonPrimitiveType(currentObjectJsonType, field, propertiesKey);
        addPrimitiveFieldToCurrentJsonObject(field);
    }

    private void addPrimitiveFieldToCurrentJsonObject(String field) {
        Object propertyValue = properties.get(propertiesKey);
        if (isArrayField(field)) {
            addFieldToArray(field, propertyValue);
        } else {
            currentObjectJsonType.addField(field, resolvePrimitiveTypeAndReturn(propertyValue, primitiveResolvers));
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

    private AbstractJsonType resolvePrimitiveTypeAndReturn(Object propertyValue, List<PrimitiveJsonTypeResolver> resolvers) {

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

    protected void addFieldToArray(String field, Object propertyValue) {
        PropertyArrayHelper propertyArrayHelper = new PropertyArrayHelper(field);
        field = propertyArrayHelper.getArrayFieldName();
        if (arrayWithGivenFieldNameExist(field)) {
            fetchArrayAndAddElement(field, propertyValue, propertyArrayHelper);
        } else {
            createArrayAndAddElement(field, propertyValue, propertyArrayHelper);
        }
    }

    private boolean arrayWithGivenFieldNameExist(String field) {
        return currentObjectJsonType.containsField(field);
    }

    private void createArrayAndAddElement(String field, Object propertyValue, PropertyArrayHelper propertyArrayHelper) {
        ArrayJsonType arrayJsonTypeObject = new ArrayJsonType();
        addElementToArray(propertyValue, propertyArrayHelper, arrayJsonTypeObject);
        currentObjectJsonType.addField(field, arrayJsonTypeObject);
    }

    private void fetchArrayAndAddElement(String field, Object propertyValue, PropertyArrayHelper propertyArrayHelper) {
        ArrayJsonType arrayJsonType = getArrayJsonWhenIsValid(field);
        checkThatGivenArrayHasExpectedStructure(propertiesKey, field, arrayJsonType, propertyArrayHelper);
        addElementToArray(propertyValue, propertyArrayHelper, arrayJsonType);
    }

    private void addElementToArray(Object propertyValue, PropertyArrayHelper propertyArrayHelper, ArrayJsonType arrayJsonTypeObject) {
        arrayJsonTypeObject.addElement(propertyArrayHelper, resolvePrimitiveTypeAndReturn(propertyValue));
    }

}
