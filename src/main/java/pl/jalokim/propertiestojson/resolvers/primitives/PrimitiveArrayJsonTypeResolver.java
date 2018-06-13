package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import static pl.jalokim.propertiestojson.Constants.SIMPLE_ARRAY_DELIMETER;

public class PrimitiveArrayJsonTypeResolver extends PrimitiveJsonTypeResolver {

    private final String arrayElementSeparator;

    public PrimitiveArrayJsonTypeResolver() {
        this.arrayElementSeparator = SIMPLE_ARRAY_DELIMETER;
    }

    public PrimitiveArrayJsonTypeResolver(String arrayElementSeparator) {
        this.arrayElementSeparator = arrayElementSeparator;
    }

    @Override
    public AbstractJsonType returnJsonTypeWhenCanBeParsed(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
        if (isSimpleArray(propertyValue)){
            return new ArrayJsonType(primitiveJsonTypesResolver, propertyValue.split(arrayElementSeparator));
        }
        return null;
    }

    private boolean isSimpleArray(String propertyValue) {
        return propertyValue.contains(arrayElementSeparator);
    }
}
