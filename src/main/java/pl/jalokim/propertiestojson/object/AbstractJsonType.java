package pl.jalokim.propertiestojson.object;

/**
 * It represents abstraction for json element.
 */
public abstract class AbstractJsonType {

    /**
     * This one simply concatenate to rest of json.
     * Simply speaking when will not converted then will not create whole json correctly.
     * @return string for part of json.
     */
    public abstract String toStringJson();

    @Override
    public final String toString() {
        return toStringJson();
    }
}
