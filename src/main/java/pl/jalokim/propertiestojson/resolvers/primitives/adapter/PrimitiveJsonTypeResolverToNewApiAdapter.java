package pl.jalokim.propertiestojson.resolvers.primitives.adapter;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver;
import pl.jalokim.propertiestojson.util.ReflectionUtils;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static pl.jalokim.propertiestojson.util.ReflectionUtils.invokeMethod;

@SuppressWarnings("unchecked")
public final class PrimitiveJsonTypeResolverToNewApiAdapter extends PrimitiveJsonTypeResolver
        implements TextToConcreteObjectResolver, ObjectToJsonTypeResolver {

    private final PrimitiveJsonTypeResolver oldImplementation;

    public PrimitiveJsonTypeResolverToNewApiAdapter(PrimitiveJsonTypeResolver oldImplementation) {
        this.oldImplementation = oldImplementation;
        ReflectionUtils.setValue(this, "canResolveClass", resolveTypeOfResolver());
    }

    @Override // from PrimitiveJsonTypeResolver and ObjectToJsonTypeResolver
    public Class<?> resolveTypeOfResolver() {
        if (oldImplementation != null) {
            return oldImplementation.resolveTypeOfResolver();
        }
        return null;
    }

    @Override // from PrimitiveJsonTypeResolver and ObjectToJsonTypeResolver
    public AbstractJsonType returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                           Object propertyValue,
                                           String propertyKey) {
        return oldImplementation.returnJsonType(primitiveJsonTypesResolver,
                                                propertyValue,
                                                propertyKey);
    }

    @Override // from PrimitiveJsonTypeResolver
    protected Optional<Object> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                                    String propertyValue,
                                                                    String propertyKey) {
        return invokeMethod(oldImplementation, "returnConcreteValueWhenCanBeResolved",
                                            asList(PrimitiveJsonTypesResolver.class, String.class, String.class),
                                            asList(primitiveJsonTypesResolver, propertyValue, propertyKey));
    }

    @Override // from PrimitiveJsonTypeResolver
    public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                   Object convertedValue,
                                                   String propertyKey) {
        return oldImplementation.returnConcreteJsonType(primitiveJsonTypesResolver, convertedValue, propertyKey);
    }


    @Override // from TextToConcreteObjectResolver and PrimitiveJsonTypeResolver
    public Optional<Object> returnConvertedValueForClearedText(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                               String propertyValue,
                                                               String propertyKey) {
        Optional<?> optional = oldImplementation.returnConvertedValueForClearedText(primitiveJsonTypesResolver, propertyValue, propertyKey);
        return Optional.ofNullable(optional.orElse(null));
    }

    @Override // from TextToConcreteObjectResolver
    public Optional<Object> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                          String propertyValue,
                                                          String propertyKey) {
        Optional<?> optional = returnConcreteValueWhenCanBeResolved(primitiveJsonTypesResolver, propertyValue, propertyKey);
        return Optional.ofNullable(optional.orElse(null));
    }

    @Override // from ObjectToJsonTypeResolver
    public Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object convertedValue, String propertyKey) {
        return Optional.of(oldImplementation.returnJsonType(primitiveJsonTypesResolver, convertedValue, propertyKey));
    }

    @Override // from ObjectToJsonTypeResolver and PrimitiveJsonTypeResolver
    public List<Class<?>> getClassesWhichCanResolve() {
        return oldImplementation.getClassesWhichCanResolve();
    }

    public PrimitiveJsonTypeResolver getOldImplementation() {
        return oldImplementation;
    }
}
