package pl.jalokim.propertiestojson.traverse;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJson;
import pl.jalokim.propertiestojson.object.ObjectJson;
import pl.jalokim.propertiestojson.traverse.transfer.DataForTraverse;

import java.util.Map;

import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.checkEalierWasArrayJson;

public abstract class TraverseAlgorithm {

    protected Map<String, String> properties;
    protected String propertiesKey;
    protected ObjectJson currentObjectJson;


    protected ArrayJson getArrayJsonWhenIsValid(String field) {
        AbstractJsonType jsonType = currentObjectJson.getJsonTypeByFieldName(field);
        checkEalierWasArrayJson(propertiesKey,field, jsonType);
        return (ArrayJson) jsonType;
    }

    public abstract ObjectJson traverse(String field);

    public final ObjectJson traverseOnObjectAndInitByField(DataForTraverse dataForTraverse){
        properties = dataForTraverse.getProperties();
        propertiesKey = dataForTraverse.getPropertiesKey();
        currentObjectJson = dataForTraverse.getCurrentObjectJson();
        return traverse(dataForTraverse.getField());
    }
}
