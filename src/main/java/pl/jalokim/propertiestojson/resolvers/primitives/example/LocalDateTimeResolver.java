package pl.jalokim.propertiestojson.resolvers.primitives.example;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.JsonNullReferenceType;
import pl.jalokim.propertiestojson.object.NumberJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.hierarchy.JsonTypeResolversHierarchyResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.ObjectFromTextJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * results of this resolver you can see in test class LocalDateTimeResolverTest
 *
 * @see <a href="https://github.com/mikolajmitura/java-properties-to-json/blob/v5.0.0/src/test/java/pl/jalokim/propertiestojson/resolvers/primitives/example/LocalDateTimeResolverTest.java">LocalDateTimeResolverTest</a>
 */
public class LocalDateTimeResolver extends PrimitiveJsonTypeResolver<LocalDate> {

    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private final DateTimeFormatter formatter;
    private final boolean asTimestampInUTC;

    LocalDateTimeResolver() {
        this(DATE_FORMAT, false);
    }

    LocalDateTimeResolver(boolean asTimestampInUTC) {
        this(DATE_FORMAT, asTimestampInUTC);
    }

    LocalDateTimeResolver(String formatOfDate) {
        this(formatOfDate, false);
    }

    LocalDateTimeResolver(String formatOfDate, boolean asTimestampInUTC) {
        formatter = DateTimeFormatter.ofPattern(formatOfDate);
        this.asTimestampInUTC = asTimestampInUTC;
    }

    /**
     * This method will be called in first phase processing step (from raw text to some object)
     * if your condition is true then return concrete value of Object.
     * if it not meets your's condition then return Optional.empty() for allow go to others type resolver in order.
     * This will be called only for read properties from Map&lt;String,String&gt;, File with properties, InputStream with properties
     *
     * @param primitiveJsonTypesResolver primitiveJsonTypesResolver
     * @param propertyValue              currently processing property value
     * @param propertyKey                currently processing property key
     * @return optional value
     */

    @Override
    protected Optional<LocalDate> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                                       String propertyValue,
                                                                       String propertyKey) {
        try {
            return Optional.ofNullable(LocalDate.parse(propertyValue, formatter)); // if parse then will return LocalDate
        } catch(Exception ex) {
            return Optional.empty(); // if not
        }
    }

    /**
     * This method will be called in second phase processing step (from some java Object to some implementation of AbstractJsonType)
     * it will be called during read properties from Map&lt;String,Object&gt;, Properties (without first processing step) or after first
     * processing step (while reading properties from file, Map&lt;String,String&gt;, inputStream)
     * <p>
     * But resolvers order (provided in PropertiesToJsonConverter(PrimitiveJsonTypeResolver... primitiveResolvers) constructor) doesn't have importance here as in first processing phase.
     * The hierarchy of classes plays a central role here
     * It looks for sufficient resolver, firstly will looks for exactly match class type provided by method {@link PrimitiveJsonTypeResolver#getClassesWhichCanResolve()}
     * More here  {@link JsonTypeResolversHierarchyResolver}
     * <p>
     * AbstractJsonType should contains converted data and provides implementation for "toStringJson()" method if you provide you own...
     * or you can return instance of existence one implementation in package 'pl.jalokim.propertiestojson.object'...  number, boolean, text, primitive array, json objects...
     * or simply convert Java object to instance ObjectJsonType by static method: public static AbstractJsonType convertFromObjectToJson(Object propertyValue, String propertyKey)
     * {@link ObjectFromTextJsonTypeResolver#convertFromObjectToJson(Object propertyValue, String propertyKey)}
     * Or if you want return null json object then return instance of {@link JsonNullReferenceType#NULL_OBJECT}
     *
     * @param primitiveJsonTypesResolver primitiveJsonTypesResolver
     * @param convertedValue             currently processing property value but as generic type
     * @param propertyKey                currently processing property key
     * @return optional value
     */

    @Override
    public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                   LocalDate convertedValue,
                                                   String propertyKey) {
        if(asTimestampInUTC) {
            return new NumberJsonType(convertedValue.atStartOfDay(ZoneOffset.UTC).toEpochSecond());
        }
        return ObjectFromTextJsonTypeResolver.convertFromObjectToJson(convertedValue, propertyKey);
    }
}
