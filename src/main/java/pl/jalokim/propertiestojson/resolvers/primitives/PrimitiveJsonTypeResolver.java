package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;

public abstract class PrimitiveJsonTypeResolver {
    public abstract AbstractJsonType returnPrimitiveJsonTypeWhenIsGivenType(String propertyValue);
}
