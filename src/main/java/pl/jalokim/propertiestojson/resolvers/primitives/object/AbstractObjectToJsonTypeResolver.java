package pl.jalokim.propertiestojson.resolvers.primitives.object;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class AbstractObjectToJsonTypeResolver<T> implements ObjectToJsonTypeResolver<T> {

    protected final Class<?> canResolveClass = resolveTypeOfResolver();

    @Override
    public List<Class<?>> getClassesWhichCanResolve() {
        return Collections.singletonList(canResolveClass);
    }
}
