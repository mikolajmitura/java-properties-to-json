package pl.jalokim.propertiestojson.resolvers.primitives.object;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.NumberJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class NumberToJsonTypeResolver extends AbstractObjectToJsonTypeResolver<Number> {

    @Override
    public AbstractJsonType convertToJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                              Number convertedValue,
                                              String propertyKey) {
        return new NumberJsonType(convertedValue);
    }
}
