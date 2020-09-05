package pl.jalokim.propertiestojson.resolvers.primitives.string;

import java.util.Optional;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class TextToCharacterResolver implements TextToConcreteObjectResolver<Character> {

    @Override
    public Optional<Character> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
        if (propertyValue.length() == 1) {
            return Optional.of(propertyValue.charAt(0));
        }
        return Optional.empty();
    }
}
