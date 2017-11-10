package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.JsonObjectFieldsValidator;
import pl.jalokim.propertiestojson.PropertyArrayHelper;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.BooleanJsonType;
import pl.jalokim.propertiestojson.object.DoubleJsonType;
import pl.jalokim.propertiestojson.object.IntegerJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.object.StringJsonType;
import pl.jalokim.propertiestojson.resolvers.JsonTypeResolver;

import java.util.ArrayList;
import java.util.List;

import static pl.jalokim.propertiestojson.Constants.SIMPLE_ARRAY_DELIMETER;
import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.checkIsListOnlyForPrimitive;
import static pl.jalokim.propertiestojson.JsonObjectsTraverseResolver.isArrayField;

public class PrimitiveJsonTypesResolver extends JsonTypeResolver {

    private List<PrimitiveJsonTypeResolver> resolvers = new ArrayList<>();

    public PrimitiveJsonTypesResolver(){
        resolvers.add(new PrimitiveArrayJsonType());
        resolvers.add();
        resolvers.add();
        resolvers.add();
        resolvers.add();
        resolvers.add();
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
        if (isSimpleArray(propertyValue)) {
            currentObjectJsonType.addField(field, new ArrayJsonType(propertyValue.split(SIMPLE_ARRAY_DELIMETER)));
        } else if (isArrayField(field)) {
            addFieldToArray(field, propertyValue);
        } else if (isDoubleNumber(propertyValue)) {
            currentObjectJsonType.addField(field, new DoubleJsonType(getDoubleNumber(propertyValue)));
        } else if (isIntegerNumber(propertyValue)) {
            currentObjectJsonType.addField(field, new IntegerJsonType(getIntegerNumber(propertyValue)));
        } else if (isBoolean(propertyValue)) {
            currentObjectJsonType.addField(field, new BooleanJsonType(getBoolean(propertyValue)));
        } else {
            currentObjectJsonType.addField(field, new StringJsonType(propertyValue));
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

    private boolean arrayWithGivenFieldNameExist(String field) {
        return currentObjectJsonType.containsField(field);
    }

}
