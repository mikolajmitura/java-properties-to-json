package pl.jalokim.propertiestojson.resolvers.primitives.object;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.List;

public interface ObjectToJsonTypeResolver<T> extends HasGenricType<T> {

    // TODO change for return OPTIONAL<AbstractJsonType>
    @SuppressWarnings("unchecked")
    default AbstractJsonType returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey) {
        return convertToJsonType(primitiveJsonTypesResolver, (T) propertyValue, propertyKey);
    }

    // TODO change for return OPTIONAL<AbstractJsonType> will help for few resolvers, check that order is necessary for the same classes...
    AbstractJsonType convertToJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                       T convertedValue,
                                                       String propertyKey);

    List<Class<?>> getClassesWhichCanResolve();
}
