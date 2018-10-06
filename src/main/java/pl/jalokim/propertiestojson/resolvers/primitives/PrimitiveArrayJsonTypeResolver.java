package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;
import static pl.jalokim.propertiestojson.Constants.SIMPLE_ARRAY_DELIMETER;
import static pl.jalokim.propertiestojson.resolvers.primitives.ObjectFromTextJsonTypeResolver.hasJsonArraySignature;

public class PrimitiveArrayJsonTypeResolver extends PrimitiveJsonTypeResolver<Collection<?>> {

    private final String arrayElementSeparator;
    private final boolean resolveTypeOfEachElement;

    public PrimitiveArrayJsonTypeResolver() {
        this.resolveTypeOfEachElement = true;
        this.arrayElementSeparator = SIMPLE_ARRAY_DELIMETER;
    }

    public PrimitiveArrayJsonTypeResolver(boolean resolveTypeOfEachElement) {
        this.resolveTypeOfEachElement = resolveTypeOfEachElement;
        this.arrayElementSeparator = SIMPLE_ARRAY_DELIMETER;
    }

    private boolean isSimpleArray(String propertyValue) {
        return propertyValue.contains(arrayElementSeparator) || hasJsonArraySignature(propertyValue);
    }

    @Override
    public Collection<?> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
        if (isSimpleArray(propertyValue)){
            List<Object> elements = new ArrayList<>();
            if (hasJsonArraySignature(propertyValue)) {
                return null;
            }
            for (String element : propertyValue.split(arrayElementSeparator)) {
                if (resolveTypeOfEachElement) {
                    elements.add(primitiveJsonTypesResolver.getResolvedObject(element));
                } else {
                    elements.add(element);
                }
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
