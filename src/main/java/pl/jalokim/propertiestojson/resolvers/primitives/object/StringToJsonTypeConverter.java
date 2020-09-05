package pl.jalokim.propertiestojson.resolvers.primitives.object;

import java.util.Optional;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.StringJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class StringToJsonTypeConverter extends AbstractObjectToJsonTypeConverter<String> {

    public static final StringToJsonTypeConverter STRING_TO_JSON_RESOLVER = new StringToJsonTypeConverter();

    @Override
    public Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
        String convertedValue,
        String propertyKey) {
        return Optional.of(new StringJsonType(convertedValue));
    }
}
