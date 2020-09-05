package pl.jalokim.propertiestojson.resolvers.primitives;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.JsonNullReferenceType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.hierarchy.JsonTypeResolversHierarchyResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.adapter.InvokedFromAdapter;
import pl.jalokim.propertiestojson.resolvers.primitives.delegator.InvokedFromDelegator;
import pl.jalokim.propertiestojson.resolvers.primitives.object.HasGenericType;

@SuppressWarnings("unchecked")
@Deprecated
public abstract class PrimitiveJsonTypeResolver<T> implements HasGenericType<T> {

    protected final Class<?> canResolveClass = resolveTypeOfResolver();

    @InvokedFromAdapter
    @InvokedFromDelegator
    public AbstractJsonType returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey) {
        return returnConcreteJsonType(primitiveJsonTypesResolver, (T) propertyValue, propertyKey);
    }

    /**
     * This method will be called in second phase processing step (from some java Object to some implementation of AbstractJsonType) it will be called during
     * read properties from Map&lt;String,Object&gt;, Properties (without first processing step) or after first processing step (while reading properties from
     * file, Map&lt;String,String&gt;, inputStream)
     * <p>
     * But resolvers order (provided in PropertiesToJsonConverter(PrimitiveJsonTypeResolver... primitiveResolvers) constructor) doesn't have importance here as
     * in first processing phase. The hierarchy of classes plays a central role here It looks for sufficient resolver, firstly will looks for exactly match
     * class type provided by method {@link PrimitiveJsonTypeResolver#getClassesWhichCanResolve()} More here  {@link JsonTypeResolversHierarchyResolver}
     * <p>
     * AbstractJsonType should contains converted data and provides implementation for "toStringJson()" method if you provide you own... or you can return
     * instance of existence one implementation in package 'pl.jalokim.propertiestojson.object'...  number, boolean, text, primitive array, json objects... or
     * simply convert Java object to instance ObjectJsonType by static method: public static AbstractJsonType convertFromObjectToJson(Object propertyValue,
     * String propertyKey) {@link ObjectFromTextJsonTypeResolver#convertFromObjectToJson(Object propertyValue, String propertyKey)} Or if you want return null
     * json object then return instance of {@link JsonNullReferenceType#NULL_OBJECT}
     *
     * @param primitiveJsonTypesResolver primitiveJsonTypesResolver
     * @param convertedValue currently processing property value but as generic type
     * @param propertyKey currently processing property key
     * @return optional value
     */
    @InvokedFromAdapter
    public abstract AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, T convertedValue, String propertyKey);


    @InvokedFromAdapter
    @InvokedFromDelegator
    public Optional<T> returnConvertedValueForClearedText(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
        String propertyValue,
        String propertyKey) {
        return returnConcreteValueWhenCanBeResolved(primitiveJsonTypesResolver,
            propertyValue == null ? null : propertyValue.trim(), propertyKey);
    }

    /**
     * This method will be called in first phase processing step (from raw text to some object) if your condition is met then return concrete value of Object.
     * if it doesn't meet its condition then return Optional.empty() for allow go to others type resolver in order. This will be called only for read properties
     * from Map&lt;String,String&gt;, File with properties, InputStream with properties
     *
     * @param primitiveJsonTypesResolver primitiveJsonTypesResolver
     * @param propertyValue currently processing property value
     * @param propertyKey currently processing property key
     * @return optional value
     */
    @InvokedFromAdapter
    protected abstract Optional<T> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
        String propertyValue,
        String propertyKey);

    @InvokedFromAdapter
    @InvokedFromDelegator
    public List<Class<?>> getClassesWhichCanResolve() {
        return Collections.singletonList(canResolveClass);
    }
}
