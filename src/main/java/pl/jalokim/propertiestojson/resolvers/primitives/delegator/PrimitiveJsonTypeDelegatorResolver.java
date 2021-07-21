package pl.jalokim.propertiestojson.resolvers.primitives.delegator;

import static pl.jalokim.utils.reflection.InvokableReflectionUtils.setValueForField;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Optional;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.PrimitiveJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.object.AbstractObjectToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver;

@SuppressWarnings("unchecked")
@SuppressFBWarnings("UR_UNINIT_READ_CALLED_FROM_SUPER_CONSTRUCTOR")
public class PrimitiveJsonTypeDelegatorResolver<T> extends PrimitiveJsonTypeResolver<T> {

    private final TextToConcreteObjectResolver toObjectResolver;
    private final AbstractObjectToJsonTypeConverter toJsonResolver;

    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public PrimitiveJsonTypeDelegatorResolver(TextToConcreteObjectResolver toObjectResolver,
        AbstractObjectToJsonTypeConverter toJsonResolver) {
        this.toObjectResolver = toObjectResolver;
        this.toJsonResolver = toJsonResolver;
        setValueForField(this, "typeWhichCanBeResolved", resolveTypeOfResolver());
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
        Optional<AbstractJsonType> optional = toJsonResolver.convertToJsonTypeOrEmpty(primitiveJsonTypesResolver,
            convertedValue,
            propertyKey);
        return optional.get();
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
