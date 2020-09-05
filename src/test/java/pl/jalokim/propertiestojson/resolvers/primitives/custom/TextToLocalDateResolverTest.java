package pl.jalokim.propertiestojson.resolvers.primitives.custom;

import static java.time.Month.AUGUST;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.Test;

public class TextToLocalDateResolverTest {

    @Test
    public void convertToLocalDateFromText() {
        // given
        TextToLocalDateResolver resolver = new TextToLocalDateResolver();
        // when
        Optional<LocalDate> localDateOptional = resolver.returnObjectWhenCanBeResolved(null, "04-08-2019", "some.field");
        // then
        assertThat(localDateOptional.isPresent()).isTrue();
        LocalDate localDate = localDateOptional.get();
        assertThat(localDate.getYear()).isEqualTo(2019);
        assertThat(localDate.getMonth()).isEqualTo(AUGUST);
        assertThat(localDate.getDayOfMonth()).isEqualTo(4);
    }

    @Test
    public void convertToLocalDateFromTextForAnotherFormat() {
        // given
        TextToLocalDateResolver resolver = new TextToLocalDateResolver("dd/MM/yyyy");
        // when
        Optional<LocalDate> localDateOptional = resolver.returnObjectWhenCanBeResolved(null, "04/08/2019", "some.field");
        // then
        assertThat(localDateOptional.isPresent()).isTrue();
        LocalDate localDate = localDateOptional.get();
        assertThat(localDate.getYear()).isEqualTo(2019);
        assertThat(localDate.getMonth()).isEqualTo(AUGUST);
        assertThat(localDate.getDayOfMonth()).isEqualTo(4);
    }

    @Test
    public void notConvertToLocalDateFromTextForAnotherFormat() {
        // given
        TextToLocalDateResolver resolver = new TextToLocalDateResolver("dd/MM/yyyy");
        // when
        Optional<LocalDate> localDate = resolver.returnObjectWhenCanBeResolved(null, "m/08/2019", "some.field");
        // then
        assertThat(localDate.isPresent()).isFalse();
    }

}