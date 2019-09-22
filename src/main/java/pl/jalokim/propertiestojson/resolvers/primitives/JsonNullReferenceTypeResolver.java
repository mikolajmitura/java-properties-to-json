package pl.jalokim.propertiestojson.resolvers.primitives;

import pl.jalokim.propertiestojson.object.JsonNullReferenceType;
import pl.jalokim.propertiestojson.resolvers.primitives.delegator.PrimitiveJsonTypeDelegatorResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.object.NullToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToJsonNullReferenceResolver;

@Deprecated
public class JsonNullReferenceTypeResolver extends PrimitiveJsonTypeDelegatorResolver<JsonNullReferenceType> {

    public JsonNullReferenceTypeResolver() {
        super(new TextToJsonNullReferenceResolver(), new NullToJsonTypeConverter());
    }
}
