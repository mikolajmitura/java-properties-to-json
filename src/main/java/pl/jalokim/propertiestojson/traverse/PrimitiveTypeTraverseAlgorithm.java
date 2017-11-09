package pl.jalokim.propertiestojson.traverse;

import pl.jalokim.propertiestojson.JsonObjectFieldsValidator;
import pl.jalokim.propertiestojson.PropertyArrayHelper;
import pl.jalokim.propertiestojson.object.ArrayJson;
import pl.jalokim.propertiestojson.object.BooleanJson;
import pl.jalokim.propertiestojson.object.DoubleNumberJson;
import pl.jalokim.propertiestojson.object.IntegerNumberJson;
import pl.jalokim.propertiestojson.object.ObjectJson;
import pl.jalokim.propertiestojson.object.StringJson;

import static pl.jalokim.propertiestojson.Constants.SIMPLE_ARRAY_DELIMETER;
import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.checkIsListOnlyForPrimitive;
import static pl.jalokim.propertiestojson.JsonObjectsTraverseResolver.isArrayField;
import static pl.jalokim.propertiestojson.util.NumberUtil.getBoolean;
import static pl.jalokim.propertiestojson.util.NumberUtil.getDoubleNumber;
import static pl.jalokim.propertiestojson.util.NumberUtil.getIntegerNumber;
import static pl.jalokim.propertiestojson.util.NumberUtil.isBoolean;
import static pl.jalokim.propertiestojson.util.NumberUtil.isDoubleNumber;
import static pl.jalokim.propertiestojson.util.NumberUtil.isIntegerNumber;

public class PrimitiveTypeTraverseAlgorithm extends TraverseAlgorithm {


    @Override
    public ObjectJson traverse(String field) {
        addPrimitiveFieldWhenIsValid(field);
        return null;
    }

    private void addPrimitiveFieldWhenIsValid(String field) {
        JsonObjectFieldsValidator.checkEarlierWasJsonString(currentObjectJson, field, propertiesKey);
        addPrimitiveFieldToCurrentJsonObject(field);
    }

    private void addPrimitiveFieldToCurrentJsonObject(String field) {
        String propertyValue = properties.get(propertiesKey);
        if (isSimpleArray(propertyValue)) {
            currentObjectJson.addField(field, new ArrayJson(propertyValue.split(SIMPLE_ARRAY_DELIMETER)));
        } else if (isArrayField(field)) {
            addFieldToArray(field, propertyValue);
        } else if (isDoubleNumber(propertyValue)) {
            currentObjectJson.addField(field, new DoubleNumberJson(getDoubleNumber(propertyValue)));
        } else if (isIntegerNumber(propertyValue)) {
            currentObjectJson.addField(field, new IntegerNumberJson(getIntegerNumber(propertyValue)));
        } else if (isBoolean(propertyValue)) {
            currentObjectJson.addField(field, new BooleanJson(getBoolean(propertyValue)));
        } else {
            currentObjectJson.addField(field, new StringJson(propertyValue));
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
        ArrayJson arrayJsonObject = new ArrayJson();
        arrayJsonObject.addElement(propertyArrayHelper.getIndexArray(), new StringJson(propertyValue));
        currentObjectJson.addField(field, arrayJsonObject);
    }

    private void fetchArrayAndAddElement(String field, String propertyValue, PropertyArrayHelper propertyArrayHelper) {
        ArrayJson arrayJson = getArrayJsonWhenIsValid(field);
        checkIsListOnlyForPrimitive(propertiesKey, field, arrayJson, propertyArrayHelper.getIndexArray());
        arrayJson.addElement(propertyArrayHelper.getIndexArray(), new StringJson(propertyValue));
    }

    private boolean arrayWithGivenFieldNameExist(String field) {
        return currentObjectJson.containsField(field);
    }

    private boolean isSimpleArray(String propValue) {
        return propValue.contains(SIMPLE_ARRAY_DELIMETER);
    }

}
