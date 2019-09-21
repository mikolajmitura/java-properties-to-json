package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.resolvers.primitives.delegator.PrimitiveJsonTypeDelegatorResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.object.StringToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToEmptyStringResolver;

@Deprecated
public class EmptyStringJsonTypeResolver extends PrimitiveJsonTypeDelegatorResolver<String> {

    public EmptyStringJsonTypeResolver() {
        super(new TextToEmptyStringResolver(), new StringToJsonTypeConverter());
    }
}
