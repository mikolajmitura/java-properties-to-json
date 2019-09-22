package pl.jalokim.propertiestojson.resolvers.primitives.string;

import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.Optional;

public interface TextToConcreteObjectResolver<T> {

    default Optional<T> returnConvertedValueForClearedText(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
        return returnObjectWhenCanBeResolved(primitiveJsonTypesResolver,
                                                    propertyValue == null ? null : propertyValue.trim(), propertyKey);
    }

    /**
     * This method will be called in first phase processing step (from raw text to some object)
     * if your condition is true then return Optional of concrete value of Object.
     * if it not meets your's condition then return Optional.empty() for allow go to others type resolver in order.
     * This will be called only for read properties from Map&lt;String,String&gt;, File with properties, InputStream with properties
     *
     * @param primitiveJsonTypesResolver primitiveJsonTypesResolver
     * @param propertyValue              currently processing property value
     * @param propertyKey                currently processing property key
     * @return optional value
     */
    Optional<T> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                                   String propertyValue,
                                                                   String propertyKey);
}
