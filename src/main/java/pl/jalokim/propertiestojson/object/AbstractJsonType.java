package pl.jalokim.propertiestojson.object;

public abstract class AbstractJsonType {
    public abstract String toStringJson();

    @Override
    public String toString() {
        return toStringJson();
    }
}
