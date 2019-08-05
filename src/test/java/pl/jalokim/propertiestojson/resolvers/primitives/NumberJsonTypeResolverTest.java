package pl.jalokim.propertiestojson.resolvers.primitives;


import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class NumberJsonTypeResolverTest {

    @Test
    public void test_01_WillNotConvertAsNumber() {
        // given
        NumberJsonTypeResolver numberJsonTypeResolver = new NumberJsonTypeResolver();
        // when
        Optional<Number> numberOpt = numberJsonTypeResolver.returnConcreteValueWhenCanBeResolved(null, "01", "test");
        // then
        assertThat(numberOpt.isPresent()).isFalse();
    }

}