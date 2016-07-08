package pl.jalokim.propertiestojson.helper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PropertyKeysPickup {
    public List<String> getAllKeysFromProperties(Map<String,String> properties){
        return properties.keySet().stream().collect(Collectors.toList());
    }
}
