package pl.jalokim.propertiestojson.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PropertyKeysPickupOrderedForTest extends PropertyKeysPickup{

    private List<String> mockKeys = new ArrayList<>();

    public void setUpMockKeys(String... keys){
        Arrays.stream(keys).forEach(mockKeys::add);
    }

    @Override
    public List<String> getAllKeysFromProperties(Map<String, String> properties) {
        return mockKeys;
    }
}
