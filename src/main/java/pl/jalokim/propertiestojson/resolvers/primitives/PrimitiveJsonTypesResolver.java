package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.JsonObjectFieldsValidator;
import pl.jalokim.propertiestojson.PropertyArrayHelper;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.object.StringJsonType;
import pl.jalokim.propertiestojson.resolvers.JsonTypeResolver;

import java.util.ArrayList;
import java.util.List;

import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.checkIsListOnlyForPrimitive;
import static pl.jalokim.propertiestojson.JsonObjectsTraverseResolver.isArrayField;

public class PrimitiveJsonTypesResolver extends JsonTypeResolver {

    private List<PrimitiveJsonTypeResolver> resolvers = new ArrayList<>();

    public PrimitiveJsonTypesResolver(){
        resolvers.add(new PrimitiveArrayJsonType());
        resolvers.add(new DoubleJsonTypeResolver());
        resolvers.add(new IntegerJsonTypeResolver());
        resolvers.add(new BooleanJsonTypeResolver());
        resolvers.add(new StringJsonTypeResolver());
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
            resolvePrimitiveType(field, propertyValue);
        }
    }

    private void resolvePrimitiveType(String field, String propertyValue) {
        for (PrimitiveJsonTypeResolver resolver : resolvers) {
            AbstractJsonType abstractJsonType = resolver.returnPrimitiveJsonTypeWhenIsGivenType(propertyValue);
            if (abstractJsonType != null){
                currentObjectJsonType.addField(field, abstractJsonType);
                break;
            }
        }
    }

    protected void addFieldToArray(String field, String propertyValue) {
        PropertyArrayHelper propertyArrayHelper = new PropertyArrayHelper(field);
        field = propertyArrayHelper.getArrayfieldName();
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
        arrayJsonTypeObject.addElement(propertyArrayHelper.getIndexArray(), new StringJsonType(propertyValue));
        currentObjectJsonType.addField(field, arrayJsonTypeObject);
    }

    private void fetchArrayAndAddElement(String field, String propertyValue, PropertyArrayHelper propertyArrayHelper) {
        ArrayJsonType arrayJsonType = getArrayJsonWhenIsValid(field);
        checkIsListOnlyForPrimitive(propertiesKey, field, arrayJsonType, propertyArrayHelper.getIndexArray());
        arrayJsonType.addElement(propertyArrayHelper.getIndexArray(), new StringJsonType(propertyValue));
    }

}
