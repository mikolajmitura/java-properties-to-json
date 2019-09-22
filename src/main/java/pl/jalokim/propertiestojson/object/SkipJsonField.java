package pl.jalokim.propertiestojson.object;

import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver;

/**
 * Dummy object for notify that field with that value will not be added to json. Will not go to next resolver or converter.
 * It can be returned by {@link ObjectToJsonTypeConverter#convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver, Object, String)}
 * and can be returned by {@link TextToConcreteObjectResolver#returnObjectWhenCanBeResolved(PrimitiveJsonTypesResolver, String, String)}
 */
public final class SkipJsonField extends AbstractJsonType {

    public static final SkipJsonField SKIP_JSON_FIELD = new SkipJsonField();

    private SkipJsonField() {

    }

    @Override
    public String toStringJson() {
        throw new UnsupportedOperationException("This is not normal implementation of AbstractJsonType");
    }
}
