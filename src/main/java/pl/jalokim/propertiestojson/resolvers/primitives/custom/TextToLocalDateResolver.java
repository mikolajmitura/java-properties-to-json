package pl.jalokim.propertiestojson.resolvers.primitives.custom;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver;

/**
 * results of this resolver you can see in those test classes:
 *
 * @see <a href="https://github.com/mikolajmitura/java-properties-to-json/blob/v5.1.0/src/test/java/pl/jalokim/propertiestojson/resolvers/primitives/custom/TextToLocalDateResolverTest.java">LocalDateTimeResolverTest</a>
 * @see <a href="https://github.com/mikolajmitura/java-properties-to-json/blob/v5.1.0/src/test/java/pl/jalokim/propertiestojson/resolvers/primitives/custom/LocalDateConvertersTest.java">LocalDateTimeResolverTest</a>
 */
public class TextToLocalDateResolver implements TextToConcreteObjectResolver<LocalDate> {

    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private final DateTimeFormatter formatter;

    public TextToLocalDateResolver() {
        this(DATE_FORMAT);
    }


    public TextToLocalDateResolver(String formatOfDate) {
        formatter = DateTimeFormatter.ofPattern(formatOfDate);
    }

    /**
     * This method will be called in first conversion phase if your condition is met then return concrete value of Object. if it doesn't meet its condition then
     * return Optional.empty() for allow go to others type resolver in order. This will be called only for read properties from Map&lt;String,String&gt;, File
     * with properties, InputStream with properties
     *
     * @param primitiveJsonTypesResolver primitiveJsonTypesResolver
     * @param propertyValue currently processing property value
     * @param propertyKey currently processing property key
     * @return optional value
     */

    @Override
    public Optional<LocalDate> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
        String propertyValue,
        String propertyKey) {
        try {
            return Optional.ofNullable(LocalDate.parse(propertyValue, formatter)); // if parse then will return LocalDate
        } catch (Exception ex) {
            return Optional.empty(); // if not, then allow another resolvers to crate java object from String
        }
    }
}
