package pl.jalokim.propertiestojson.util;

import org.assertj.core.util.VisibleForTesting;
import pl.jalokim.propertiestojson.JsonObjectsTraverseResolver;
import pl.jalokim.propertiestojson.helper.PropertyKeysPickup;
import pl.jalokim.propertiestojson.object.ObjectJson;


import java.util.List;
import java.util.Map;

import static pl.jalokim.propertiestojson.Constants.DOT;


public class PropertiesToJsonParser {

    private static PropertyKeysPickup propertyKeysPickup = new PropertyKeysPickup();

    public static String parseToJson(Map<String, String> properties) {
        ObjectJson coreObjectJson = new ObjectJson();
        for (String propertiesKey : getAllKeysFromProperties(properties)) {
            addFieldsToJsonObject(properties, coreObjectJson, propertiesKey);
        }
        return coreObjectJson.toStringJson();
    }

    private static void addFieldsToJsonObject(Map<String, String> properties, ObjectJson coreObjectJson, String propertiesKey) {
        String[] fields = propertiesKey.split(DOT);
        new JsonObjectsTraverseResolver(properties, propertiesKey, fields, coreObjectJson).initializeFieldsInJson();
    }


    private static List<String> getAllKeysFromProperties(Map<String, String> properties) {
        return propertyKeysPickup.getAllKeysFromProperties(properties);
    }

    @VisibleForTesting
    protected static void setPropertyKeysPickup(PropertyKeysPickup propertyKeysPickup){
        PropertiesToJsonParser.propertyKeysPickup = propertyKeysPickup;
    }


}
