package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.StringJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.Optional;

public class StringJsonTypeResolver extends PrimitiveJsonTypeResolver<String> {

    @Override
    public Optional<String> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
        return Optional.ofNullable(propertyValue);
    }

    @Override
    public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
        return new StringJsonType(propertyValue);
    }
}
