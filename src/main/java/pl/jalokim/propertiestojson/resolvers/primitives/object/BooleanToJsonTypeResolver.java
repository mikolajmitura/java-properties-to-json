package pl.jalokim.propertiestojson.resolvers.primitives.object;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.BooleanJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.Optional;

public class BooleanToJsonTypeResolver extends AbstractObjectToJsonTypeResolver<Boolean> {

    @Override
    public Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                               Boolean convertedValue,
                                                               String propertyKey) {
        return Optional.of(new BooleanJsonType(convertedValue));
    }
}
