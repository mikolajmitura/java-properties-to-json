package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.NumberJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberJsonTypeResolver extends PrimitiveJsonTypeResolver<Number> {

    private static BigInteger getIntegerNumber(String toParse) {
        return new BigInteger(toParse);
    }

    private static BigDecimal getDoubleNumber(String toParse) {
        return new BigDecimal(toParse);
    }

    @Override
    public Number returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
        try {
            return getIntegerNumber(propertyValue);
        } catch (NumberFormatException exc) {
        }
        try {
            return getDoubleNumber(propertyValue);
        } catch (NumberFormatException exc) {
        }
        return null;
    }

    @Override
    public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Number propertyValue) {
        return new NumberJsonType(propertyValue);
    }
}
