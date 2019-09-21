package pl.jalokim.propertiestojson.resolvers.primitives.string;

import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.Optional;

import static pl.jalokim.propertiestojson.resolvers.primitives.utils.JsonObjectHelper.convertToAbstractJsonType;
import static pl.jalokim.propertiestojson.resolvers.primitives.utils.JsonObjectHelper.hasJsonArraySignature;
import static pl.jalokim.propertiestojson.resolvers.primitives.utils.JsonObjectHelper.hasJsonObjectSignature;
import static pl.jalokim.propertiestojson.resolvers.primitives.utils.JsonObjectHelper.toJsonElement;

public class TextToObjectResolver implements TextToConcreteObjectResolver {

    @Override
    public Optional<Object> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
        if(hasJsonObjectSignature(propertyValue) || hasJsonArraySignature(propertyValue)) {
            try {
                return Optional.ofNullable(convertToAbstractJsonType(toJsonElement(propertyValue), propertyKey));
            } catch(Exception exception) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
