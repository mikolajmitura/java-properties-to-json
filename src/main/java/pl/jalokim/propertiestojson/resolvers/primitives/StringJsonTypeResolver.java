package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.resolvers.primitives.delegator.PrimitiveJsonTypeDelegatorResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.object.StringToJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToStringResolver;

@Deprecated
public class StringJsonTypeResolver extends PrimitiveJsonTypeDelegatorResolver<String> {

    public StringJsonTypeResolver() {
        super(new TextToStringResolver(), new StringToJsonTypeResolver());
    }
}
