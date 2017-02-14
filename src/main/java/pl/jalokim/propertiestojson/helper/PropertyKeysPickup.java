package pl.jalokim.propertiestojson.helper;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

public class PropertyKeysPickup {
    public List<String> getAllKeysFromProperties(Map<String, String> properties) {
        return Lists.newArrayList(properties.keySet());
    }
}
