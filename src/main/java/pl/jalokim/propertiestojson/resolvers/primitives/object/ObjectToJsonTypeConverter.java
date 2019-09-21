package pl.jalokim.propertiestojson.resolvers.primitives.object;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.List;
import java.util.Optional;

public interface ObjectToJsonTypeConverter<T> extends HasGenricType<T> {

    @SuppressWarnings("unchecked")
    default Optional<AbstractJsonType> returnOptionalJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey) {
        return convertToJsonTypeOrEmpty(primitiveJsonTypesResolver, (T) propertyValue, propertyKey);
    }

    Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                       T convertedValue,
                                                       String propertyKey);

    List<Class<?>> getClassesWhichCanResolve();
}
