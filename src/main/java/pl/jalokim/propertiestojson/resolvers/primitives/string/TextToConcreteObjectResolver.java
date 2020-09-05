package pl.jalokim.propertiestojson.resolvers.primitives.string;

import java.util.Optional;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public interface TextToConcreteObjectResolver<T> {

    default Optional<T> returnConvertedValueForClearedText(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
        return returnObjectWhenCanBeResolved(primitiveJsonTypesResolver,
            propertyValue == null ? null : propertyValue.trim(), propertyKey);
    }

    /**
     * This method will be called in first phase processing step (from raw text to some object) if your condition is met then return Optional of concrete value
     * of Object. if it doesn't meet its condition then return Optional.empty() for allow go to others type resolver in order. This will be called only for read
     * properties from Map&lt;String,String&gt;, File with properties, InputStream with properties
     *
     * @param primitiveJsonTypesResolver primitiveJsonTypesResolver
     * @param propertyValue currently processing property value
     * @param propertyKey currently processing property key
     * @return optional value
     */
    Optional<T> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
        String propertyValue,
        String propertyKey);
}
