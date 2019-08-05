package pl.jalokim.propertiestojson;

import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.path.PathMetadata;
import pl.jalokim.propertiestojson.resolvers.JsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.transfer.DataForResolve;

import java.util.Map;

public class JsonObjectsTraverseResolver {

    private final Map<AlgorithmType, JsonTypeResolver> algorithms;
    private Map<String, Object> properties;
    private String propertyKey;
    private PathMetadata rootPathMetaData;
    private ObjectJsonType currentObjectJsonType;

    public JsonObjectsTraverseResolver(Map<AlgorithmType, JsonTypeResolver> algorithms,
                                       Map<String, Object> properties, String propertyKey,
                                       PathMetadata rootPathMetaData, ObjectJsonType coreObjectJsonType) {
        this.properties = properties;
        this.propertyKey = propertyKey;
        this.rootPathMetaData = rootPathMetaData;
        this.currentObjectJsonType = coreObjectJsonType;
        this.algorithms = algorithms;
    }

    public void initializeFieldsInJson() {
        PathMetadata currentPathMetaData = rootPathMetaData;
        while (currentPathMetaData != null) {
            DataForResolve dataForResolve = new DataForResolve(properties, propertyKey, currentObjectJsonType, currentPathMetaData);
            currentObjectJsonType = algorithms.get(resolveAlgorithm(currentPathMetaData))
                                              .traverseOnObjectAndInitByField(dataForResolve);
            currentPathMetaData = currentPathMetaData.getChild();
        }
    }

    private AlgorithmType resolveAlgorithm(PathMetadata currentPathMetaData) {
        if (isPrimitiveField(currentPathMetaData)) {
            return AlgorithmType.PRIMITIVE;
        }
        if (currentPathMetaData.isArrayField()) {
            return AlgorithmType.ARRAY;
        }
        return AlgorithmType.OBJECT;
    }


    private boolean isPrimitiveField(PathMetadata currentPathMetaData) {
        return currentPathMetaData.isLeaf();
    }

}
