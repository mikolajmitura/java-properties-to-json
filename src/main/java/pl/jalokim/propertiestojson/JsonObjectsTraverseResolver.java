package pl.jalokim.propertiestojson;

import pl.jalokim.propertiestojson.object.*;
import pl.jalokim.propertiestojson.resolvers.*;
import pl.jalokim.propertiestojson.resolvers.transfer.DataForResolve;

import java.util.Map;

public class JsonObjectsTraverseResolver {

    private static final String NUMBER_PATTERN = "([1-9]\\d*)|0";
    public static final String INDEXES_PATTERN = "\\s*(\\[\\s*((" + NUMBER_PATTERN + ")|\\*)\\s*]\\s*)+";

    private static final String WORD_PATTERN = "(.)*";
    private final Map<AlgorithmType, JsonTypeResolver> algorithms;
    private Map<String, Object> properties;
    private String propertiesKey;
    private String[] fields;
    private ObjectJsonType currentObjectJsonType;

    public JsonObjectsTraverseResolver(Map<AlgorithmType, JsonTypeResolver> algorithms,
                                       Map<String, Object> properties, String propertiesKey,
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
        return field.matches(WORD_PATTERN + INDEXES_PATTERN);
    }

    private boolean isPrimitiveField(int index) {
        int lastIndex = fields.length - 1;
        return index == lastIndex;
    }

}
