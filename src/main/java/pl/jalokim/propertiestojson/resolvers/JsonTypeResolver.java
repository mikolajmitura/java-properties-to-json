package pl.jalokim.propertiestojson.resolvers;

import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.checkEarlierWasArrayJson;

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

    /**
     * Don't remove this it will be used by others in own custom type resolvers.
     * @see <a href="https://github.com/mikolajmitura/java-properties-to-json/issues/34">issue 34</a>
     * @return propertiesKey
     */
    // TODO remove it and write in documentation about it
    public String getPropertiesKey() {
		return propertyKey;
	}

	protected ArrayJsonType getArrayJsonWhenIsValid(PathMetadata currentPathMetaData) {
        AbstractJsonType jsonType = currentObjectJsonType.getJsonTypeByFieldName(currentPathMetaData.getFieldName());
        checkEarlierWasArrayJson(propertyKey, currentPathMetaData, jsonType);
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
