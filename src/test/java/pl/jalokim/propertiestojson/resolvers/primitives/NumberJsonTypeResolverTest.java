package pl.jalokim.propertiestojson.resolvers.primitives;


import org.junit.Test;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.util.ReflectionUtils;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class NumberJsonTypeResolverTest {

    @Test
    public void test_01_WillNotConvertAsNumber() {
        // given
        NumberJsonTypeResolver numberJsonTypeResolver = new NumberJsonTypeResolver();
        // when
        Optional<Number> numberOpt = ReflectionUtils.invokeMethod(numberJsonTypeResolver, "returnConcreteValueWhenCanBeResolved",
                                                                  Arrays.asList(PrimitiveJsonTypesResolver.class, String.class, String.class),
                                                                  Arrays.asList(null, "01", "test"));
        // then
        assertThat(numberOpt.isPresent()).isFalse();
    }

}