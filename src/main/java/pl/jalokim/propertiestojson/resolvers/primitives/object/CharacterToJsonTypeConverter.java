package pl.jalokim.propertiestojson.resolvers.primitives.object;

import java.util.Optional;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.StringJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class CharacterToJsonTypeConverter extends AbstractObjectToJsonTypeConverter<Character> {

    @Override
    public Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
        Character convertedValue,
        String propertyKey) {
        return Optional.of(new StringJsonType(convertedValue.toString()));
    }
}
