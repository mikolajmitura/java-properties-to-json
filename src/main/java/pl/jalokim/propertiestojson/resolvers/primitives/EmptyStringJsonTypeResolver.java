package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.Optional;

public class EmptyStringJsonTypeResolver extends StringJsonTypeResolver {

    private final static String EMPTY_VALUE = "";

    @Override
    public Optional<String> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
        String text = propertyValue.equals(EMPTY_VALUE) ? EMPTY_VALUE : null;
        return Optional.ofNullable(text);
    }
}
