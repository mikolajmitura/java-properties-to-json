package pl.jalokim.propertiestojson.util;

import org.assertj.core.util.VisibleForTesting;
import pl.jalokim.propertiestojson.JsonObjectsTraverseResolver;
import pl.jalokim.propertiestojson.helper.PropertyKeysPickup;
import pl.jalokim.propertiestojson.object.ObjectJson;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static pl.jalokim.propertiestojson.Constants.DOT;


public class PropertiesToJsonParser {

    private static PropertyKeysPickup propertyKeysPickup = new PropertyKeysPickup();

    /**
     * generate String with Json by given InputStream
     * @param inputStream InputStream with properties
     * @return simple String with json
     * @throws IOException when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(InputStream inputStream) throws IOException, ParsePropertiesException {
        Properties properties = new Properties();
        properties.load(inputStream);
        return parseToJson(properties);
    }


    /**
     * generate String with Json by given Java Properties
     * @param properties Java Properties
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(Properties properties) throws ParsePropertiesException{
        Map<String, String> map = new HashMap<>();
        properties.stringPropertyNames().stream().forEach((name)->map.put(name,properties.getProperty(name)));
        return parseToJson(map);
    }


    /**
     * generate String with Json by given Map&lt;String,String&gt;
     * @param properties Java Map with properties
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(Map<String, String> properties) throws ParsePropertiesException {
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
