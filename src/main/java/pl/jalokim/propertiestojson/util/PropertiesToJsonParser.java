package pl.jalokim.propertiestojson.util;

import pl.jalokim.propertiestojson.JsonObjectsInitializer;
import pl.jalokim.propertiestojson.object.ObjectJson;


import java.util.Map;
import java.util.Set;

import static pl.jalokim.propertiestojson.Constants.DOT;


public class PropertiesToJsonParser {

    public static String parseToJson(Map<String, String> properties) {
        ObjectJson coreObjectJson = new ObjectJson();
        for (String propertiesKey : getAllKeysFromMap(properties)) {
            addFieldsToJsonObject(properties, coreObjectJson, propertiesKey);
        }
        return coreObjectJson.toStringJson();
    }

    private static void addFieldsToJsonObject(Map<String, String> properties, ObjectJson coreObjectJson, String propertiesKey) {
        String[] fields = propertiesKey.split(DOT);
        new JsonObjectsInitializer(properties, propertiesKey, fields, coreObjectJson).addFieldForCurrentJsonObject();
    }


    private static Set<String> getAllKeysFromMap(Map<String, String> properties) {
        return properties.keySet();
    }


}
