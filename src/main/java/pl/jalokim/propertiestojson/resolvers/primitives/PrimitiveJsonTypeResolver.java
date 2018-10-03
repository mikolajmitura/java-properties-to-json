package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.lang.reflect.ParameterizedType;

@SuppressWarnings("unchecked")
public abstract class PrimitiveJsonTypeResolver<T> {

    protected final Class<?> canResolveClass = resolveTypeOfResolver();

    protected Class<?> resolveTypeOfResolver() {
        Class<?> currentClass = getClass();
        while (currentClass != null) {
            try {
                return (Class<T>) ((ParameterizedType) currentClass
                        .getGenericSuperclass()).getActualTypeArguments()[0];
            } catch (Exception ccx) {
                currentClass = currentClass.getSuperclass();
            }
        }
        throw new ParsePropertiesException("Cannot find generic type for resolver: " + getClass());
    }

    public AbstractJsonType returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue) {
        return returnConcreteJsonType(primitiveJsonTypesResolver, (T) propertyValue);
    }

    public T returnConvertedValueForClearedText(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
        return returnConcreteValueWhenCanBeResolved(primitiveJsonTypesResolver,
                                                    propertyValue == null ? null : propertyValue.trim());
    }

    protected abstract T returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue);

    public abstract AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, T propertyValue);

    public boolean canResolveThisObject(Class<?> classToTest) {
        return canResolveClass.isAssignableFrom(classToTest);
    }

}
