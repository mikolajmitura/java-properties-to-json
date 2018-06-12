package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.DoubleJsonType;
import pl.jalokim.propertiestojson.object.PrimitiveJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import static pl.jalokim.propertiestojson.Constants.NORMAL_DOT;

public class DoubleJsonTypeResolver extends PrimitiveJsonTypeResolver {

    @Override
    public PrimitiveJsonType<?> returnJsonTypeWhenCanBeParsed(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
        try {
            if (propertyValue.contains(NORMAL_DOT)){
                return new DoubleJsonType(getDoubleNumber(propertyValue));
            }
        } catch (NumberFormatException exc) {
        }
        return null;
    }

    private static Double getDoubleNumber(String toParse) {
        return Double.valueOf(toParse);
    }
}
