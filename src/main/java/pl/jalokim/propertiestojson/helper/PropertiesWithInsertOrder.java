package pl.jalokim.propertiestojson.helper;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Implementation for Properties which stores keys in the same order like were put. It supports order only in {@link #forEach(BiConsumer)} and {@link #keySet()}
 * methods.
 */
public class PropertiesWithInsertOrder extends Properties {

    private static final long serialVersionUID = 1L;

    private final Set<Object> orderedKeys = new LinkedHashSet<>();

    @Override
    public synchronized Object put(Object key, Object value) {
        orderedKeys.add(key);
        return super.put(key, value);
    }

    @Override
    public synchronized boolean remove(Object key, Object value) {
        boolean removed = super.remove(key, value);
        if (removed) {
            orderedKeys.remove(key);
        }
        return removed;
    }

    @Override
    public synchronized Object remove(Object key) {
        orderedKeys.remove(key);
        return super.remove(key);
    }

    @Override
    public synchronized void forEach(BiConsumer<? super Object, ? super Object> action) {
        keySet().forEach(key -> action.accept(key, get(key)));
    }

    @Override
    public synchronized Set<Object> keySet() {
        return orderedKeys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PropertiesWithInsertOrder)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PropertiesWithInsertOrder that = (PropertiesWithInsertOrder) o;
        return Objects.equals(orderedKeys, that.orderedKeys);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), orderedKeys);
    }
}
