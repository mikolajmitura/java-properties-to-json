package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.BooleanJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.Optional;

public class BooleanJsonTypeResolver extends PrimitiveJsonTypeResolver<Boolean>{

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    private static Boolean getBoolean(String value) {
        return Boolean.valueOf(value);
    }

    @Override
    public Optional<Boolean> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
        if (TRUE.equalsIgnoreCase(propertyValue) || FALSE.equalsIgnoreCase(propertyValue)){
            return Optional.of(getBoolean(propertyValue));
        }
        return Optional.empty();
    }

    @Override
    public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Boolean propertyValue, String propertyKey) {
        return new BooleanJsonType(propertyValue);
    }
}
