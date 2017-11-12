package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.PrimitiveJsonType;
import pl.jalokim.propertiestojson.object.StringJsonType;

public class StringJsonTypeResolver extends PrimitiveJsonTypeResolver {

    @Override
    public PrimitiveJsonType<?> returnPrimitiveJsonTypeWhenIsGivenType(String propertyValue) {
        return new StringJsonType(propertyValue);
    }
}
