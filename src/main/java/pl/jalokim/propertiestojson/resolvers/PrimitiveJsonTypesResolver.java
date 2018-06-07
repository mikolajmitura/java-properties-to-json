package pl.jalokim.propertiestojson.resolvers;

import pl.jalokim.propertiestojson.JsonObjectFieldsValidator;
import pl.jalokim.propertiestojson.PropertyArrayHelper;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.resolvers.primitives.BooleanJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.DoubleJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.IntegerJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveArrayJsonType;
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.StringJsonTypeResolver;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.util.ArrayList;
import java.util.List;

import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.checkThatArrayElementIsPrimitiveType;
import static pl.jalokim.propertiestojson.JsonObjectsTraverseResolver.isArrayField;

public class PrimitiveJsonTypesResolver extends JsonTypeResolver {

    private List<PrimitiveJsonTypeResolver> resolvers = new ArrayList<>();
    private static List<PrimitiveJsonTypeResolver> primitiveResolvers = new ArrayList<>();

    static {
        primitiveResolvers.add(new DoubleJsonTypeResolver());
        primitiveResolvers.add(new IntegerJsonTypeResolver());
        primitiveResolvers.add(new BooleanJsonTypeResolver());
        primitiveResolvers.add(new StringJsonTypeResolver());
    }

    public PrimitiveJsonTypesResolver(boolean switchOnParseArrays) {
        super();
        if (switchOnParseArrays) {
            resolvers.add(new PrimitiveArrayJsonType());
        }
        resolvers.addAll(primitiveResolvers);
    }

    @Override
    public ObjectJsonType traverse(String field) {
        addPrimitiveFieldWhenIsValid(field);
        return null;
    }

    private void addPrimitiveFieldWhenIsValid(String field) {
        JsonObjectFieldsValidator.checkEarlierWasJsonString(currentObjectJsonType, field, propertiesKey);
        addPrimitiveFieldToCurrentJsonObject(field);
    }

    private void addPrimitiveFieldToCurrentJsonObject(String field) {
        String propertyValue = properties.get(propertiesKey);
        if (isArrayField(field)) {
            addFieldToArray(field, propertyValue);
        } else {
            currentObjectJsonType.addField(field, resolvePrimitiveTypeAndReturn(propertyValue, resolvers));
        }
    }

    private static AbstractJsonType resolvePrimitiveTypeAndReturn(String propertyValue, List<PrimitiveJsonTypeResolver> resolvers) {
        for (PrimitiveJsonTypeResolver resolver : resolvers) {
            AbstractJsonType abstractJsonType = resolver.returnPrimitiveJsonTypeWhenIsGivenType(propertyValue);
            if (abstractJsonType != null){
                return abstractJsonType;
            }
        }
        throw new ParsePropertiesException("Cannot find valid JSON type");
    }

    public static AbstractJsonType resolvePrimitiveTypeAndReturn(String propertyValue) {
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
