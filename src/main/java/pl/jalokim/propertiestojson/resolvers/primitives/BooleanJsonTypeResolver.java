package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.BooleanJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class BooleanJsonTypeResolver extends PrimitiveJsonTypeResolver<Boolean>{

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    private static Boolean getBoolean(String value) {
        return Boolean.valueOf(value);
    }

    @Override
    public Boolean returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
        if (TRUE.equalsIgnoreCase(propertyValue) || FALSE.equalsIgnoreCase(propertyValue)){
            return getBoolean(propertyValue);
        }
        return null;
    }

    @Override
    public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Boolean propertyValue) {
        return new BooleanJsonType(propertyValue);
    }
}
