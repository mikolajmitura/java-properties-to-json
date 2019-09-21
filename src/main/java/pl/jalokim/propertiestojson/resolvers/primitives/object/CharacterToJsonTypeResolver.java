package pl.jalokim.propertiestojson.resolvers.primitives.object;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.StringJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class CharacterToJsonTypeResolver extends AbstractObjectToJsonTypeResolver<Character> {

    @Override
    public AbstractJsonType convertToJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                              Character convertedValue,
                                              String propertyKey) {
        return new StringJsonType(convertedValue.toString());
    }
}
