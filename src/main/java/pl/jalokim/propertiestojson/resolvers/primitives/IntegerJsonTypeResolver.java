package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.IntegerJsonType;
import pl.jalokim.propertiestojson.object.PrimitiveJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class IntegerJsonTypeResolver extends PrimitiveJsonTypeResolver {

    @Override
    public PrimitiveJsonType<?> returnJsonTypeWhenCanBeParsed(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
        try {
           return new IntegerJsonType(getIntegerNumber(propertyValue));
        } catch (NumberFormatException exc) {
        }
        return null;
    }

    public static Integer getIntegerNumber(String toParse) {
        return Integer.valueOf(toParse);
    }
}
