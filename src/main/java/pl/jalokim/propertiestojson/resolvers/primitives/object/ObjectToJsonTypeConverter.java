package pl.jalokim.propertiestojson.resolvers.primitives.object;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.JsonNullReferenceType;
import pl.jalokim.propertiestojson.object.SkipJsonField;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.hierarchy.JsonTypeResolversHierarchyResolver;

import java.util.List;
import java.util.Optional;

/**
 * You can extends {@link AbstractObjectToJsonTypeConverter} which have implemented {@link AbstractObjectToJsonTypeConverter#canResolveClass}
 * @param <T> Generic type of converter.
 */
public interface ObjectToJsonTypeConverter<T> extends HasGenericType<T> {

    @SuppressWarnings("unchecked")
    default Optional<AbstractJsonType> returnOptionalJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey) {
        return convertToJsonTypeOrEmpty(primitiveJsonTypesResolver, (T) propertyValue, propertyKey);
    }

    /**
     * This method will be called in second phase conversion step (from some java Object to some implementation of AbstractJsonType)
     * it will be called during read properties from Map&lt;String,Object&gt;, Properties (without first processing step) or after first
     * conversion phase (while reading properties from file, Map&lt;String,String&gt;, inputStream)
     * <p>
     * But converters order (provided in PropertiesToJsonConverter constructor for List&lt;ObjectToJsonTypeConverter&gt; toJsonTypeResolvers or through PropertiesToJsonConverterBuilder) doesn't have importance here as in first processing phase,
     * it is important only when some of implementation of {@link pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter} can convert from the same java class, then order or the same converters type have matter.
     * But mostly hierarchy of classes plays a main role here
     * It looks for sufficient resolver, firstly will looks for exactly match class type provided by method {@link pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter#getClassesWhichCanResolve()}
     * if find a few resolvers for the same class then it will looks for firs converter which properly convert java object to AbstractJsonType (here converters order does it matter).
     * More here  {@link JsonTypeResolversHierarchyResolver}
     * <p>
     *
     * AbstractJsonType should contains converted data and provides implementation for "toStringJson()" method if you provide your own...
     * or you can return instance of existence one implementation in package 'pl.jalokim.propertiestojson.object'...  number, boolean, text, primitive array, json objects...
     * or simply convert Java object to instance ObjectJsonType by static method: public static AbstractJsonType convertFromObjectToJson(Object propertyValue, String propertyKey)
     * {@link SuperObjectToJsonTypeConverter#convertFromObjectToJson(Object propertyValue, String propertyKey)}
     * Or if you want return null json object then return instance of {@link JsonNullReferenceType#NULL_OBJECT}
     * Or if you want to skip this json leaf then return instance of {@link SkipJsonField#SKIP_JSON_FIELD} then it will not add it to json with null value.
     *
     * @param primitiveJsonTypesResolver primitiveJsonTypesResolver
     * @param convertedValue             currently processing property value but as generic type
     * @param propertyKey                currently processing property key
     * @return optional value
     */
    Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
                                                       T convertedValue,
                                                       String propertyKey);

    /**
     * Override it when want inform about what type of classes can coverts this converter.
     * @return list of classes.
     */
    List<Class<?>> getClassesWhichCanResolve();
}
