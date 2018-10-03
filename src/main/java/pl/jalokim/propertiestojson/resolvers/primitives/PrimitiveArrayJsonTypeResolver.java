package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;
import static pl.jalokim.propertiestojson.Constants.SIMPLE_ARRAY_DELIMETER;

public class PrimitiveArrayJsonTypeResolver extends PrimitiveJsonTypeResolver<Collection<?>> {

    private final String arrayElementSeparator;

    public PrimitiveArrayJsonTypeResolver() {
        this.arrayElementSeparator = SIMPLE_ARRAY_DELIMETER;
    }

    public PrimitiveArrayJsonTypeResolver(String arrayElementSeparator) {
        this.arrayElementSeparator = arrayElementSeparator;
    }

    private boolean isSimpleArray(String propertyValue) {
        return propertyValue.contains(arrayElementSeparator);
    }

    @Override
    public Collection<?> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
        if (isSimpleArray(propertyValue)){
            List<Object> elements = new ArrayList<>();
            for (String element : propertyValue.split(arrayElementSeparator)) {
                elements.add(primitiveJsonTypesResolver.getResolvedObject(element));
            }
            return elements;
        }
        return null;
    }

    @Override
    public AbstractJsonType returnJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue) {
        if (canResolveThisObject(propertyValue.getClass())) {
            return returnConcreteJsonType(primitiveJsonTypesResolver, (Collection<?>) propertyValue);
        }
        return returnConcreteJsonType(primitiveJsonTypesResolver, singletonList(propertyValue));
    }

    @Override
    public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Collection<?> propertyValue) {
        return new ArrayJsonType(primitiveJsonTypesResolver, propertyValue);
    }

    @Override
    protected Class<?> resolveTypeOfResolver() {
        return Collection.class;
    }

    public boolean canResolveThisObject(Class<?> classToTest) {
        return canResolveClass.isAssignableFrom(classToTest) || classToTest.isArray();
    }
}
