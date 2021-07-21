package pl.jalokim.propertiestojson.resolvers.primitives.object;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class AbstractObjectToJsonTypeConverter<T> implements ObjectToJsonTypeConverter<T> {

    protected final Class<?> classesWhichCanResolved = resolveTypeOfResolver();

    /**
     * Inform about that certain converter can convert from generic type.
     *
     * @return list of classes from which can convert to json object/element.
     */
    @Override
    public List<Class<?>> getClassesWhichCanResolve() {
        return Collections.singletonList(classesWhichCanResolved);
    }
}
