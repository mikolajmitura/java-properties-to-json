package pl.jalokim.propertiestojson.resolvers.primitives.example;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.NumberJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.ObjectFromTextJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/*
Class only for check backward compatibility with extend of PrimitiveJsonTypeResolver...
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
