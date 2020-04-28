package pl.jalokim.propertiestojson.helper;


import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertiesWithInsertOrderTest {

    @Test
    public void verifyThatOrderIsLikeOrderOfInsertions() {
        // when
        Properties properties = new PropertiesWithInsertOrder();
        properties.put("field.sdfgs", 1);
        properties.put("fsdfgield.g", 2);
        properties.put("sdfgs.field3", 3);
        properties.put("nextField.sdfg", 4);
        properties.put("dfgsf.field5", 5);
        // then
        AtomicInteger currentValue = new AtomicInteger(0);
        properties.keySet().forEach(key -> {
            assertThat(properties.get(key)).isEqualTo(currentValue.incrementAndGet());
        });
        assertThat(currentValue.get()).isEqualTo(5);
    }
}