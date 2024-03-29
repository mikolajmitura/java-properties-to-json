package pl.jalokim.propertiestojson.resolvers.primitives;

import java.util.Collection;
import pl.jalokim.propertiestojson.resolvers.primitives.delegator.PrimitiveJsonTypeDelegatorResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.object.ElementsToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToElementsResolver;

/**
 * When given text contains ',' or text starts with '[' and ends with ']' in text then it tries split by comma and remove '[]' signs and then every separated
 * text tries convert to json value. It will try resolve every types by provided resolvers
 * in {@link pl.jalokim.propertiestojson.util.PropertiesToJsonConverter#PropertiesToJsonConverter(PrimitiveJsonTypeResolver...)}
 */
@Deprecated
public class PrimitiveArrayJsonTypeResolver extends PrimitiveJsonTypeDelegatorResolver<Collection<?>> {

    public PrimitiveArrayJsonTypeResolver() {
        super(new TextToElementsResolver(), new ElementsToJsonTypeConverter());
    }

    public PrimitiveArrayJsonTypeResolver(boolean resolveTypeOfEachElement) {
        super(new TextToElementsResolver(resolveTypeOfEachElement), new ElementsToJsonTypeConverter());
    }
}
