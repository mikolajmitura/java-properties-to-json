package pl.jalokim.propertiestojson.resolvers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.path.PathMetadata;
import pl.jalokim.propertiestojson.resolvers.transfer.DataForResolve;

@SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
public abstract class JsonTypeResolver {

    protected Map<String, Object> properties;
    protected String propertyKey;
    protected ObjectJsonType currentObjectJsonType;

    protected ArrayJsonType getArrayJsonWhenIsValid(PathMetadata currentPathMetaData) {
        return (ArrayJsonType) currentObjectJsonType.getField(currentPathMetaData.getFieldName());
    }

    public abstract ObjectJsonType traverse(PathMetadata currentPathMetaData);

    public final ObjectJsonType traverseOnObjectAndInitByField(DataForResolve dataForResolve) {
        properties = dataForResolve.getProperties();
        propertyKey = dataForResolve.getPropertiesKey();
        currentObjectJsonType = dataForResolve.getCurrentObjectJsonType();
        return traverse(dataForResolve.getCurrentPathMetaData());
    }
}
