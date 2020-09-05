package pl.jalokim.propertiestojson.resolvers.primitives.string;

import static pl.jalokim.propertiestojson.object.JsonNullReferenceType.NULL_OBJECT;
import static pl.jalokim.propertiestojson.object.JsonNullReferenceType.NULL_VALUE;

import java.util.Optional;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class TextToJsonNullReferenceResolver implements TextToConcreteObjectResolver<Object> {

    public static final TextToJsonNullReferenceResolver TEXT_TO_NULL_JSON_RESOLVER = new TextToJsonNullReferenceResolver();

    @Override
    public Optional<Object> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
        if (propertyValue == null || propertyValue.equals(NULL_VALUE)) {
            return Optional.of(NULL_OBJECT);
        }
        return Optional.empty();
    }
}
