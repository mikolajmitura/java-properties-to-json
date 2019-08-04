package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.JsonNullReferenceType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.Optional;

import static pl.jalokim.propertiestojson.object.JsonNullReferenceType.NULL_OBJECT;
import static pl.jalokim.propertiestojson.object.JsonNullReferenceType.NULL_VALUE;

public class JsonNullReferenceTypeResolver extends PrimitiveJsonTypeResolver<JsonNullReferenceType> {

    public static final JsonNullReferenceTypeResolver NULL_RESOLVER = new JsonNullReferenceTypeResolver();

    @Override
    public Optional<JsonNullReferenceType> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
        if (propertyValue == null || isStringType(propertyValue) && propertyValue.equals(NULL_VALUE)) {
            return Optional.of(NULL_OBJECT);
        }
        return Optional.empty();
    }

    @Override
    public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, JsonNullReferenceType propertyValue, String propertyKey) {
        return propertyValue;
    }

    private Boolean isStringType(Object value) {
        return String.class.isAssignableFrom(value.getClass());
    }
}
