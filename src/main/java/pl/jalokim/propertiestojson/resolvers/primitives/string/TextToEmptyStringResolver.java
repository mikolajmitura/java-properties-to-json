package pl.jalokim.propertiestojson.resolvers.primitives.string;

import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.Optional;

public class TextToEmptyStringResolver implements TextToConcreteObjectResolver {

    public static final TextToEmptyStringResolver EMPTY_TEXT_RESOLVER = new TextToEmptyStringResolver();
    private final static String EMPTY_VALUE = "";

    @Override
    public Optional<Object> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                          String propertyValue,
                                                          String propertyKey) {
        String text = propertyValue.equals(EMPTY_VALUE) ? EMPTY_VALUE : null;
        return Optional.ofNullable(text);
    }
}
