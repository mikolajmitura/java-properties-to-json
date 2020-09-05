package pl.jalokim.propertiestojson.resolvers.primitives.object;

import java.util.Optional;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.NumberJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class NumberToJsonTypeConverter extends AbstractObjectToJsonTypeConverter<Number> {

    @Override
    public Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
        Number convertedValue,
        String propertyKey) {
        return Optional.of(new NumberJsonType(convertedValue));
    }
}
