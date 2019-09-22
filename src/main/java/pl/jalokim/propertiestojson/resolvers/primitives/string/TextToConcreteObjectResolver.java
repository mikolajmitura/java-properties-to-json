package pl.jalokim.propertiestojson.resolvers.primitives.string;

import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.Optional;

public interface TextToConcreteObjectResolver<T> {

    default Optional<T> returnConvertedValueForClearedText(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
        return returnObjectWhenCanBeResolved(primitiveJsonTypesResolver,
                                                    propertyValue == null ? null : propertyValue.trim(), propertyKey);
    }

    Optional<T> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                                   String propertyValue,
                                                                   String propertyKey);
}
