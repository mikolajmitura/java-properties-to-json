package pl.jalokim.propertiestojson.resolvers.primitives.string;

import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.Optional;

public class TextToCharacterResolver implements TextToConcreteObjectResolver<Character> {

    @Override
    public Optional<Character> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
        if(propertyValue.length() == 1) {
            return Optional.of(propertyValue.charAt(0));
        }
        return Optional.empty();
    }
}
