package pl.jalokim.propertiestojson.util;

import pl.jalokim.propertiestojson.JsonObjectsTraverseResolver;
import pl.jalokim.propertiestojson.helper.PropertyKeysPickup;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;
import pl.jalokim.propertiestojson.util.exception.ReadInputException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;
import static pl.jalokim.propertiestojson.Constants.REGEX_DOT;


public class PropertiesToJsonParser {

    private static PropertyKeysPickup propertyKeysPickup = new PropertyKeysPickup();

    /**
     * Generate Json by given path to file with properties with only included domain keys.
     *
     * @param pathToFile        path to File
     * @param includeDomainKeys domain head keys which should be parsed to json <br>
     *                          example properties:<br>
     *                          object1.field1=value1<br>
     *                          object1.field2=value2<br>
     *                          someObject2.field2=value3<br>
     *                          filter "object1"<br>
     *                          will parse only nested domain for "object1"<br>
     * @return simple String with json
     * @throws ReadInputException       when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parsePropertiesFromFileToJson(String pathToFile, String... includeDomainKeys) throws ReadInputException, ParsePropertiesException {
        return parsePropertiesFromFileToJson(new File(pathToFile), includeDomainKeys);
    }

    /**
     * Generate Json by given path to file with properties.
     *
     * @param pathToFile path to File
     * @return simple String with json
     * @throws ReadInputException       when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parsePropertiesFromFileToJson(String pathToFile) throws ReadInputException, ParsePropertiesException {
        return parsePropertiesFromFileToJson(new File(pathToFile));
    }

    /**
     * Generate Json by given file with properties with only included domain keys.
     *
     * @param file              file with properties
     * @param includeDomainKeys domain head keys which should be parsed to json <br>
     *                          example properties:<br>
     *                          object1.field1=value1<br>
     *                          object1.field2=value2<br>
     *                          someObject2.field2=value3<br>
     *                          filter "object1"<br>
     *                          will parse only nested domain for "object1"<br>
     * @return simple String with json
     * @throws ReadInputException       when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parsePropertiesFromFileToJson(File file, String... includeDomainKeys) throws ReadInputException, ParsePropertiesException {
        try {
            InputStream targetStream = new FileInputStream(file);
            return parseToJson(targetStream, includeDomainKeys);
        } catch (FileNotFoundException e) {
            throw new ReadInputException(e);
        }
    }

    /**
     * Generate Json by given file with properties.
     *
     * @param file file with properties
     * @return simple String with json
     * @throws ReadInputException       when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parsePropertiesFromFileToJson(File file) throws ReadInputException, ParsePropertiesException {
        try {
            InputStream targetStream = new FileInputStream(file);
            return parseToJson(targetStream);
        } catch (FileNotFoundException e) {
            throw new ReadInputException(e);
        }
    }

    /**
     * generate Json by given InputStream and given filter.
     *
     * @param inputStream       InputStream with properties
     * @param includeDomainKeys domain head keys which should be parsed to json <br>
     *                          example properties:<br>
     *                          object1.field1=value1<br>
     *                          object1.field2=value2<br>
     *                          someObject2.field2=value3<br>
     *                          filter "object1"<br>
     *                          will parse only nested domain for "object1"<br>
     * @return simple String with json
     * @throws ReadInputException       when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(InputStream inputStream, String... includeDomainKeys) throws ReadInputException, ParsePropertiesException {
        return parseToJson(inputStreamToProperties(inputStream), includeDomainKeys);
    }

    /**
     * generate Json by given InputStream.
     *
     * @param inputStream InputStream with properties
     * @return simple String with json
     * @throws ReadInputException       when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(InputStream inputStream) throws ReadInputException, ParsePropertiesException {
        return parseToJson(inputStreamToProperties(inputStream));
    }

    /**
     * generate Json by given Java Properties
     *
     * @param properties Java Properties
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(Properties properties) throws ParsePropertiesException {
        return parseToJson(propertiesToMap(properties));
    }

    /**
     * generate Json by given Map&lt;String,String&gt;
     *
     * @param properties Java Map with properties
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(Map<String, String> properties) throws ParsePropertiesException {
        ObjectJsonType coreObjectJsonType = new ObjectJsonType();

        for (String propertiesKey : getAllKeysFromProperties(properties)) {
            addFieldsToJsonObject(properties, coreObjectJsonType, propertiesKey);
        }
        return coreObjectJsonType.toStringJson();
    }

    /**
     * generate Json by given Map&lt;String,String&gt; and given filter
     *
     * @param properties        Java Map with properties
     * @param includeDomainKeys domain head keys which should be parsed to json <br>
     *                          example properties:<br>
     *                          object1.field1=value1<br>
     *                          object1.field2=value2<br>
     *                          someObject2.field2=value3<br>
     *                          filter "object1"<br>
     *                          will parse only nested domain for "object1"<br>
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(Map<String, String> properties, String... includeDomainKeys) throws ParsePropertiesException {
        Map<String, String> filteredProperties = new HashMap<>();
        for (String key : properties.keySet()) {
            for (String requiredKey : includeDomainKeys) {
                checkKey(properties, filteredProperties, key, requiredKey);
            }
        }
        return parseToJson(filteredProperties);
    }

    /**
     * generate Json by given Java Properties and given filter
     *
     * @param properties        Java Properties
     * @param includeDomainKeys domain head keys which should be parsed to json <br>
     *                          example properties:<br>
     *                          object1.field1=value1<br>
     *                          object1.field2=value2<br>
     *                          someObject2.field2=value3<br>
     *                          filter "object1"<br>
     *                          will parse only nested domain for "object1"<br>
     * @param includeDomainKeys domain head keys which should be parsed to json
     * @return Simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public static String parseToJson(Properties properties, String... includeDomainKeys) throws ParsePropertiesException {
        return parseToJson(propertiesToMap(properties), includeDomainKeys);
    }


    private static void checkKey(Map<String, String> properties, Map<String, String> filteredProperties, String key, String requiredKey) {
        if (key.equals(requiredKey) || (key.startsWith(requiredKey) && keyIsCompatibleWithRequiredKey(requiredKey, key))) {
            filteredProperties.put(key, properties.get(key));
        }
    }

    private static boolean keyIsCompatibleWithRequiredKey(String requiredKey, String key) {
        String testedChar = key.substring(requiredKey.length(), requiredKey.length() + 1);
        if (testedChar.equals(ARRAY_START_SIGN) || testedChar.equals(".")) {
            return true;
        }
        return false;
    }

    private static Properties inputStreamToProperties(InputStream inputStream) throws ReadInputException {
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new ReadInputException(e);
        }
        return properties;
    }

    private static void addFieldsToJsonObject(Map<String, String> properties, ObjectJsonType coreObjectJsonType, String propertiesKey) {
        String[] fields = propertiesKey.split(REGEX_DOT);
        new JsonObjectsTraverseResolver(properties, propertiesKey, fields, coreObjectJsonType).initializeFieldsInJson();
    }

    private static List<String> getAllKeysFromProperties(Map<String, String> properties) {
        return propertyKeysPickup.getAllKeysFromProperties(properties);
    }


    private static Map<String, String> propertiesToMap(Properties properties) {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<Object,Object> property : properties.entrySet()) {
            map.put(property.getKey().toString(), property.getValue().toString());
        }
        return map;
    }

    protected static void setPropertyKeysPickup(PropertyKeysPickup propertyKeysPickup) {
        PropertiesToJsonParser.propertyKeysPickup = propertyKeysPickup;
    }
}
