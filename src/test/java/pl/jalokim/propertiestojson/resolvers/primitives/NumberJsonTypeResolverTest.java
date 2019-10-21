package pl.jalokim.propertiestojson.resolvers.primitives;


import org.junit.Test;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.jalokim.utils.reflection.InvokableReflectionUtils.invokeMethod;

public class NumberJsonTypeResolverTest {

    @Test
    public void test_01_WillNotConvertAsNumber() {
        // given
        NumberJsonTypeResolver numberJsonTypeResolver = new NumberJsonTypeResolver();
        // when
        Optional<Number> numberOpt = invokeMethod(numberJsonTypeResolver, "returnConcreteValueWhenCanBeResolved",
                                                                           Arrays.asList(PrimitiveJsonTypesResolver.class, String.class, String.class),
                                                                           Arrays.asList(null, "01", "test"));
        // then
        assertThat(numberOpt.isPresent()).isFalse();
    }

}