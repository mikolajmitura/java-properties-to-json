package pl.jalokim.propertiestojson.resolvers;

import pl.jalokim.propertiestojson.JsonObjectFieldsValidator;
import pl.jalokim.propertiestojson.PropertyArrayHelper;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.util.List;

import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.checkThatArrayElementIsPrimitiveType;
import static pl.jalokim.propertiestojson.JsonObjectsTraverseResolver.isArrayField;

public class PrimitiveJsonTypesResolver extends JsonTypeResolver {

    private final List<PrimitiveJsonTypeResolver> primitiveResolvers;

    public PrimitiveJsonTypesResolver(List<PrimitiveJsonTypeResolver> primitiveResolvers) {
        this.primitiveResolvers = primitiveResolvers;
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
        String propertyValue = properties.get(propertiesKey);
        if (isArrayField(field)) {
            addFieldToArray(field, propertyValue);
        } else {
            currentObjectJsonType.addField(field, resolvePrimitiveTypeAndReturn(propertyValue, primitiveResolvers));
        }
    }

    private AbstractJsonType resolvePrimitiveTypeAndReturn(String propertyValue, List<PrimitiveJsonTypeResolver> resolvers) {
        for (PrimitiveJsonTypeResolver resolver : resolvers) {
            AbstractJsonType abstractJsonType = resolver.returnJsonTypeWhenCanBeParsed(this, propertyValue);
            if (abstractJsonType != null){
                return abstractJsonType;
            }
        }
        throw new ParsePropertiesException("Cannot find valid JSON type");
    }

    public AbstractJsonType resolvePrimitiveTypeAndReturn(String propertyValue) {
        return resolvePrimitiveTypeAndReturn(propertyValue, primitiveResolvers);
    }

    protected void addFieldToArray(String field, String propertyValue) {
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

    private void createArrayAndAddElement(String field, String propertyValue, PropertyArrayHelper propertyArrayHelper) {
        ArrayJsonType arrayJsonTypeObject = new ArrayJsonType();
        addElementToArray(propertyValue, propertyArrayHelper, arrayJsonTypeObject);
        currentObjectJsonType.addField(field, arrayJsonTypeObject);
    }

    private void fetchArrayAndAddElement(String field, String propertyValue, PropertyArrayHelper propertyArrayHelper) {
        ArrayJsonType arrayJsonType = getArrayJsonWhenIsValid(field);
        checkThatArrayElementIsPrimitiveType(propertiesKey, field, arrayJsonType, propertyArrayHelper.getIndexArray());
        addElementToArray(propertyValue, propertyArrayHelper, arrayJsonType);
    }

    private void addElementToArray(String propertyValue, PropertyArrayHelper propertyArrayHelper, ArrayJsonType arrayJsonTypeObject) {
        arrayJsonTypeObject.addElement(propertyArrayHelper.getIndexArray(), resolvePrimitiveTypeAndReturn(propertyValue));
    }

}
