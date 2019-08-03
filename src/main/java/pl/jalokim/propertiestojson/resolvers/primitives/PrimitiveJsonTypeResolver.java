package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;

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
        throw new ParsePropertiesException("Cannot find generic type for resolver: " + getClass() +
                " Please override method resolveTypeOfResolver() for provide explicit class type");
    }

    public AbstractJsonType returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue) {
        return returnConcreteJsonType(primitiveJsonTypesResolver, (T) propertyValue);
    }

    public T returnConvertedValueForClearedText(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
        return returnConcreteValueWhenCanBeResolved(primitiveJsonTypesResolver,
                                                    propertyValue == null ? null : propertyValue.trim());
    }

    // TODO as argument should be PathMetadata
    protected abstract T returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue);

    public abstract AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, T propertyValue);

    public List<Class<?>> getClassesWhichCanResolve() {
        return Collections.singletonList(canResolveClass);
    }
 }
