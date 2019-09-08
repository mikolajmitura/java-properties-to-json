package pl.jalokim.propertiestojson.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import pl.jalokim.propertiestojson.AlgorithmType;
import pl.jalokim.propertiestojson.JsonObjectsTraverseResolver;
import pl.jalokim.propertiestojson.helper.PropertyKeysOrderResolver;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.path.PathMetadata;
import pl.jalokim.propertiestojson.path.PathMetadataBuilder;
import pl.jalokim.propertiestojson.resolvers.ArrayJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.JsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.ObjectJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.BooleanJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.CharacterJsonTypeResolver;
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
import static java.util.Objects.requireNonNull;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;
import static pl.jalokim.propertiestojson.resolvers.primitives.JsonNullReferenceTypeResolver.NULL_RESOLVER;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.STRING_RESOLVER_AS_NOT_LAST;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.PROPERTY_KEY_NEEDS_TO_BE_STRING_TYPE;

public final class PropertiesToJsonConverter {

    public static final StringJsonTypeResolver STRING_RESOLVER = new StringJsonTypeResolver();
    private static final List<PrimitiveJsonTypeResolver> DEFAULT_PRIMITIVE_RESOLVERS;
    private static final EmptyStringJsonTypeResolver EMPTY_TEXT_RESOLVER = new EmptyStringJsonTypeResolver();

    static {
        DEFAULT_PRIMITIVE_RESOLVERS = new ArrayList<>();
        DEFAULT_PRIMITIVE_RESOLVERS.add(new PrimitiveArrayJsonTypeResolver());
        DEFAULT_PRIMITIVE_RESOLVERS.add(new ObjectFromTextJsonTypeResolver());
        DEFAULT_PRIMITIVE_RESOLVERS.add(new NumberJsonTypeResolver());
        DEFAULT_PRIMITIVE_RESOLVERS.add(new CharacterJsonTypeResolver());
        DEFAULT_PRIMITIVE_RESOLVERS.add(new BooleanJsonTypeResolver());
    }

    private PropertyKeysOrderResolver propertyKeysOrderResolver = new PropertyKeysOrderResolver();
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
     * It generates Json from properties file stored in provided path as string.
     * Every property value will tries resolve to concrete object by given resolvers...
     * It will try convert to some object (number, boolean, list etc, depends on generic type of given {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver}) from string value through method:
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey)}
     * The order of resolvers is important because on that depends on which resolver as first will convert from string to some given object...
     * <p>
     * Next will looks for sufficient resolver, firstly will looks for exactly match class type,
     * if not then will looks for closets parent class or parent interface.
     * If will find resolver for parent class or parent interface at the same level, then will get parent super class as first.
     * If will find only closets super interfaces (at the same level) then will throw exception...
     * after successful found resolver it converts from given object to some instance which extends AbstractJsonType
     * through method {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param pathToFile path to File
     * @return simple String with json
     * @throws ReadInputException       when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String convertPropertiesFromFileToJson(String pathToFile) throws ReadInputException, ParsePropertiesException {
        return convertPropertiesFromFileToJson(new File(pathToFile));
    }

    /**
     * It generates Json from properties file stored in provided path as string and will converts only included keys or parts of property keys provided by second parameter.
     * Every property value will tries resolve to concrete object by given resolvers...
     * It will try convert to some object (number, boolean, list etc, depends on generic type of given {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver}) from string value through method:
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey)}
     * The order of resolvers is important because on that depends on which resolver as first will convert from string to some given object...
     * <p>
     * Next will looks for sufficient resolver, firstly will looks for exactly match class type,
     * if not then will looks for closets parent class or parent interface.
     * If will find resolver for parent class or parent interface at the same level, then will get parent super class as first.
     * If will find only closets super interfaces (at the same level) then will throw exception...
     * after successful found resolver it converts from given object to some instance which extends AbstractJsonType
     * through method {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
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
    public String convertPropertiesFromFileToJson(String pathToFile, String... includeDomainKeys) throws ReadInputException, ParsePropertiesException {
        return convertPropertiesFromFileToJson(new File(pathToFile), includeDomainKeys);
    }

    /**
     * It generates Json from properties file stored in provided File.
     * Every property value will tries resolve to concrete object by given resolvers...
     * It will try convert to some object (number, boolean, list etc, depends on generic type of given {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver}) from string value through method:
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey)}
     * The order of resolvers is important because on that depends on which resolver as first will convert from string to some given object...
     * <p>
     * Next will looks for sufficient resolver, firstly will looks for exactly match class type,
     * if not then will looks for closets parent class or parent interface.
     * If will find resolver for parent class or parent interface at the same level, then will get parent super class as first.
     * If will find only closets super interfaces (at the same level) then will throw exception...
     * after successful found resolver it converts from given object to some instance which extends AbstractJsonType
     * through method {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param file file with properties
     * @return simple String with json
     * @throws ReadInputException       when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String convertPropertiesFromFileToJson(File file) throws ReadInputException, ParsePropertiesException {
        try {
            InputStream targetStream = new FileInputStream(file);
            return convertToJson(targetStream);
        } catch(FileNotFoundException e) {
            throw new ReadInputException(e);
        }
    }

    /**
     * It generates Json from properties file stored in provided File and will converts only included keys or parts of property keys provided by second parameter.
     * Every property value will tries resolve to concrete object by given resolvers...
     * It will try convert to some object (number, boolean, list etc, depends on generic type of given {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver}) from string value through method:
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey)}
     * The order of resolvers is important because on that depends on which resolver as first will convert from string to some given object...
     * <p>
     * Next will looks for sufficient resolver, firstly will looks for exactly match class type,
     * if not then will looks for closets parent class or parent interface.
     * If will find resolver for parent class or parent interface at the same level, then will get parent super class as first.
     * If will find only closets super interfaces (at the same level) then will throw exception...
     * after successful found resolver it converts from given object to some instance which extends AbstractJsonType
     * through method {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
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
    public String convertPropertiesFromFileToJson(File file, String... includeDomainKeys) throws ReadInputException, ParsePropertiesException {
        try {
            InputStream targetStream = new FileInputStream(file);
            return convertToJson(targetStream, includeDomainKeys);
        } catch(FileNotFoundException e) {
            throw new ReadInputException(e);
        }
    }

    /**
     * It generates Json from properties stored in provided InputStream.
     * Every property value will tries resolve to concrete object by given resolvers...
     * It will try convert to some object (number, boolean, list etc, depends on generic type of given {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver}) from string value through method:
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey)}
     * The order of resolvers is important because on that depends on which resolver as first will convert from string to some given object...
     * <p>
     * Next will looks for sufficient resolver, firstly will looks for exactly match class type,
     * if not then will looks for closets parent class or parent interface.
     * If will find resolver for parent class or parent interface at the same level, then will get parent super class as first.
     * If will find only closets super interfaces (at the same level) then will throw exception...
     * after successful found resolver it converts from given object to some instance which extends AbstractJsonType
     * through method {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param inputStream InputStream with properties
     * @return simple String with json
     * @throws ReadInputException       when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String convertToJson(InputStream inputStream) throws ReadInputException, ParsePropertiesException {
        return convertToJson(inputStreamToProperties(inputStream));
    }

    /**
     * It generates Json from properties stored in provided InputStream and will converts only included keys or parts of property keys provided by second parameter.
     * Every property value will tries resolve to concrete object by given resolvers...
     * It will try convert to some object (number, boolean, list etc, depends on generic type of given {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver}) from string value through method:
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey)}
     * The order of resolvers is important because on that depends on which resolver as first will convert from string to some given object...
     * <p>
     * Next will looks for sufficient resolver, firstly will looks for exactly match class type,
     * if not then will looks for closets parent class or parent interface.
     * If will find resolver for parent class or parent interface at the same level, then will get parent super class as first.
     * If will find only closets super interfaces (at the same level) then will throw exception...
     * after successful found resolver it converts from given object to some instance which extends AbstractJsonType
     * through method {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
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
    public String convertToJson(InputStream inputStream, String... includeDomainKeys) throws ReadInputException, ParsePropertiesException {
        return convertToJson(inputStreamToProperties(inputStream), includeDomainKeys);
    }

    /**
     * It generates Json from given Java Properties instance.
     * If property value will be string then will not try convert it to another type.
     * <p>
     * It will only looks for sufficient resolver, firstly will looks for exactly match class type,
     * if not then will looks for closets parent class or parent interface.
     * If will find resolver for parent class or parent interface at the same level, then will get parent super class as first.
     * If will find only closets super interfaces (at the same level) then will throw exception...
     * after successful found resolver it converts from given object to some instance which extends AbstractJsonType
     * through method {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param properties Java Properties
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String convertToJson(Properties properties) throws ParsePropertiesException {
        for(Map.Entry<Object, Object> entry : properties.entrySet()) {
            if(!(entry.getKey() instanceof String)) {
                throw new ParsePropertiesException(format(PROPERTY_KEY_NEEDS_TO_BE_STRING_TYPE,
                                                          entry.getKey().getClass(),
                                                          entry.getKey() == null ? "null" : entry.getKey()));
            }
        }
        return convertFromValuesAsObjectMap(propertiesToMap(properties));
    }

    /**
     * It generates Json from given Java Properties instance and will converts only included keys or parts of property keys provided by second parameter.
     * If property value will be string then will not try convert it to another type.
     * <p>
     * It will only looks for sufficient resolver, firstly will looks for exactly match class type,
     * if not then will looks for closets parent class or parent interface.
     * If will find resolver for parent class or parent interface at the same level, then will get parent super class as first.
     * If will find only closets super interfaces (at the same level) then will throw exception...
     * after successful found resolver it converts from given object to some instance which extends AbstractJsonType
     * through method {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param properties        Java Properties
     * @param includeDomainKeys domain head keys which should be parsed to json <br>
     *                          example properties:<br>
     *                          object1.field1=value1<br>
     *                          object1.field2=value2<br>
     *                          someObject2.field2=value3<br>
     *                          filter "object1"<br>
     *                          will parse only nested domain for "object1"<br>
     * @return Simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String convertToJson(Properties properties, String... includeDomainKeys) throws ParsePropertiesException {
        return convertFromValuesAsObjectMap(propertiesToMap(properties), includeDomainKeys);
    }

    /**
     * It generates Json from given Map&lt;String,String&gt; instance.
     * Every property value will tries resolve to concrete object by given resolvers...
     * It will try convert to some object (number, boolean, list etc, depends on generic type of given {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver}) from string value through method:
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey)}
     * The order of resolvers is important because on that depends on which resolver as first will convert from string to some given object...
     * <p>
     * Next will looks for sufficient resolver, firstly will looks for exactly match class type,
     * if not then will looks for closets parent class or parent interface.
     * If will find resolver for parent class or parent interface at the same level, then will get parent super class as first.
     * If will find only closets super interfaces (at the same level) then will throw exception...
     * after successful found resolver it converts from given object to some instance which extends AbstractJsonType
     * through method {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param properties Java Map with properties
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */

    public String convertToJson(Map<String, String> properties) throws ParsePropertiesException {
        return convertFromValuesAsObjectMap(stringValueMapToObjectValueMap(properties));

    }

    /**
     * It generates Json from given Map&lt;String,String&gt; instance and will converts only included keys or parts of property keys provided by second parameter.
     * Every property value will tries resolve to concrete object by given resolvers...
     * It will try convert to some object (number, boolean, list etc, depends on generic type of given {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver}) from string value through method:
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey)}
     * The order of resolvers is important because on that depends on which resolver as first will convert from string to some given object...
     * <p>
     * Next will looks for sufficient resolver, firstly will looks for exactly match class type,
     * if not then will looks for closets parent class or parent interface.
     * If will find resolver for parent class or parent interface at the same level, then will get parent super class as first.
     * If will find only closets super interfaces (at the same level) then will throw exception...
     * after successful found resolver it converts from given object to some instance which extends AbstractJsonType
     * through method {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
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

    public String convertToJson(Map<String, String> properties, String... includeDomainKeys) throws ParsePropertiesException {
        return convertFromValuesAsObjectMap(stringValueMapToObjectValueMap(properties), includeDomainKeys);
    }

    /**
     * It generates Json given Map&lt;String,Object&gt; instance.
     * If property value will be string then will not try convert it to another type.
     * <p>
     * It will only looks for sufficient resolver, firstly will looks for exactly match class type,
     * if not then will looks for closets parent class or parent interface.
     * If will find resolver for parent class or parent interface at the same level, then will get parent super class as first.
     * If will find only closets super interfaces (at the same level) then will throw exception...
     * after successful found resolver it converts from given object to some instance which extends AbstractJsonType
     * through method {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param properties Java Map with properties
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */

    public String convertFromValuesAsObjectMap(Map<String, Object> properties) throws ParsePropertiesException {
        ObjectJsonType coreObjectJsonType = new ObjectJsonType();
        for(String propertyKey : getAllKeysFromProperties(properties)) {
            addFieldsToJsonObject(properties, coreObjectJsonType, propertyKey);
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
     * It generates Json given Map&lt;String,Object&gt; instance and will converts only included keys or parts of property keys provided by second parameter.
     * If property value will be string then will not try convert it to another type.
     * <p>
     * It will only looks for sufficient resolver, firstly will looks for exactly match class type,
     * if not then will looks for closets parent class or parent interface.
     * If will find resolver for parent class or parent interface at the same level, then will get parent super class as first.
     * If will find only closets super interfaces (at the same level) then will throw exception...
     * after successful found resolver it converts from given object to some instance which extends AbstractJsonType
     * through method {@link pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver#returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
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
        for(String key : properties.keySet()) {
            for(String requiredKey : includeDomainKeys) {
                checkKey(properties, filteredProperties, key, requiredKey);
            }
        }
        return convertFromValuesAsObjectMap(filteredProperties);
    }

    /**
     * It change implementation of order gathering keys from properties
     *
     * @param propertyKeysOrderResolver another implementation of get ordered properties keys
     */
    public void setPropertyKeysOrderResolver(PropertyKeysOrderResolver propertyKeysOrderResolver) {
        requireNonNull(propertyKeysOrderResolver);
        this.propertyKeysOrderResolver = propertyKeysOrderResolver;
    }

    private static void checkKey(Map<String, Object> properties, Map<String, Object> filteredProperties, String key, String requiredKey) {
        if(key.equals(requiredKey) || (key.startsWith(requiredKey) && keyIsCompatibleWithRequiredKey(requiredKey, key))) {
            filteredProperties.put(key, properties.get(key));
        }
    }

    private static boolean keyIsCompatibleWithRequiredKey(String requiredKey, String key) {
        String testedChar = key.substring(requiredKey.length(), requiredKey.length() + 1);
        if(testedChar.equals(ARRAY_START_SIGN) || testedChar.equals(".")) {
            return true;
        }
        return false;
    }

    private Properties inputStreamToProperties(InputStream inputStream) throws ReadInputException {
        Properties propertiesWithConvertedValues = new Properties();
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
            for(String property : getAllKeysFromProperties(propertiesToMap(properties))) {
                Object object = primitiveResolvers.getResolvedObject((String) properties.get(property), property);
                propertiesWithConvertedValues.put(property, object);
            }
        } catch(IOException e) {
            throw new ReadInputException(e);
        }
        return propertiesWithConvertedValues;
    }

    private void addFieldsToJsonObject(Map<String, Object> properties, ObjectJsonType coreObjectJsonType, String propertyKey) {
        PathMetadata rootPathMetaData = PathMetadataBuilder.createRootPathMetaData(propertyKey);
        new JsonObjectsTraverseResolver(algorithms, properties, propertyKey, rootPathMetaData, coreObjectJsonType)
                .initializeFieldsInJson();
    }

    private List<String> getAllKeysFromProperties(Map<String, ?> properties) {
        return propertyKeysOrderResolver.getKeysInExpectedOrder(properties);
    }


    private static Map<String, Object> propertiesToMap(Properties properties) {
        Map<String, Object> map = new HashMap<>();
        for(Map.Entry<Object, Object> property : properties.entrySet()) {
            map.put(property.getKey().toString(), property.getValue());
        }
        return map;
    }

    private Map<String, Object> stringValueMapToObjectValueMap(Map<String, String> properties) {
        Map<String, Object> map = new HashMap<>();
        for(String property : getAllKeysFromProperties(properties)) {
            Object object = primitiveResolvers.getResolvedObject(properties.get(property), property);
            map.put(property, object);
        }
        return map;
    }


    private static PrimitiveJsonTypeResolver[] fromListToArray(List<PrimitiveJsonTypeResolver> resolversAsList) {
        PrimitiveJsonTypeResolver[] resolvers = new PrimitiveJsonTypeResolver[resolversAsList.size()];
        return resolversAsList.toArray(resolvers);
    }

    private static void validateTypeResolverOrder(PrimitiveJsonTypeResolver... primitiveResolvers) {
        List<PrimitiveJsonTypeResolver> resolvers = asList(primitiveResolvers);
        boolean containStringResolverType = false;
        for(PrimitiveJsonTypeResolver resolver : resolvers) {
            if(resolver instanceof StringJsonTypeResolver) {
                containStringResolverType = true;
            }
        }
        if(containStringResolverType) {
            PrimitiveJsonTypeResolver lastResolver = resolvers.get(resolvers.size() - 1);
            if(!(lastResolver instanceof StringJsonTypeResolver)) {
                throw new ParsePropertiesException(STRING_RESOLVER_AS_NOT_LAST);
            }
        }
    }
}
