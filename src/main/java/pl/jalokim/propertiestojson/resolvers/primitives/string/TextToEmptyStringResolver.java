package pl.jalokim.propertiestojson.resolvers.primitives.string;

import java.util.Optional;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class TextToEmptyStringResolver implements TextToConcreteObjectResolver<String> {

    public static final TextToEmptyStringResolver EMPTY_TEXT_RESOLVER = new TextToEmptyStringResolver();
    private static final String EMPTY_VALUE = "";

    @Override
    public Optional<String> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
        String propertyValue,
        String propertyKey) {
        String text = EMPTY_VALUE.equals(propertyValue) ? EMPTY_VALUE : null;
        return Optional.ofNullable(text);
    }
}
