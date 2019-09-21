package pl.jalokim.propertiestojson.resolvers.primitives.object;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.BooleanJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class BooleanToJsonTypeResolver extends AbstractObjectToJsonTypeResolver<Boolean> {

    @Override
    public AbstractJsonType convertToJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                              Boolean convertedValue,
                                              String propertyKey) {
        return new BooleanJsonType(convertedValue);
    }
}
