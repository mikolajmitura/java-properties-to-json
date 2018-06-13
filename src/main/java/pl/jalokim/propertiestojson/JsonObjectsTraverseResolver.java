package pl.jalokim.propertiestojson;

import pl.jalokim.propertiestojson.object.*;
import pl.jalokim.propertiestojson.resolvers.*;
import pl.jalokim.propertiestojson.resolvers.transfer.DataForResolve;

import java.util.Map;

import static pl.jalokim.propertiestojson.Constants.ARRAY_END_SIGN;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;


public class JsonObjectsTraverseResolver {

    private final Map<AlgorithmType, JsonTypeResolver> algorithms;
    private Map<String, String> properties;
    private String propertiesKey;
    private String[] fields;
    private ObjectJsonType currentObjectJsonType;

    public JsonObjectsTraverseResolver(Map<AlgorithmType, JsonTypeResolver> algorithms,
                                       Map<String, String> properties, String propertiesKey,
                                       String[] fields, ObjectJsonType coreObjectJsonType) {
        this.properties = properties;
        this.propertiesKey = propertiesKey;
        this.fields = fields;
        this.currentObjectJsonType = coreObjectJsonType;
        this.algorithms = algorithms;
    }

    public void initializeFieldsInJson() {
        for (int index = 0; index < fields.length; index++) {
            String field = fields[index];
            DataForResolve dataForResolve = new DataForResolve(properties, propertiesKey, currentObjectJsonType, field);
            currentObjectJsonType = algorithms.get(resolveAlgorithm(index, field)).traverseOnObjectAndInitByField(dataForResolve);
        }
    }

    private AlgorithmType resolveAlgorithm(int index, String field) {
        if (isPrimitiveField(index)) {
            return AlgorithmType.PRIMITIVE;
        }
        if (isArrayField(field)) {
            return AlgorithmType.ARRAY;
        }
        return AlgorithmType.OBJECT;
    }

    public static boolean isArrayField(String field) {
        return field.contains(ARRAY_START_SIGN) && field.contains(ARRAY_END_SIGN);
    }

    private boolean isPrimitiveField(int index) {
        int lastIndex = fields.length - 1;
        return index == lastIndex;
    }

}
