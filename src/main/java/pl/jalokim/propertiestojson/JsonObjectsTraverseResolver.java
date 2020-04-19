package pl.jalokim.propertiestojson;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.object.SkipJsonField;
import pl.jalokim.propertiestojson.path.PathMetadata;
import pl.jalokim.propertiestojson.resolvers.JsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.transfer.DataForResolve;

import java.util.Map;

public class JsonObjectsTraverseResolver {

    private final Map<AlgorithmType, JsonTypeResolver> algorithms;
    private final PrimitiveJsonTypesResolver primitiveJsonTypesResolver;
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
        this.primitiveJsonTypesResolver = (PrimitiveJsonTypesResolver) algorithms.get(AlgorithmType.PRIMITIVE);
    }

    public void initializeFieldsInJson() {
        PathMetadata currentPathMetaData = rootPathMetaData;
        Object valueFromProperties = properties.get(currentPathMetaData.getOriginalPropertyKey());
        if (valueFromProperties instanceof SkipJsonField) {
            return;

        }
        AbstractJsonType resolverJsonObject = primitiveJsonTypesResolver.resolvePrimitiveTypeAndReturn(valueFromProperties, currentPathMetaData.getOriginalPropertyKey());
        if (resolverJsonObject instanceof SkipJsonField && !rootPathMetaData.getLeaf().isArrayField()) {
            return;
        } else {
            rootPathMetaData.getLeaf().setJsonValue(resolverJsonObject);
        }
        rootPathMetaData.getLeaf().setRawValue(properties.get(propertyKey));

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
