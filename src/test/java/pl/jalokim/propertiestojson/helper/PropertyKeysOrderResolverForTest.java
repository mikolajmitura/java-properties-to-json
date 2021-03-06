package pl.jalokim.propertiestojson.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PropertyKeysOrderResolverForTest extends PropertyKeysOrderResolver {

    private List<String> mockKeys = new ArrayList<>();

    public void setUpMockKeys(String... keys) {
        mockKeys.addAll(Arrays.asList(keys));
    }

    @Override
    public List<String> getKeysInExpectedOrder(Map<String, ?> properties) {
        for (String mockKey : mockKeys) {
            if (properties.get(mockKey) == null) {
                throw new RuntimeException("cannot find key: " + mockKey + " in test keys indexing!");
            }
        }
        return mockKeys;
    }
}
