package pl.jalokim.propertiestojson.helper;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class PropertiesWithInsertOrderTest {

    @Test
    public void verifyKeySetThatOrderIsLikeOrderOfInsertions() {
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

    @Test
    public void removePropertiesImpactToKeySetAndForeachAndKeys() {
        // given
        Properties properties = new PropertiesWithInsertOrder();
        properties.put("field.sdfgs", 1);
        properties.put("fsdfgield.g", 2);
        properties.put("sdfgs.field3", 3);
        properties.put("nextField.sdfg", 4);
        properties.put("dfgsf.field5", 5);
        // when
        properties.remove("field.sdfgs");
        properties.remove("sdfgs.field3", 4);
        properties.remove("dfgsf.field5", 5);
        // then
        List<Integer> expectedValues = Arrays.asList(2, 3, 4);
        Iterator<Integer> iterator1 = expectedValues.iterator();
        // key set
        properties.keySet().forEach(key ->
            assertThat(properties.get(key)).isEqualTo(iterator1.next())
        );
        Iterator<Integer> iterator2 = expectedValues.iterator();
        properties.forEach((key, value) ->
            assertThat(properties.get(key)).isEqualTo(iterator2.next())
        );
    }
}