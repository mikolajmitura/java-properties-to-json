package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.String.join;
import static java.util.Collections.singletonList;
import static pl.jalokim.propertiestojson.Constants.EMPTY_STRING;
import static pl.jalokim.propertiestojson.Constants.SIMPLE_ARRAY_DELIMITER;
import static pl.jalokim.propertiestojson.resolvers.primitives.ObjectFromTextJsonTypeResolver.hasJsonArraySignature;
import static pl.jalokim.propertiestojson.resolvers.primitives.ObjectFromTextJsonTypeResolver.isValidJsonObjectOrArray;

public class PrimitiveArrayJsonTypeResolver extends PrimitiveJsonTypeResolver<Collection<?>> {

    private final String arrayElementSeparator;
    private final boolean resolveTypeOfEachElement;

    public PrimitiveArrayJsonTypeResolver() {
        this.resolveTypeOfEachElement = true;
        this.arrayElementSeparator = SIMPLE_ARRAY_DELIMITER;
    }

    public PrimitiveArrayJsonTypeResolver(boolean resolveTypeOfEachElement) {
        this.resolveTypeOfEachElement = resolveTypeOfEachElement;
        this.arrayElementSeparator = SIMPLE_ARRAY_DELIMITER;
    }

    private boolean isSimpleArray(String propertyValue) {
        return propertyValue.contains(arrayElementSeparator) || hasJsonArraySignature(propertyValue);
    }

    @Override
    public Collection<?> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
        if(isSimpleArray(propertyValue) && !isValidJsonObjectOrArray(propertyValue)) {

            // TODO only array with comma, not real json array from array from text?
            if(hasJsonArraySignature(propertyValue)) {
                propertyValue = propertyValue
                        .replaceAll("]\\s*$", EMPTY_STRING)
                        .replaceAll("^\\s*\\[\\s*", EMPTY_STRING);
                String[] elements = propertyValue.split(arrayElementSeparator);
                List<String> clearedElements = new ArrayList<>();
                for(String element : elements) {
                    element = element.trim()
                                     .replaceAll("^\\s*\"\\s*", EMPTY_STRING)
                                     .replaceAll("\"\\s*$", EMPTY_STRING);
                    clearedElements.add(element);
                }
                propertyValue = join(arrayElementSeparator, clearedElements);
            }

            List<Object> elements = new ArrayList<>();
            for(String element : propertyValue.split(arrayElementSeparator)) {
                if(resolveTypeOfEachElement) {
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
        if(canResolveThisObject(propertyValue.getClass())) {
            return returnConcreteJsonType(primitiveJsonTypesResolver, (Collection<?>) propertyValue);
        }
        return returnConcreteJsonType(primitiveJsonTypesResolver, singletonList(propertyValue));
    }

    @Override
    public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Collection<?> elements) {
        return new ArrayJsonType(primitiveJsonTypesResolver, elements, null);
    }

    @Override
    protected Class<?> resolveTypeOfResolver() {
        return Collection.class;
    }

    public boolean canResolveThisObject(Class<?> classToTest) {
        return canResolveClass.isAssignableFrom(classToTest) || classToTest.isArray();
    }
}
