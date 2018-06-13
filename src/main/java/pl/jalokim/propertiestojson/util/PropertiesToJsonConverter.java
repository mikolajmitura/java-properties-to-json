package pl.jalokim.propertiestojson.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import pl.jalokim.propertiestojson.AlgorithmType;
import pl.jalokim.propertiestojson.JsonObjectsTraverseResolver;
import pl.jalokim.propertiestojson.helper.PropertyKeysPickup;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.resolvers.ArrayJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.JsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.ObjectJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.BooleanJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.DoubleJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.IntegerJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.ObjectFromTextJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveArrayJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.StringJsonTypeResolver;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;
import pl.jalokim.propertiestojson.util.exception.ReadInputException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;
import static pl.jalokim.propertiestojson.Constants.REGEX_DOT;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.ADDED_SOME_TYPE_RESOLVER_AFTER_LAST;

public class PropertiesToJsonConverter {

    private PropertyKeysPickup propertyKeysPickup = new PropertyKeysPickup();
    private final Map<AlgorithmType, JsonTypeResolver> algorithms = new HashMap<>();

    private static final List<PrimitiveJsonTypeResolver> defaultPrimitiveResolvers;

    static {
        defaultPrimitiveResolvers = new ArrayList<>();
        defaultPrimitiveResolvers.add(new ObjectFromTextJsonTypeResolver());
        defaultPrimitiveResolvers.add(new PrimitiveArrayJsonTypeResolver());
        defaultPrimitiveResolvers.add(new DoubleJsonTypeResolver());
        defaultPrimitiveResolvers.add(new IntegerJsonTypeResolver());
        defaultPrimitiveResolvers.add(new BooleanJsonTypeResolver());
        defaultPrimitiveResolvers.add(new StringJsonTypeResolver());
    }

    /**
     * This constructor allow to give a resolvers, the order of resolvers is important.
     *
     * @param primitiveResolvers ordered list
     */
    public PropertiesToJsonConverter(PrimitiveJsonTypeResolver... primitiveResolvers) {
        validateTypeResolverOrder(primitiveResolvers);
        algorithms.put(AlgorithmType.OBJECT, new ObjectJsonTypeResolver());
        algorithms.put(AlgorithmType.PRIMITIVE, new PrimitiveJsonTypesResolver(Arrays.asList(primitiveResolvers)));
        algorithms.put(AlgorithmType.ARRAY, new ArrayJsonTypeResolver());
    }

    /**
     * Default implementation of json primitive type resolvers.
     */
    public PropertiesToJsonConverter() {
        this(fromListToArray(defaultPrimitiveResolvers));
    }

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
    public String parsePropertiesFromFileToJson(String pathToFile, String... includeDomainKeys) throws ReadInputException, ParsePropertiesException {
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
    public String parsePropertiesFromFileToJson(String pathToFile) throws ReadInputException, ParsePropertiesException {
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
    public String parsePropertiesFromFileToJson(File file, String... includeDomainKeys) throws ReadInputException, ParsePropertiesException {
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
    public String parsePropertiesFromFileToJson(File file) throws ReadInputException, ParsePropertiesException {
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
    public String parseToJson(InputStream inputStream, String... includeDomainKeys) throws ReadInputException, ParsePropertiesException {
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
    public String parseToJson(InputStream inputStream) throws ReadInputException, ParsePropertiesException {
        return parseToJson(inputStreamToProperties(inputStream));
    }

    /**
     * generate Json by given Java Properties
     *
     * @param properties Java Properties
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String parseToJson(Properties properties) throws ParsePropertiesException {
        return parseToJson(propertiesToMap(properties));
    }

    /**
     * generate Json by given Map&lt;String,String&gt;
     *
     * @param properties Java Map with properties
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String parseToJson(Map<String, String> properties) throws ParsePropertiesException {
        ObjectJsonType coreObjectJsonType = new ObjectJsonType();

        for (String propertiesKey : getAllKeysFromProperties(properties)) {
            addFieldsToJsonObject(properties, coreObjectJsonType, propertiesKey);
        }
        return prettifyOfJson(coreObjectJsonType.toStringJson());
    }

    private static String prettifyOfJson(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(json);
        return gson.toJson(je);
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
    public String parseToJson(Map<String, String> properties, String... includeDomainKeys) throws ParsePropertiesException {
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
    public String parseToJson(Properties properties, String... includeDomainKeys) throws ParsePropertiesException {
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

    private void addFieldsToJsonObject(Map<String, String> properties, ObjectJsonType coreObjectJsonType, String propertiesKey) {
        String[] fields = propertiesKey.split(REGEX_DOT);
        new JsonObjectsTraverseResolver(algorithms, properties, propertiesKey, fields, coreObjectJsonType)
                .initializeFieldsInJson();
    }

    private List<String> getAllKeysFromProperties(Map<String, String> properties) {
        return propertyKeysPickup.getAllKeysFromProperties(properties);
    }


    private static Map<String, String> propertiesToMap(Properties properties) {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<Object, Object> property : properties.entrySet()) {
            map.put(property.getKey().toString(), property.getValue().toString());
        }
        return map;
    }

    protected void setPropertyKeysPickup(PropertyKeysPickup propertyKeysPickup) {
        this.propertyKeysPickup = propertyKeysPickup;
    }

    private static PrimitiveJsonTypeResolver[] fromListToArray(List<PrimitiveJsonTypeResolver> resolversAsList) {
        PrimitiveJsonTypeResolver[] resolvers = new PrimitiveJsonTypeResolver[resolversAsList.size()];
        return resolversAsList.toArray(resolvers);
    }

    private static void validateTypeResolverOrder(PrimitiveJsonTypeResolver... primitiveResolvers) {
        List<PrimitiveJsonTypeResolver> resolvers = Arrays.asList(primitiveResolvers);
        boolean containStringResolverType = false;
        for (PrimitiveJsonTypeResolver resolver : resolvers) {
            if (resolver instanceof StringJsonTypeResolver) {
                containStringResolverType = true;
            }
        }
        if (containStringResolverType) {
            PrimitiveJsonTypeResolver lastResolver = resolvers.get(resolvers.size() - 1);
            if (!(lastResolver instanceof StringJsonTypeResolver)) {
                throw new ParsePropertiesException(ADDED_SOME_TYPE_RESOLVER_AFTER_LAST);
            }
        }
    }
}
