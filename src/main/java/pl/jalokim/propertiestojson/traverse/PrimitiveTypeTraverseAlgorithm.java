package pl.jalokim.propertiestojson.traverse;

import pl.jalokim.propertiestojson.JsonObjectFieldsValidator;
import pl.jalokim.propertiestojson.PropertyArrayHelper;
import pl.jalokim.propertiestojson.object.ArrayJson;
import pl.jalokim.propertiestojson.object.NumberJson;
import pl.jalokim.propertiestojson.object.ObjectJson;
import pl.jalokim.propertiestojson.object.StringJson;

import static pl.jalokim.propertiestojson.Constants.SIMPLE_ARRAY_DELIMETER;
import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.checkIsListOnlyForPrimitive;
import static pl.jalokim.propertiestojson.JsonObjectsTraverseResolver.isArrayField;
import static pl.jalokim.propertiestojson.util.NumberUtil.getNumber;
import static pl.jalokim.propertiestojson.util.NumberUtil.isNumber;

public class PrimitiveTypeTraverseAlgorithm extends TraverseAlgorithm{


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
        if (isSimpleArray(propertyValue)){
            currentObjectJson.addField(field, new ArrayJson(propertyValue.split(SIMPLE_ARRAY_DELIMETER)));
        } else if (isArrayField(field)){
            addFieldToArray(field, propertyValue);
        } else if (isNumber(propertyValue)) {
            currentObjectJson.addField(field, new NumberJson(getNumber(propertyValue)));
        } else {
            currentObjectJson.addField(field, new StringJson(propertyValue));
        }
    }

    protected void addFieldToArray(String field, String propertyValue) {
        PropertyArrayHelper propertyArrayHelper = new PropertyArrayHelper(field);
        field = propertyArrayHelper.getArrayfieldName();
        if (currentObjectJson.containsField(field)){
            ArrayJson arrayJson = getArrayJsonWhenIsValid(field);
            checkIsListOnlyForPrimitive( propertiesKey, field, arrayJson,propertyArrayHelper.getIndexArray());
            arrayJson.addElement(propertyArrayHelper.getIndexArray(),new StringJson(propertyValue));
        } else {
            ArrayJson arrayJsonObject = new ArrayJson();
            arrayJsonObject.addElement(propertyArrayHelper.getIndexArray(), new StringJson(propertyValue));
            currentObjectJson.addField(field, arrayJsonObject);
        }
    }

    private boolean isSimpleArray(String propValue) {
        return propValue.contains(SIMPLE_ARRAY_DELIMETER);
    }

}
