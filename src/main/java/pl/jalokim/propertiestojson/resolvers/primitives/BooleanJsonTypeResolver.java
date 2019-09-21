package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.resolvers.primitives.delegator.PrimitiveJsonTypeDelegatorResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.object.BooleanToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToBooleanResolver;

@Deprecated
public class BooleanJsonTypeResolver extends PrimitiveJsonTypeDelegatorResolver<Boolean> {

    public BooleanJsonTypeResolver() {
        super(new TextToBooleanResolver(), new BooleanToJsonTypeConverter());
    }
}
