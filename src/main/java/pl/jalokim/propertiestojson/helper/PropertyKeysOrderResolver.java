package pl.jalokim.propertiestojson.helper;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

public class PropertyKeysOrderResolver {
    public List<String> getKeysInExpectedOrder(Map<String, Object> properties) {
        return Lists.newArrayList(properties.keySet());
    }
}
