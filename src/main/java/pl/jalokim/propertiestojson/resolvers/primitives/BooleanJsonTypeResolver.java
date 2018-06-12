package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.BooleanJsonType;
import pl.jalokim.propertiestojson.object.PrimitiveJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class BooleanJsonTypeResolver extends PrimitiveJsonTypeResolver{

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Override
    public PrimitiveJsonType<?> returnJsonTypeWhenCanBeParsed(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
        if (TRUE.equalsIgnoreCase(propertyValue) || FALSE.equalsIgnoreCase(propertyValue)){
            return new BooleanJsonType(getBoolean(propertyValue));
        }
        return null;
    }

    private static Boolean getBoolean(String toParse) {
        return Boolean.valueOf(toParse);
    }
}
