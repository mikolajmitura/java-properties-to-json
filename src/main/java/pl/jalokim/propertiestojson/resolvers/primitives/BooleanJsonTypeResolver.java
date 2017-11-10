package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.BooleanJsonType;
import pl.jalokim.propertiestojson.object.PrimitiveJsonType;

public class BooleanJsonTypeResolver extends PrimitiveJsonTypeResolver{

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Override
    public PrimitiveJsonType<?> returnPrimitiveJsonTypeWhenIsGivenType(String propertyValue) {
        if (TRUE.equalsIgnoreCase(propertyValue) || FALSE.equalsIgnoreCase(propertyValue)){
            return new BooleanJsonType(getBoolean(propertyValue));
        }
        return null;
    }

    private static Boolean getBoolean(String toParse) {
        return Boolean.valueOf(toParse);
    }
}
