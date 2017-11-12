package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;

import static pl.jalokim.propertiestojson.Constants.SIMPLE_ARRAY_DELIMETER;

public class PrimitiveArrayJsonType extends PrimitiveJsonTypeResolver {

    @Override
    public AbstractJsonType returnPrimitiveJsonTypeWhenIsGivenType(String propertyValue) {
        if (isSimpleArray(propertyValue)){
            return new ArrayJsonType(propertyValue.split(SIMPLE_ARRAY_DELIMETER));
        }
        return null;
    }

    private boolean isSimpleArray(String propertyValue) {
        return propertyValue.contains(SIMPLE_ARRAY_DELIMETER);
    }
}
