package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.resolvers.primitives.delegator.PrimitiveJsonTypeDelegatorResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.object.NumberToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToNumberResolver;

@Deprecated
public class NumberJsonTypeResolver extends PrimitiveJsonTypeDelegatorResolver<Number> {

    public NumberJsonTypeResolver() {
        super(new TextToNumberResolver(), new NumberToJsonTypeConverter());
    }
}
