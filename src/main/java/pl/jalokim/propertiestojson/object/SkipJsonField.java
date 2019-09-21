package pl.jalokim.propertiestojson.object;

/**
 * Dummy object for notify that field with that value will not be added to json.
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
