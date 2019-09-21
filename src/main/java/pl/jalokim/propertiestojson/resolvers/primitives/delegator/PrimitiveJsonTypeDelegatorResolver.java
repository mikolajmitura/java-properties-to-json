package pl.jalokim.propertiestojson.resolvers.primitives.delegator;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.object.AbstractObjectToJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver;
import pl.jalokim.propertiestojson.util.ReflectionUtils;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class PrimitiveJsonTypeDelegatorResolver<T> extends PrimitiveJsonTypeResolver<T> {

    private final TextToConcreteObjectResolver toObjectResolver;
    private final AbstractObjectToJsonTypeResolver toJsonResolver;

    public PrimitiveJsonTypeDelegatorResolver(TextToConcreteObjectResolver toObjectResolver,
                                              AbstractObjectToJsonTypeResolver toJsonResolver) {
        this.toObjectResolver = toObjectResolver;
        this.toJsonResolver = toJsonResolver;
        ReflectionUtils.setValue(this, "canResolveClass", resolveTypeOfResolver());
    }

    @Override
    public Class<?> resolveTypeOfResolver() {
        if (toJsonResolver != null) {
            return toJsonResolver.resolveTypeOfResolver();
        }
        return null;
    }

    @Override
    public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                   T convertedValue,
                                                   String propertyKey) {
        return toJsonResolver.returnJsonType(primitiveJsonTypesResolver, convertedValue, propertyKey);
    }

    @Override
    protected Optional<T> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                               String propertyValue,
                                                               String propertyKey) {
        Optional<Object> optionalObject = toObjectResolver.returnObjectWhenCanBeResolved(primitiveJsonTypesResolver,
                                                                            propertyValue, propertyKey);
        return optionalObject.map(o -> (T) o);
    }

    @Override
    public List<Class<?>> getClassesWhichCanResolve() {
        return toJsonResolver.getClassesWhichCanResolve();
    }
}
