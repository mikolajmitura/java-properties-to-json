package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.lang.String.join;
import static java.util.Collections.singletonList;
import static pl.jalokim.propertiestojson.Constants.EMPTY_STRING;
import static pl.jalokim.propertiestojson.Constants.SIMPLE_ARRAY_DELIMITER;
import static pl.jalokim.propertiestojson.resolvers.primitives.ObjectFromTextJsonTypeResolver.hasJsonArraySignature;
import static pl.jalokim.propertiestojson.resolvers.primitives.ObjectFromTextJsonTypeResolver.isValidJsonObjectOrArray;

/**
 * When given text contains ',' or text starts with '[' and ends with ']' in text then it tries split by comma and remove '[]' signs and then
 * every separated text tries convert to json value.
 * It will try resolve every types by provided resolvers in {@link pl.jalokim.propertiestojson.util.PropertiesToJsonConverter#PropertiesToJsonConverter(PrimitiveJsonTypeResolver...)}
 */
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
    public Collection<?> returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
        if(isSimpleArray(propertyValue) && !isValidJsonObjectOrArray(propertyValue)) {

            if(hasJsonArraySignature(propertyValue)) {
                propertyValue = propertyValue
                        .replaceAll("]\\s*$", EMPTY_STRING)
                        .replaceAll("^\\s*\\[\\s*", EMPTY_STRING);
                String[] elements = propertyValue.split(arrayElementSeparator);
                List<String> clearedElements = new ArrayList<>();
                for(String element : elements) {
                    element = element.trim();
                    clearedElements.add(element);
                }
                propertyValue = join(arrayElementSeparator, clearedElements);
            }

            List<Object> elements = new ArrayList<>();
            for(String element : propertyValue.split(arrayElementSeparator)) {
                if(resolveTypeOfEachElement) {
                    elements.add(primitiveJsonTypesResolver.getResolvedObject(element, propertyKey));
                } else {
                    elements.add(element);
                }
            }
            return elements;
        }
        return null;
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
            return returnConcreteJsonType(primitiveJsonTypesResolver, collection, propertyKey);
        }
        return returnConcreteJsonType(primitiveJsonTypesResolver, singletonList(propertyValue), propertyKey);
    }

    @Override
    public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Collection<?> elements, String propertyKey) {
        return new ArrayJsonType(primitiveJsonTypesResolver, elements, null, propertyKey);
    }

    @Override
    protected Class<?> resolveTypeOfResolver() {
        return Collection.class;
    }

    @Override
    public List<Class<?>> getClassesWhichCanResolve() {
        return Arrays.asList(Collection.class, Object[].class);
    }

    public boolean hasElements(Class<?> classToTest) {
        return canResolveClass.isAssignableFrom(classToTest) || classToTest.isArray();
    }
}
