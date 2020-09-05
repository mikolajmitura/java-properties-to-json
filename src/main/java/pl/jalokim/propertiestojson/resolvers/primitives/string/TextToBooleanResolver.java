package pl.jalokim.propertiestojson.resolvers.primitives.string;

import java.util.Optional;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

public class TextToBooleanResolver implements TextToConcreteObjectResolver<Boolean> {

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    private static Boolean getBoolean(String value) {
        return Boolean.valueOf(value);
    }

    @Override
    public Optional<Boolean> returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
        if (TRUE.equalsIgnoreCase(propertyValue) || FALSE.equalsIgnoreCase(propertyValue)) {
            return Optional.of(getBoolean(propertyValue));
        }
        return Optional.empty();
    }
}
