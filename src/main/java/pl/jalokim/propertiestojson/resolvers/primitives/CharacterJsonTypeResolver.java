package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.resolvers.primitives.delegator.PrimitiveJsonTypeDelegatorResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.object.CharacterToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToCharacterResolver;

@Deprecated
public class CharacterJsonTypeResolver extends PrimitiveJsonTypeDelegatorResolver<Character> {

    public CharacterJsonTypeResolver() {
        super(new TextToCharacterResolver(), new CharacterToJsonTypeConverter());
    }
}