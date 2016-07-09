package pl.jalokim.propertiestojson.util;

import org.assertj.core.util.VisibleForTesting;
import pl.jalokim.propertiestojson.JsonObjectsTraverseResolver;
import pl.jalokim.propertiestojson.helper.PropertyKeysPickup;
import pl.jalokim.propertiestojson.object.ObjectJson;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;


import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;
import static pl.jalokim.propertiestojson.Constants.DOT;


public class PropertiesToJsonParser {

    private static PropertyKeysPickup propertyKeysPickup = new PropertyKeysPickup();

    /**
     * generate Json by given InputStream
     * @param inputStream InputStream with properties
     * @return simple String with json
     * @throws IOException when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(InputStream inputStream) throws IOException, ParsePropertiesException {
        return parseToJson(inputStreamToProperties(inputStream));
    }

    /**
     * generate Json by given Java Properties
     * @param properties Java Properties
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(Properties properties) throws ParsePropertiesException{
        return parseToJson(propertiesToMap(properties));
    }

    /**
     * generate Json by given Map&lt;String,String&gt;
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

    /**
     * generate Json by given Map&lt;String,String&gt; and given filter
     *
     * @param properties Java Map with properties
     * @param includeDomainKeys domain head keys which should be parsed to json <br>
     *                          example properties:<br>
     *                          object1.field1=value1<br>
     *                          object1.field2=value2<br>
     *                          someObject2.field2=value3<br>
     *                          filter "object1"<br>
     *                          will parse only nested domain for "object1"<br>
     *
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(Map<String, String> properties, String... includeDomainKeys) throws ParsePropertiesException {
        Map<String, String> filteredProperties = new HashMap<>();
        properties.keySet().forEach((key)->Arrays.stream(includeDomainKeys).forEach((requiredKey)->checkKey(properties, filteredProperties, key, requiredKey)));
        return parseToJson(filteredProperties);
    }

    /**
     * generate Json by given Java Properties and given filter
     * @param includeDomainKeys domain head keys which should be parsed to json <br>
     *                          example properties:<br>
     *                          object1.field1=value1<br>
     *                          object1.field2=value2<br>
     *                          someObject2.field2=value3<br>
     *                          filter "object1"<br>
     *                          will parse only nested domain for "object1"<br>
     *
     * @param includeDomainKeys domain head keys which should be parsed to json
     * @return Simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(Properties properties, String... includeDomainKeys)  throws ParsePropertiesException{
        return parseToJson(propertiesToMap(properties), includeDomainKeys);
    }

    /**
     *
     * @param inputStream InputStream with properties
     * @param includeDomainKeys domain head keys which should be parsed to json <br>
     *                          example properties:<br>
     *                          object1.field1=value1<br>
     *                          object1.field2=value2<br>
     *                          someObject2.field2=value3<br>
     *                          filter "object1"<br>
     *                          will parse only nested domain for "object1"<br>
     * @return simple String with json
     * @throws IOException when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(InputStream inputStream, String... includeDomainKeys) throws IOException, ParsePropertiesException{
        return parseToJson(inputStreamToProperties(inputStream),includeDomainKeys);
    }

    private static void checkKey(Map<String, String> properties, Map<String, String> filteredProperties, String key, String requiredKey) {
        if (key.equals(requiredKey) || ( key.startsWith(requiredKey) && keyIsCompatibleWithRequiredKey(requiredKey, key))){
            filteredProperties.put(key, properties.get(key));
        }
    }

    public static boolean keyIsCompatibleWithRequiredKey(String requiredKey, String key){
        String testedChar = key.substring(requiredKey.length(), requiredKey.length() + 1);
        if (testedChar.equals(ARRAY_START_SIGN) || testedChar.equals(DOT)){
            return true;
        }
        return false;
    }

    private static Properties inputStreamToProperties(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }

    private static void addFieldsToJsonObject(Map<String, String> properties, ObjectJson coreObjectJson, String propertiesKey) {
        String[] fields = propertiesKey.split(DOT);
        new JsonObjectsTraverseResolver(properties, propertiesKey, fields, coreObjectJson).initializeFieldsInJson();
    }

    private static List<String> getAllKeysFromProperties(Map<String, String> properties) {
        return propertyKeysPickup.getAllKeysFromProperties(properties);
    }


    private static Map<String, String> propertiesToMap(Properties properties) {
        Map<String, String> map = new HashMap<>();
        properties.stringPropertyNames().stream().forEach((name)->map.put(name,properties.getProperty(name)));
        return map;
    }

    @VisibleForTesting
    protected static void setPropertyKeysPickup(PropertyKeysPickup propertyKeysPickup){
        PropertiesToJsonParser.propertyKeysPickup = propertyKeysPickup;
    }


}
