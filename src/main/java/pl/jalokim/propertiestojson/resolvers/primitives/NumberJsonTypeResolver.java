package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.NumberJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

public class NumberJsonTypeResolver extends PrimitiveJsonTypeResolver<Number> {



    private static BigInteger getIntegerNumber(String toParse) {
        return new BigInteger(toParse);
    }

    private static BigDecimal getDoubleNumber(String toParse) {
        return new BigDecimal(toParse);
    }

    @Override
    public Optional<Number> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
        return Optional.ofNullable(convertToNumber(propertyValue));
    }

    @Override
    public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Number propertyValue, String propertyKey) {
        return new NumberJsonType(propertyValue);
    }

    public static Number convertToNumber(String propertyValue) {
        Number number = convertToNumberFromText(propertyValue);
        if (number!= null && number.toString().equals(propertyValue)) {
            return number;
        }
        return null;
    }

    private static Number convertToNumberFromText(String propertyValue) {

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
}
