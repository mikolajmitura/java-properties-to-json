package pl.jalokim.propertiestojson.resolvers.primitives.object;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.JsonNullReferenceType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class NullToJsonTypeConverter extends AbstractObjectToJsonTypeResolver<JsonNullReferenceType> {

    public static final NullToJsonTypeConverter NULL_TO_JSON_RESOLVER = new NullToJsonTypeConverter();

    @Override
    public AbstractJsonType convertToJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, JsonNullReferenceType convertedValue, String propertyKey) {
        return convertedValue;
    }
}
