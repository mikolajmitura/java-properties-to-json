package pl.jalokim.propertiestojson.helper;

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Implementation for Properties which stores keys in the same order like were put.
 */
public class PropertiesWithInsertOrder extends Properties {

    private final Set<Object> orderedKeys = new LinkedHashSet<>();

    @Override
    public synchronized Object put(Object key, Object value) {
        orderedKeys.add(key);
        return super.put(key, value);
    }

    @Override
    public Set<Object> keySet() {
        return orderedKeys;
    }
}
