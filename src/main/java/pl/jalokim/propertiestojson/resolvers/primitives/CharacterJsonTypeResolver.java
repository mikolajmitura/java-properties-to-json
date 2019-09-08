package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.StringJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.Optional;

public class CharacterJsonTypeResolver extends PrimitiveJsonTypeResolver<Character> {

    @Override
    protected Optional<Character> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                                       String propertyValue,
                                                                       String propertyKey) {
        if (propertyValue.length() == 1) {
            return Optional.of(propertyValue.charAt(0));
        }
        return Optional.empty();
    }

    @Override
    public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                   Character convertedValue,
                                                   String propertyKey) {
        return new StringJsonType(convertedValue.toString());
    }
}