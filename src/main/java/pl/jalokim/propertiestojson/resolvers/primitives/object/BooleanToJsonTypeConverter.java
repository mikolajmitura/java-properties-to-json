package pl.jalokim.propertiestojson.resolvers.primitives.object;

import java.util.Optional;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.BooleanJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class BooleanToJsonTypeConverter extends AbstractObjectToJsonTypeConverter<Boolean> {

    @Override
    public Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
        Boolean convertedValue,
        String propertyKey) {
        return Optional.of(new BooleanJsonType(convertedValue));
    }
}
