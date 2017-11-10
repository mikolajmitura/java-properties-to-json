package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.IntegerJsonType;
import pl.jalokim.propertiestojson.object.PrimitiveJsonType;

public class IntegerJsonTypeResolver extends PrimitiveJsonTypeResolver {

    @Override
    public PrimitiveJsonType<?> returnPrimitiveJsonTypeWhenIsGivenType(String propertyValue) {
        try {
           return new IntegerJsonType(getIntegerNumber(propertyValue));
        } catch (Exception exc) {
        }
        return null;
    }

    public static Integer getIntegerNumber(String toParse) {
        return Integer.valueOf(toParse);
    }
}
