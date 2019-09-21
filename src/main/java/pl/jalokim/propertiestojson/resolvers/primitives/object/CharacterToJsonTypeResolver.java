package pl.jalokim.propertiestojson.resolvers.primitives.object;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.StringJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.Optional;

public class CharacterToJsonTypeResolver extends AbstractObjectToJsonTypeResolver<Character> {

    @Override
    public Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                              Character convertedValue,
                                              String propertyKey) {
        return Optional.of(new StringJsonType(convertedValue.toString()));
    }
}
