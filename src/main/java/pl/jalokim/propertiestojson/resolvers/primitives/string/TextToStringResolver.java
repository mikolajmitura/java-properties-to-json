package pl.jalokim.propertiestojson.resolvers.primitives.string;

import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.Optional;

public class TextToStringResolver implements TextToConcreteObjectResolver<String> {

    public static final TextToStringResolver TO_STRING_RESOLVER = new TextToStringResolver();

    @Override
    public Optional<String> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                          String propertyValue,
                                                          String propertyKey) {
        return Optional.ofNullable(propertyValue);
    }
}
