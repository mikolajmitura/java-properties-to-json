package pl.jalokim.propertiestojson.resolvers.primitives.string;

import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.join;
import static pl.jalokim.propertiestojson.Constants.EMPTY_STRING;
import static pl.jalokim.propertiestojson.Constants.SIMPLE_ARRAY_DELIMITER;
import static pl.jalokim.propertiestojson.resolvers.primitives.utils.JsonObjectHelper.hasJsonArraySignature;
import static pl.jalokim.propertiestojson.resolvers.primitives.utils.JsonObjectHelper.isValidJsonObjectOrArray;

public class TextToElementsResolver implements TextToConcreteObjectResolver {

    private final String arrayElementSeparator;
    private final boolean resolveTypeOfEachElement;

    public TextToElementsResolver() {
        this(true);
    }

    public TextToElementsResolver(boolean resolveTypeOfEachElement) {
        this.resolveTypeOfEachElement = resolveTypeOfEachElement;
        this.arrayElementSeparator = SIMPLE_ARRAY_DELIMITER;
    }

    @Override
    public Optional<Object> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
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
            return Optional.of(elements);
        }
        return Optional.empty();
    }

    private boolean isSimpleArray(String propertyValue) {
        return propertyValue.contains(arrayElementSeparator) || hasJsonArraySignature(propertyValue);
    }
}
