package pl.jalokim.propertiestojson.traverse.transfer;

import pl.jalokim.propertiestojson.object.ObjectJson;

import java.util.Map;

public class DataForTraverse {

    private final Map<String, String> properties;
    private final String propertiesKey;
    private final ObjectJson currentObjectJson;
    private final String field;

    public DataForTraverse(Map<String, String> properties, String propertiesKey, ObjectJson currentObjectJson, String field) {
        this.properties = properties;
        this.propertiesKey = propertiesKey;
        this.currentObjectJson = currentObjectJson;
        this.field = field;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public String getPropertiesKey() {
        return propertiesKey;
    }

    public ObjectJson getCurrentObjectJson() {
        return currentObjectJson;
    }

    public String getField() {
        return field;
    }
}
