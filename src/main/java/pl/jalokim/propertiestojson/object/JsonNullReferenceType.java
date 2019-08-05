package pl.jalokim.propertiestojson.object;

public final class JsonNullReferenceType extends AbstractJsonType {

    public final static JsonNullReferenceType NULL_OBJECT = new JsonNullReferenceType();

    public final static String NULL_VALUE = "null";

    @Override
    public String toStringJson() {
        return NULL_VALUE;
    }
}
