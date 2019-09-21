package pl.jalokim.propertiestojson.resolvers.primitives.object;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;

public class ElementsToJsonTypeResolver extends AbstractObjectToJsonTypeResolver<Collection<?>> {

    @Override
    public AbstractJsonType convertToJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Collection<?> elements, String propertyKey) {
        return new ArrayJsonType(primitiveJsonTypesResolver, elements, null, propertyKey);
    }

    @Override
    public AbstractJsonType returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey) {
        if(hasElements(propertyValue.getClass())) {
            Collection<?> collection;
            if (propertyValue.getClass().isArray()) {
                Object[] rawArray = (Object[]) propertyValue;
                collection  = new ArrayList<>(Arrays.asList(rawArray));
            } else {
                collection = (Collection<?>) propertyValue;
            }
            return convertToJsonType(primitiveJsonTypesResolver, collection, propertyKey);
        }
        return convertToJsonType(primitiveJsonTypesResolver, singletonList(propertyValue), propertyKey);
    }

    public boolean hasElements(Class<?> classToTest) {
        return canResolveClass.isAssignableFrom(classToTest) || classToTest.isArray();
    }

    @Override
    public Class<?> resolveTypeOfResolver() {
        return Collection.class;
    }

    @Override
    public List<Class<?>> getClassesWhichCanResolve() {
        return Arrays.asList(Collection.class, Object[].class);
    }
}
