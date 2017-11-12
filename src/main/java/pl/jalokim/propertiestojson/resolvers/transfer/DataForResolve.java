package pl.jalokim.propertiestojson.resolvers.transfer;

import pl.jalokim.propertiestojson.object.ObjectJsonType;

import java.util.Map;

public class DataForResolve {

    private final Map<String, String> properties;
    private final String propertiesKey;
    private final ObjectJsonType currentObjectJsonType;
    private final String field;

    public DataForResolve(Map<String, String> properties, String propertiesKey, ObjectJsonType currentObjectJsonType, String field) {
        this.properties = properties;
        this.propertiesKey = propertiesKey;
        this.currentObjectJsonType = currentObjectJsonType;
        this.field = field;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public String getPropertiesKey() {
        return propertiesKey;
    }

    public ObjectJsonType getCurrentObjectJsonType() {
        return currentObjectJsonType;
    }

    public String getField() {
        return field;
    }
}
