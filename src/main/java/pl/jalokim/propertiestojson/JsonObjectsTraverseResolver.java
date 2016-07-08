package pl.jalokim.propertiestojson;

import pl.jalokim.propertiestojson.object.*;
import pl.jalokim.propertiestojson.traverse.*;
import pl.jalokim.propertiestojson.traverse.transfer.DataForTraverse;

import java.util.HashMap;
import java.util.Map;

import static pl.jalokim.propertiestojson.Constants.ARRAY_END_SIGN;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;


public class JsonObjectsTraverseResolver {

    private Map<String, String> properties;
    private String propertiesKey;
    private String[] fields;
    private ObjectJson currentObjectJson;

    public JsonObjectsTraverseResolver(Map<String, String> properties, String propertiesKey, String[] fields, ObjectJson coreObjectJson) {
        this.properties = properties;
        this.propertiesKey = propertiesKey;
        this.fields = fields;
        this.currentObjectJson = coreObjectJson;
    }

    private static Map<Algorithm, TraverseAlgorithm> algorithms = new HashMap<>();
    static {
        algorithms.put(Algorithm.OBJECT, new ObjectJsonTypeTraverseAlgorithm());
        algorithms.put(Algorithm.PRIMITIVE, new PrimitiveTypeTraverseAlgorithm());
        algorithms.put(Algorithm.ARRAY, new ArrayJsonTypeTraverseAlgorithm());
    }

    public void initializeFieldsInJson() {
        for (int index = 0; index < fields.length; index++) {
            String field = fields[index];
            DataForTraverse dataForTraverse = new DataForTraverse(properties, propertiesKey, currentObjectJson, field);
            currentObjectJson = algorithms.get(resolveAlgorithm(index, field)).traverseOnObjectAndInitByField(dataForTraverse);
        }
    }

    private Algorithm resolveAlgorithm(int index, String field){
        if (isLastPartOfKey(index)){
           return  Algorithm.PRIMITIVE;
        }
        if (isArrayField(field)){
            return Algorithm.ARRAY;
        }
        return Algorithm.OBJECT;
    }

    public static boolean isArrayField(String field) {
        return field.contains(ARRAY_START_SIGN) && field.contains(ARRAY_END_SIGN);
    }

    private boolean isLastPartOfKey(int index) {
        int lastIndex = fields.length - 1;
        return index == lastIndex;
    }

}
