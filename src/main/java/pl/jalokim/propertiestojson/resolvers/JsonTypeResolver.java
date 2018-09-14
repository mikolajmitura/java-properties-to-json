package pl.jalokim.propertiestojson.resolvers;

import static pl.jalokim.propertiestojson.JsonObjectFieldsValidator.checkEalierWasArrayJson;

import java.util.Map;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.resolvers.transfer.DataForResolve;

public abstract class JsonTypeResolver {

    protected Map<String, String> properties;
    protected String propertiesKey;
    protected ObjectJsonType currentObjectJsonType;

    /**
     * Don't remove this it will be used by others in own custom type resolvers.
     * @see <a href="https://github.com/mikolajmitura/java-properties-to-json/issues/34">issue 34</a>
     * @return propertiesKey
     */
    public String getPropertiesKey() {
		return propertiesKey;
	}

	protected ArrayJsonType getArrayJsonWhenIsValid(String field) {
        AbstractJsonType jsonType = currentObjectJsonType.getJsonTypeByFieldName(field);
        checkEalierWasArrayJson(propertiesKey, field, jsonType);
        return (ArrayJsonType) jsonType;
    }

    public abstract ObjectJsonType traverse(String field);

    public final ObjectJsonType traverseOnObjectAndInitByField(DataForResolve dataForResolve) {
        properties = dataForResolve.getProperties();
        propertiesKey = dataForResolve.getPropertiesKey();
        currentObjectJsonType = dataForResolve.getCurrentObjectJsonType();
        return traverse(dataForResolve.getField());
    }
}
