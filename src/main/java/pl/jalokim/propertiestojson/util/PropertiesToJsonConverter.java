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
import pl.jalokim.propertiestojson.resolvers.primitives.EmptyStringJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.JsonNullReferenceTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.NumberJsonTypeResolver;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;
import static pl.jalokim.propertiestojson.Constants.REGEX_DOT;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.STRING_RESOLVER_AS_NOT_LAST;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.PROPERTY_KEY_NEEDS_TO_BE_STRING_TYPE;

public class PropertiesToJsonConverter {

    public static final StringJsonTypeResolver STRING_RESOLVER = new StringJsonTypeResolver();
    private static final List<PrimitiveJsonTypeResolver> DEFAULT_PRIMITIVE_RESOLVERS;
    private static final JsonNullReferenceTypeResolver NULL_RESOLVER = new JsonNullReferenceTypeResolver();
    private static final EmptyStringJsonTypeResolver EMPTY_TEXT_RESOLVER = new EmptyStringJsonTypeResolver();

    static {
        DEFAULT_PRIMITIVE_RESOLVERS = new ArrayList<>();
        DEFAULT_PRIMITIVE_RESOLVERS.add(new PrimitiveArrayJsonTypeResolver());
        DEFAULT_PRIMITIVE_RESOLVERS.add(new ObjectFromTextJsonTypeResolver());
        DEFAULT_PRIMITIVE_RESOLVERS.add(new NumberJsonTypeResolver());
        DEFAULT_PRIMITIVE_RESOLVERS.add(new BooleanJsonTypeResolver());
    }

    private PropertyKeysPickup propertyKeysPickup = new PropertyKeysPickup();
    private final Map<AlgorithmType, JsonTypeResolver> algorithms = new HashMap<>();
    private final PrimitiveJsonTypesResolver primitiveResolvers;

    /**
     * This constructor allow to give a resolvers, the order of resolvers is important.
     *
     * @param primitiveResolvers ordered list
     */
    public PropertiesToJsonConverter(PrimitiveJsonTypeResolver... primitiveResolvers) {
        validateTypeResolverOrder(primitiveResolvers);
        this.primitiveResolvers = new PrimitiveJsonTypesResolver(buildAllPrimitiveResolvers(primitiveResolvers));
        algorithms.put(AlgorithmType.OBJECT, new ObjectJsonTypeResolver());
        algorithms.put(AlgorithmType.PRIMITIVE, this.primitiveResolvers);
        algorithms.put(AlgorithmType.ARRAY, new ArrayJsonTypeResolver());
    }

    /**
     * Default implementation of json primitive type resolvers.
     */
    public PropertiesToJsonConverter() {
        this(fromListToArray(DEFAULT_PRIMITIVE_RESOLVERS));
    }

    private List<PrimitiveJsonTypeResolver> buildAllPrimitiveResolvers(PrimitiveJsonTypeResolver... primitiveResolvers) {
        List<PrimitiveJsonTypeResolver> allPrimitiveResolvers = new ArrayList<>();
        allPrimitiveResolvers.add(NULL_RESOLVER);
        allPrimitiveResolvers.add(EMPTY_TEXT_RESOLVER);
        allPrimitiveResolvers.addAll(asList(primitiveResolvers));
        allPrimitiveResolvers.add(STRING_RESOLVER);
        return allPrimitiveResolvers;
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
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            if (!(entry.getKey() instanceof String)) {
                throw new ParsePropertiesException(format(PROPERTY_KEY_NEEDS_TO_BE_STRING_TYPE,
                                                          entry.getKey().getClass(),
                                                          entry.getKey() == null ? "null" : entry.getKey()));
            }
        }
        return convertFromValuesAsObjectMap(propertiesToMap(properties));
    }

    /**
     * generate Json by given Map&lt;String,String&gt;
     *
     * @param properties Java Map with properties
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String parseToJson(Map<String, String> properties) throws ParsePropertiesException {
        return convertFromValuesAsObjectMap(stringValueMapToObjectValueMap(properties));

    }

    /**
     * generate Json by given Map&lt;String,Object&gt;
     *
     * @param properties Java Map with properties
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String convertFromValuesAsObjectMap(Map<String, Object> properties) throws ParsePropertiesException {
        ObjectJsonType coreObjectJsonType = new ObjectJsonType();
        for (String propertiesKey : getAllKeysFromProperties(properties)) {
            addFieldsToJsonObject(properties, coreObjectJsonType, propertiesKey);
        }
        return prettifyOfJson(coreObjectJsonType.toStringJson());
    }

    private static String prettifyOfJson(String json) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
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
        return convertFromValuesAsObjectMap(stringValueMapToObjectValueMap(properties), includeDomainKeys);
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
    public String convertFromValuesAsObjectMap(Map<String, Object> properties, String... includeDomainKeys) throws ParsePropertiesException {
        Map<String, Object> filteredProperties = new HashMap<>();
        for (String key : properties.keySet()) {
            for (String requiredKey : includeDomainKeys) {
                checkKey(properties, filteredProperties, key, requiredKey);
            }
        }
        return convertFromValuesAsObjectMap(filteredProperties);
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
        return convertFromValuesAsObjectMap(propertiesToMap(properties), includeDomainKeys);
    }


    private static void checkKey(Map<String, Object> properties, Map<String, Object> filteredProperties, String key, String requiredKey) {
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

    private Properties inputStreamToProperties(InputStream inputStream) throws ReadInputException {
        Properties propertiesWithConvertedValues = new Properties();
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
            for (Map.Entry<Object, Object> property : properties.entrySet()) {
                Object object = primitiveResolvers.getResolvedObject((String) property.getValue());
                propertiesWithConvertedValues.put(property.getKey(), object);
            }
        } catch (IOException e) {
            throw new ReadInputException(e);
        }
        return propertiesWithConvertedValues;
    }

    private void addFieldsToJsonObject(Map<String, Object> properties, ObjectJsonType coreObjectJsonType, String propertiesKey) {
        String[] fields = propertiesKey.split(REGEX_DOT);
        new JsonObjectsTraverseResolver(algorithms, properties, propertiesKey, fields, coreObjectJsonType)
                .initializeFieldsInJson();
    }

    private List<String> getAllKeysFromProperties(Map<String, Object> properties) {
        return propertyKeysPickup.getAllKeysFromProperties(properties);
    }


    private static Map<String, Object> propertiesToMap(Properties properties) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<Object, Object> property : properties.entrySet()) {
            map.put(property.getKey().toString(), property.getValue());
        }
        return map;
    }

    private Map<String, Object> stringValueMapToObjectValueMap(Map<String, String> properties) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, String> property : properties.entrySet()) {
            Object object = primitiveResolvers.getResolvedObject(property.getValue());
            map.put(property.getKey(), object);
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
        List<PrimitiveJsonTypeResolver> resolvers = asList(primitiveResolvers);
        boolean containStringResolverType = false;
        for (PrimitiveJsonTypeResolver resolver : resolvers) {
            if (resolver instanceof StringJsonTypeResolver) {
                containStringResolverType = true;
            }
        }
        if (containStringResolverType) {
            PrimitiveJsonTypeResolver lastResolver = resolvers.get(resolvers.size() - 1);
            if (!(lastResolver instanceof StringJsonTypeResolver)) {
                throw new ParsePropertiesException(STRING_RESOLVER_AS_NOT_LAST);
            }
        }
    }
}
