package pl.jalokim.propertiestojson.util;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;
import static pl.jalokim.propertiestojson.resolvers.primitives.object.NullToJsonTypeConverter.NULL_TO_JSON_RESOLVER;
import static pl.jalokim.propertiestojson.resolvers.primitives.object.StringToJsonTypeConverter.STRING_TO_JSON_RESOLVER;
import static pl.jalokim.propertiestojson.resolvers.primitives.string.TextToEmptyStringResolver.EMPTY_TEXT_RESOLVER;
import static pl.jalokim.propertiestojson.resolvers.primitives.string.TextToJsonNullReferenceResolver.TEXT_TO_NULL_JSON_RESOLVER;
import static pl.jalokim.propertiestojson.resolvers.primitives.string.TextToStringResolver.TO_STRING_RESOLVER;
import static pl.jalokim.propertiestojson.util.PropertiesToJsonConverterBuilder.TO_JSON_TYPE_CONVERTERS;
import static pl.jalokim.propertiestojson.util.PropertiesToJsonConverterBuilder.TO_OBJECT_RESOLVERS;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.PROPERTY_KEY_NEEDS_TO_BE_STRING_TYPE;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.STRING_RESOLVER_AS_NOT_LAST;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.STRING_TO_JSON_RESOLVER_AS_NOT_LAST;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import pl.jalokim.propertiestojson.AlgorithmType;
import pl.jalokim.propertiestojson.JsonObjectsTraverseResolver;
import pl.jalokim.propertiestojson.helper.PropertiesWithInsertOrder;
import pl.jalokim.propertiestojson.helper.PropertyKeysOrderResolver;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.path.PathMetadata;
import pl.jalokim.propertiestojson.path.PathMetadataBuilder;
import pl.jalokim.propertiestojson.resolvers.ArrayJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.JsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.ObjectJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.StringJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.adapter.PrimitiveJsonTypeResolverToNewApiAdapter;
import pl.jalokim.propertiestojson.resolvers.primitives.object.NullToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.object.StringToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToEmptyStringResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToJsonNullReferenceResolver;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;
import pl.jalokim.propertiestojson.util.exception.ReadInputException;

@SuppressWarnings("PMD.GodClass")
public final class PropertiesToJsonConverter {

    private final NullToJsonTypeConverter nullToJsonConverter;
    private final TextToJsonNullReferenceResolver textToJsonNullResolver;
    private final TextToEmptyStringResolver textToEmptyStringResolver;

    private final Map<AlgorithmType, JsonTypeResolver> algorithms = new HashMap<>();
    private final PrimitiveJsonTypesResolver primitiveResolvers;
    private final Charset charsetToUse;
    private PropertyKeysOrderResolver propertyKeysOrderResolver = new PropertyKeysOrderResolver();

    /**
     * Default implementation of json primitive type resolvers.
     */
    public PropertiesToJsonConverter() {
        this(TO_OBJECT_RESOLVERS, TO_JSON_TYPE_CONVERTERS);
    }

    /**
     * This is deprecated constructor and will be remove in 6.0.0 version. Please migrate you resolvers for new interfaces and build PropertiesToJsonConverter
     * instance through {@link PropertiesToJsonConverterBuilder}
     * <p>
     * For add order or own implementation for for {@link TextToConcreteObjectResolver} please use one of below: - and use method {@link
     * PropertiesToJsonConverterBuilder#onlyCustomTextToObjectResolvers} - and use
     * method {@link PropertiesToJsonConverterBuilder#defaultAndCustomTextToObjectResolvers}
     * <p>
     * For add order or own implementation for for {@link ObjectToJsonTypeConverter} please use one of below: - and use method {@link
     * PropertiesToJsonConverterBuilder#onlyCustomObjectToJsonTypeConverters} - and use
     * method {@link PropertiesToJsonConverterBuilder#defaultAndCustomObjectToJsonTypeConverters}
     * <p>
     *
     * @param customPrimitiveResolvers ordered list
     */
    @Deprecated
    public PropertiesToJsonConverter(PrimitiveJsonTypeResolver<?>... customPrimitiveResolvers) {
        this(convertToNewResolvers(customPrimitiveResolvers), convertToNewConverters(customPrimitiveResolvers));
    }

    public PropertiesToJsonConverter(List<TextToConcreteObjectResolver<?>> toObjectsResolvers,
        List<ObjectToJsonTypeConverter<?>> toJsonTypeResolvers) {
        this(toObjectsResolvers, toJsonTypeResolvers, NULL_TO_JSON_RESOLVER, TEXT_TO_NULL_JSON_RESOLVER, EMPTY_TEXT_RESOLVER, false);
    }

    public PropertiesToJsonConverter(List<TextToConcreteObjectResolver<?>> toObjectsResolvers,
        List<ObjectToJsonTypeConverter<?>> toJsonTypeResolvers,
        NullToJsonTypeConverter nullToJsonConverter,
        TextToJsonNullReferenceResolver textToJsonNullResolver,
        TextToEmptyStringResolver textToEmptyStringResolver,
        Boolean skipNull) {

        this(toObjectsResolvers, toJsonTypeResolvers, nullToJsonConverter, textToJsonNullResolver, textToEmptyStringResolver, skipNull, null);
    }

    public PropertiesToJsonConverter(List<TextToConcreteObjectResolver<?>> toObjectsResolvers,
        List<ObjectToJsonTypeConverter<?>> toJsonTypeResolvers,
        NullToJsonTypeConverter nullToJsonConverter,
        TextToJsonNullReferenceResolver textToJsonNullResolver,
        TextToEmptyStringResolver textToEmptyStringResolver,
        Boolean skipNull,
        Charset charset) {

        this.nullToJsonConverter = nullToJsonConverter;
        this.textToJsonNullResolver = textToJsonNullResolver;
        this.textToEmptyStringResolver = textToEmptyStringResolver;
        this.charsetToUse = Optional.ofNullable(charset).orElse(StandardCharsets.UTF_8);

        validateTypeResolverOrder(toJsonTypeResolvers);
        this.primitiveResolvers = new PrimitiveJsonTypesResolver(buildAllToObjectResolvers(toObjectsResolvers),
            buildAllToJsonResolvers(toJsonTypeResolvers),
            skipNull,
            nullToJsonConverter);
        algorithms.put(AlgorithmType.OBJECT, new ObjectJsonTypeResolver());
        algorithms.put(AlgorithmType.PRIMITIVE, this.primitiveResolvers);
        algorithms.put(AlgorithmType.ARRAY, new ArrayJsonTypeResolver());
    }

    private static List<ObjectToJsonTypeConverter<?>> convertToNewConverters(PrimitiveJsonTypeResolver<?>... customPrimitiveResolvers) {
        validateTypeResolverOrder(customPrimitiveResolvers);

        return Arrays.stream(customPrimitiveResolvers)
            .map(PrimitiveJsonTypeResolverToNewApiAdapter::new)
            .collect(Collectors.toList());
    }

    private static List<TextToConcreteObjectResolver<?>> convertToNewResolvers(PrimitiveJsonTypeResolver<?>... customPrimitiveResolvers) {
        return Arrays.stream(customPrimitiveResolvers)
            .map(PrimitiveJsonTypeResolverToNewApiAdapter::new)
            .collect(Collectors.toList());
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

    private static void checkKey(Map<String, Object> properties, Map<String, Object> filteredProperties, String key, String requiredKey) {
        if (key.equals(requiredKey) || key.startsWith(requiredKey) && keyIsCompatibleWithRequiredKey(requiredKey, key)) {
            filteredProperties.put(key, properties.get(key));
        }
    }

    private static boolean keyIsCompatibleWithRequiredKey(String requiredKey, String key) {
        String testedChar = key.substring(requiredKey.length(), requiredKey.length() + 1);
        return ARRAY_START_SIGN.equals(testedChar) || ".".equals(testedChar);
    }

    @SuppressFBWarnings("WMI_WRONG_MAP_ITERATOR")
    private static Map<String, Object> propertiesToMap(Properties properties) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (Object key : properties.keySet()) {
            map.put(key.toString(), properties.get(key));
        }
        return map;
    }

    private static void validateTypeResolverOrder(PrimitiveJsonTypeResolver<?>... primitiveResolvers) {
        List<PrimitiveJsonTypeResolver<?>> resolvers = asList(primitiveResolvers);
        boolean containStringResolverType = false;
        for (PrimitiveJsonTypeResolver<?> resolver : resolvers) {
            if (resolver.getClass().equals(StringJsonTypeResolver.class)) {
                containStringResolverType = true;
            }
        }
        if (containStringResolverType) {
            PrimitiveJsonTypeResolver<?> lastResolver = resolvers.get(resolvers.size() - 1);
            if (!(lastResolver.getClass().equals(StringJsonTypeResolver.class))) {
                throw new ParsePropertiesException(STRING_RESOLVER_AS_NOT_LAST);
            }
        }
    }

    private static void validateTypeResolverOrder(List<ObjectToJsonTypeConverter<?>> resolvers) {
        boolean containStringResolverType = false;
        for (ObjectToJsonTypeConverter<?> resolver : resolvers) {
            if (resolver.getClass().equals(StringToJsonTypeConverter.class)) {
                containStringResolverType = true;
            }
        }
        if (containStringResolverType) {
            ObjectToJsonTypeConverter<?> lastResolver = resolvers.get(resolvers.size() - 1);
            if (!(lastResolver.getClass().equals(StringToJsonTypeConverter.class))) {
                throw new ParsePropertiesException(STRING_TO_JSON_RESOLVER_AS_NOT_LAST);
            }
        }
    }

    /**
     * Merged list of TextToConcreteObjectResolver instances for first conversion phase
     *
     * @param resolvers provided by user
     * @return list
     */
    private List<TextToConcreteObjectResolver<?>> buildAllToObjectResolvers(List<TextToConcreteObjectResolver<?>> resolvers) {
        List<TextToConcreteObjectResolver<?>> allResolvers = new ArrayList<>();
        allResolvers.add(textToJsonNullResolver);
        allResolvers.add(textToEmptyStringResolver);
        allResolvers.addAll(resolvers);
        allResolvers.add(TO_STRING_RESOLVER);
        return allResolvers;
    }

    /**
     * Merged list of ObjectToJsonTypeConverter instances for second conversion phase
     *
     * @param toJsonTypeResolvers provided by user
     * @return list
     */
    private List<ObjectToJsonTypeConverter<?>> buildAllToJsonResolvers(List<ObjectToJsonTypeConverter<?>> toJsonTypeResolvers) {
        List<ObjectToJsonTypeConverter<?>> mergedToJsonTypeConverters = new ArrayList<>(toJsonTypeResolvers);
        mergedToJsonTypeConverters.add(STRING_TO_JSON_RESOLVER);
        mergedToJsonTypeConverters.add(nullToJsonConverter);
        return mergedToJsonTypeConverters;
    }

    /**
     * It generates Json from properties file stored in provided path as string. Every property value will tries resolve to concrete object by given
     * resolvers... It will try convert to some object (number, boolean, list etc, depends on generic type of given {@link
     * pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver}) from string value through method: {@link
     * pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver#returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, String propertyValue, String propertyKey)} The order of resolvers is important because on that depends on which resolver as
     * first will convert from string to some given object...
     * <p>
     * Next will looks for sufficient converter, firstly will looks for exactly match class type, if not then will looks for closets parent class or parent
     * interface. If will find resolver for parent class or parent interface at the same level, then will get parent super class as first. If will find only
     * closets super interfaces (at the same level) then will throw exception... after successful found resolver it converts from given object to some instance
     * which extends AbstractJsonType through method
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter#convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param pathToFile path to File
     * @return simple String with json
     * @throws ReadInputException when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String convertPropertiesFromFileToJson(String pathToFile) {
        return convertPropertiesFromFileToJson(new File(pathToFile));
    }

    /**
     * It generates Json from properties file stored in provided path as string and will converts only included keys or parts of property keys provided by
     * second parameter. Every property value will tries resolve to concrete object by given resolvers... It will try convert to some object (number, boolean,
     * list etc, depends on generic type of given {@link pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver}) from string
     * value through method:
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver#returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, String propertyValue, String propertyKey)} The order of resolvers is important because on that depends on which resolver as
     * first will convert from string to some given object...
     * <p>
     * Next will looks for sufficient converter, firstly will looks for exactly match class type, if not then will looks for closets parent class or parent
     * interface. If will find resolver for parent class or parent interface at the same level, then will get parent super class as first. If will find only
     * closets super interfaces (at the same level) then will throw exception... after successful found resolver it converts from given object to some instance
     * which extends AbstractJsonType through method
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter#convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param pathToFile path to File
     * @param includeDomainKeys domain head keys which should be parsed to json <br> example properties:<br> object1.field1=value1<br> object1.field2=value2<br>
     * someObject2.field2=value3<br> filter "object1"<br> will parse only nested domain for "object1"<br>
     * @return simple String with json
     * @throws ReadInputException when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String convertPropertiesFromFileToJson(String pathToFile, String... includeDomainKeys) {
        return convertPropertiesFromFileToJson(new File(pathToFile), includeDomainKeys);
    }

    /**
     * It generates Json from properties file stored in provided File. Every property value will tries resolve to concrete object by given resolvers... It will
     * try convert to some object (number, boolean, list etc, depends on generic type of given
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver})
     * from string value through method:
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver#returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, String propertyValue, String propertyKey)} The order of resolvers is important because on that depends on which resolver as
     * first will convert from string to some given object...
     * <p>
     * Next will looks for sufficient converter, firstly will looks for exactly match class type, if not then will looks for closets parent class or parent
     * interface. If will find resolver for parent class or parent interface at the same level, then will get parent super class as first. If will find only
     * closets super interfaces (at the same level) then will throw exception... after successful found resolver it converts from given object to some instance
     * which extends AbstractJsonType through method
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter#convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param file file with properties
     * @return simple String with json
     * @throws ReadInputException when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String convertPropertiesFromFileToJson(File file) {
        try (InputStream targetStream = Files.newInputStream(file.toPath())) {
            return convertToJson(targetStream);
        } catch (IOException e) {
            throw new ReadInputException(e);
        }
    }

    /**
     * It generates Json from properties file stored in provided File and will converts only included keys or parts of property keys provided by second
     * parameter. Every property value will tries resolve to concrete object by given resolvers... It will try convert to some object (number, boolean, list
     * etc, depends on generic type of given {@link pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver}) from string value
     * through method:
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver#returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, String propertyValue, String propertyKey)} The order of resolvers is important because on that depends on which resolver as
     * first will convert from string to some given object...
     * <p>
     * Next will looks for sufficient converter, firstly will looks for exactly match class type, if not then will looks for closets parent class or parent
     * interface. If will find resolver for parent class or parent interface at the same level, then will get parent super class as first. If will find only
     * closets super interfaces (at the same level) then will throw exception... after successful found resolver it converts from given object to some instance
     * which extends AbstractJsonType through method
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter#convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param file file with properties
     * @param includeDomainKeys domain head keys which should be parsed to json <br> example properties:<br> object1.field1=value1<br> object1.field2=value2<br>
     * someObject2.field2=value3<br> filter "object1"<br> will parse only nested domain for "object1"<br>
     * @return simple String with json
     * @throws ReadInputException when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String convertPropertiesFromFileToJson(File file, String... includeDomainKeys) {
        try (InputStream targetStream = Files.newInputStream(file.toPath())) {
            return convertToJson(targetStream, includeDomainKeys);
        } catch (IOException e) {
            throw new ReadInputException(e);
        }
    }

    /**
     * It generates Json from properties stored in provided InputStream. Every property value will tries resolve to concrete object by given resolvers... It
     * will try convert to some object (number, boolean, list etc, depends on generic type of given
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver})
     * from string value through method:
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver#returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, String propertyValue, String propertyKey)} The order of resolvers is important because on that depends on which resolver as
     * first will convert from string to some given object...
     * <p>
     * Next will looks for sufficient converter, firstly will looks for exactly match class type, if not then will looks for closets parent class or parent
     * interface. If will find resolver for parent class or parent interface at the same level, then will get parent super class as first. If will find only
     * closets super interfaces (at the same level) then will throw exception... after successful found resolver it converts from given object to some instance
     * which extends AbstractJsonType through method
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter#convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param inputStream InputStream with properties
     * @return simple String with json
     * @throws ReadInputException when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String convertToJson(InputStream inputStream) {
        return convertToJson(inputStreamToProperties(inputStream));
    }

    /**
     * It generates Json from properties stored in provided InputStream and will converts only included keys or parts of property keys provided by second
     * parameter. Every property value will tries resolve to concrete object by given resolvers... It will try convert to some object (number, boolean, list
     * etc, depends on generic type of given {@link pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver}) from string value
     * through method:
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver#returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, String propertyValue, String propertyKey)} The order of resolvers is important because on that depends on which resolver as
     * first will convert from string to some given object...
     * <p>
     * Next will looks for sufficient converter, firstly will looks for exactly match class type, if not then will looks for closets parent class or parent
     * interface. If will find resolver for parent class or parent interface at the same level, then will get parent super class as first. If will find only
     * closets super interfaces (at the same level) then will throw exception... after successful found resolver it converts from given object to some instance
     * which extends AbstractJsonType through method
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter#convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param inputStream InputStream with properties
     * @param includeDomainKeys domain head keys which should be parsed to json <br> example properties:<br> object1.field1=value1<br> object1.field2=value2<br>
     * someObject2.field2=value3<br> filter "object1"<br> will parse only nested domain for "object1"<br>
     * @return simple String with json
     * @throws ReadInputException when cannot find file
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String convertToJson(InputStream inputStream, String... includeDomainKeys) {
        return convertToJson(inputStreamToProperties(inputStream), includeDomainKeys);
    }

    /**
     * It generates Json from given Java Properties instance. If property value will be string then will not try convert it to another type.
     * <p>
     * It will only looks for sufficient resolver, firstly will looks for exactly match class type, if not then will looks for closets parent class or parent
     * interface. If will find resolver for parent class or parent interface at the same level, then will get parent super class as first. If will find only
     * closets super interfaces (at the same level) then will throw exception... after successful found resolver it converts from given object to some instance
     * which extends AbstractJsonType through method
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter#convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param properties Java Properties
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String convertToJson(Properties properties) {
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
     * It generates Json from given Java Properties instance and will converts only included keys or parts of property keys provided by second parameter. If
     * property value will be string then will not try convert it to another type.
     * <p>
     * It will only looks for sufficient resolver, firstly will looks for exactly match class type, if not then will looks for closets parent class or parent
     * interface. If will find resolver for parent class or parent interface at the same level, then will get parent super class as first. If will find only
     * closets super interfaces (at the same level) then will throw exception... after successful found resolver it converts from given object to some instance
     * which extends AbstractJsonType through method
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter#convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param properties Java Properties
     * @param includeDomainKeys domain head keys which should be parsed to json <br> example properties:<br> object1.field1=value1<br> object1.field2=value2<br>
     * someObject2.field2=value3<br> filter "object1"<br> will parse only nested domain for "object1"<br>
     * @return Simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String convertToJson(Properties properties, String... includeDomainKeys) {
        return convertFromValuesAsObjectMap(propertiesToMap(properties), includeDomainKeys);
    }

    /**
     * It generates Json from given Map&lt;String,String&gt; instance. Every property value will tries resolve to concrete object by given resolvers... It will
     * try convert to some object (number, boolean, list etc, depends on generic type of given
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver})
     * from string value through method:
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver#returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, String propertyValue, String propertyKey)} The order of resolvers is important because on that depends on which resolver as
     * first will convert from string to some given object...
     * <p>
     * Next will looks for sufficient converter, firstly will looks for exactly match class type, if not then will looks for closets parent class or parent
     * interface. If will find resolver for parent class or parent interface at the same level, then will get parent super class as first. If will find only
     * closets super interfaces (at the same level) then will throw exception... after successful found resolver it converts from given object to some instance
     * which extends AbstractJsonType through method
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter#convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param properties Java Map with properties
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */

    public String convertToJson(Map<String, String> properties) {
        return convertFromValuesAsObjectMap(stringValueMapToObjectValueMap(properties));

    }

    /**
     * It generates Json from given Map&lt;String,String&gt; instance and will converts only included keys or parts of property keys provided by second
     * parameter. Every property value will tries resolve to concrete object by given resolvers... It will try convert to some object (number, boolean, list
     * etc, depends on generic type of given {@link pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver}) from string value
     * through method:
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver#returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, String propertyValue, String propertyKey)} The order of resolvers is important because on that depends on which resolver as
     * first will convert from string to some given object...
     * <p>
     * Next will looks for sufficient converter, firstly will looks for exactly match class type, if not then will looks for closets parent class or parent
     * interface. If will find resolver for parent class or parent interface at the same level, then will get parent super class as first. If will find only
     * closets super interfaces (at the same level) then will throw exception... after successful found resolver it converts from given object to some instance
     * which extends AbstractJsonType through method
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter#convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param properties Java Map with properties
     * @param includeDomainKeys domain head keys which should be parsed to json <br> example properties:<br> object1.field1=value1<br> object1.field2=value2<br>
     * someObject2.field2=value3<br> filter "object1"<br> will parse only nested domain for "object1"<br>
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */

    public String convertToJson(Map<String, String> properties, String... includeDomainKeys) {
        return convertFromValuesAsObjectMap(stringValueMapToObjectValueMap(properties), includeDomainKeys);
    }

    /**
     * It generates Json given Map&lt;String,Object&gt; instance. If property value will be string then will not try convert it to another type.
     * <p>
     * It will only looks for sufficient resolver, firstly will looks for exactly match class type, if not then will looks for closets parent class or parent
     * interface. If will find resolver for parent class or parent interface at the same level, then will get parent super class as first. If will find only
     * closets super interfaces (at the same level) then will throw exception... after successful found resolver it converts from given object to some instance
     * which extends AbstractJsonType through method
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter#convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param properties Java Map with properties
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */

    public String convertFromValuesAsObjectMap(Map<String, Object> properties) {
        ObjectJsonType coreObjectJsonType = new ObjectJsonType();
        for (String propertyKey : getAllKeysFromProperties(properties)) {
            addFieldsToJsonObject(properties, coreObjectJsonType, propertyKey);
        }
        return prettifyOfJson(coreObjectJsonType.toStringJson());
    }

    /**
     * It generates Json given Map&lt;String,Object&gt; instance and will converts only included keys or parts of property keys provided by second parameter. If
     * property value will be string then will not try convert it to another type.
     * <p>
     * It will only looks for sufficient resolver, firstly will looks for exactly match class type, if not then will looks for closets parent class or parent
     * interface. If will find resolver for parent class or parent interface at the same level, then will get parent super class as first. If will find only
     * closets super interfaces (at the same level) then will throw exception... after successful found resolver it converts from given object to some instance
     * which extends AbstractJsonType through method
     * {@link pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter#convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver
     * primitiveJsonTypesResolver, Object propertyValue, String propertyKey)}
     *
     * @param properties Java Map with properties
     * @param includeDomainKeys domain head keys which should be parsed to json <br> example properties:<br> object1.field1=value1<br> object1.field2=value2<br>
     * someObject2.field2=value3<br> filter "object1"<br> will parse only nested domain for "object1"<br>
     * @return simple String with json
     * @throws ParsePropertiesException when structure of properties is not compatible with json structure
     */
    public String convertFromValuesAsObjectMap(Map<String, Object> properties, String... includeDomainKeys) {
        Map<String, Object> filteredProperties = new LinkedHashMap<>();
        for (String key : properties.keySet()) {
            for (String requiredKey : includeDomainKeys) {
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

    private Properties inputStreamToProperties(InputStream inputStream) {
        Properties propertiesWithConvertedValues = new PropertiesWithInsertOrder();
        Properties properties = new PropertiesWithInsertOrder();
        try {
            properties.load(new InputStreamReader(inputStream, charsetToUse));
            for (String property : getAllKeysFromProperties(propertiesToMap(properties))) {
                Object object = primitiveResolvers.getResolvedObject((String) properties.get(property), property);
                propertiesWithConvertedValues.put(property, object);
            }
        } catch (IOException e) {
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

    private Map<String, Object> stringValueMapToObjectValueMap(Map<String, String> properties) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String property : getAllKeysFromProperties(properties)) {
            Object object = primitiveResolvers.getResolvedObject(properties.get(property), property);
            map.put(property, object);
        }
        return map;
    }
}
