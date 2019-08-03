package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class EmptyStringJsonTypeResolver extends StringJsonTypeResolver {

    private final static String EMPTY_VALUE = "";

    @Override
    public String returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
        return propertyValue.equals(EMPTY_VALUE) ? EMPTY_VALUE : null;
    }
}
