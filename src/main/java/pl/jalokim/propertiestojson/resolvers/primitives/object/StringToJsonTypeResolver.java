package pl.jalokim.propertiestojson.resolvers.primitives.object;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.StringJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class StringToJsonTypeResolver extends AbstractObjectToJsonTypeResolver<String> {

    public static final StringToJsonTypeResolver STRING_TO_JSON_RESOLVER = new StringToJsonTypeResolver();

    @Override
    public AbstractJsonType convertToJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                              String convertedValue,
                                              String propertyKey) {
        return new StringJsonType(convertedValue);
    }
}
