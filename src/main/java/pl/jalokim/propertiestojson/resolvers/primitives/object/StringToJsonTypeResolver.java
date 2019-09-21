package pl.jalokim.propertiestojson.resolvers.primitives.object;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.StringJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.Optional;

public class StringToJsonTypeResolver extends AbstractObjectToJsonTypeResolver<String> {

    public static final StringToJsonTypeResolver STRING_TO_JSON_RESOLVER = new StringToJsonTypeResolver();

    @Override
    public Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                              String convertedValue,
                                              String propertyKey) {
        return Optional.of(new StringJsonType(convertedValue));
    }
}
