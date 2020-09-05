package pl.jalokim.propertiestojson.resolvers;


import java.util.Map;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.path.PathMetadata;
import pl.jalokim.propertiestojson.resolvers.transfer.DataForResolve;

public abstract class JsonTypeResolver {

    protected Map<String, Object> properties;
    protected String propertyKey;
    protected ObjectJsonType currentObjectJsonType;

    protected ArrayJsonType getArrayJsonWhenIsValid(PathMetadata currentPathMetaData) {
        AbstractJsonType jsonType = currentObjectJsonType.getField(currentPathMetaData.getFieldName());
        return (ArrayJsonType) jsonType;
    }

    public abstract ObjectJsonType traverse(PathMetadata currentPathMetaData);

    public final ObjectJsonType traverseOnObjectAndInitByField(DataForResolve dataForResolve) {
        properties = dataForResolve.getProperties();
        propertyKey = dataForResolve.getPropertiesKey();
        currentObjectJsonType = dataForResolve.getCurrentObjectJsonType();
        return traverse(dataForResolve.getCurrentPathMetaData());
    }
}
