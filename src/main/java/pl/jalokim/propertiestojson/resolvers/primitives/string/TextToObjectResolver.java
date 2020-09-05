package pl.jalokim.propertiestojson.resolvers.primitives.string;

import static pl.jalokim.propertiestojson.resolvers.primitives.utils.JsonObjectHelper.convertToAbstractJsonType;
import static pl.jalokim.propertiestojson.resolvers.primitives.utils.JsonObjectHelper.hasJsonArraySignature;
import static pl.jalokim.propertiestojson.resolvers.primitives.utils.JsonObjectHelper.hasJsonObjectSignature;
import static pl.jalokim.propertiestojson.resolvers.primitives.utils.JsonObjectHelper.toJsonElement;

import java.util.Optional;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class TextToObjectResolver implements TextToConcreteObjectResolver<AbstractJsonType> {

    @Override
    public Optional<AbstractJsonType> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue,
        String propertyKey) {
        if (hasJsonObjectSignature(propertyValue) || hasJsonArraySignature(propertyValue)) {
            try {
                return Optional.ofNullable(convertToAbstractJsonType(toJsonElement(propertyValue), propertyKey));
            } catch (Exception exception) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
