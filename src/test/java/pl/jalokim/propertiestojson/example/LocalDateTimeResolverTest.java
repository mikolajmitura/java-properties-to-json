package pl.jalokim.propertiestojson.example;


import org.junit.Test;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.NumberJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;

import java.time.LocalDate;
import java.util.Optional;

import static java.time.Month.AUGUST;
import static org.assertj.core.api.Assertions.assertThat;

public class LocalDateTimeResolverTest {

    @Test
    public void convertToLocalDateFromText() {
        // given
        LocalDateTimeResolver resolver = new LocalDateTimeResolver();
        // when
        Optional<LocalDate> localDate = resolver.returnConcreteValueWhenCanBeResolved(null, "04-08-2019", "some.field");
        // then
        assertThat(localDate.isPresent()).isTrue();
        assertThat(localDate.get().getYear()).isEqualTo(2019);
        assertThat(localDate.get().getMonth()).isEqualTo(AUGUST);
        assertThat(localDate.get().getDayOfMonth()).isEqualTo(4);
    }

    @Test
    public void convertToLocalDateFromTextForAnotherFormat() {
        // given
        LocalDateTimeResolver resolver = new LocalDateTimeResolver("dd/MM/yyyy");
        // when
        Optional<LocalDate> localDate = resolver.returnConcreteValueWhenCanBeResolved(null, "04/08/2019", "some.field");
        // then
        assertThat(localDate.isPresent()).isTrue();
        assertThat(localDate.get().getYear()).isEqualTo(2019);
        assertThat(localDate.get().getMonth()).isEqualTo(AUGUST);
        assertThat(localDate.get().getDayOfMonth()).isEqualTo(4);
    }

    @Test
    public void notConvertToLocalDateFromTextForAnotherFormat() {
        // given
        LocalDateTimeResolver resolver = new LocalDateTimeResolver("dd/MM/yyyy");
        // when
        Optional<LocalDate> localDate = resolver.returnConcreteValueWhenCanBeResolved(null, "m/08/2019", "some.field");
        // then
        assertThat(localDate.isPresent()).isFalse();
    }

    @Test
    public void convertLocalDateToUtcTimestamp() {
        // given
        LocalDate localDate = LocalDate.of(2019, 8, 4);
        LocalDateTimeResolver resolver = new LocalDateTimeResolver(true);
        // when
        AbstractJsonType jsonObject = resolver.returnConcreteJsonType(null, localDate, "some.field");
        // then
        assertThat(jsonObject).isNotNull();
        NumberJsonType numberJsonType = (NumberJsonType)  jsonObject;
        assertThat(numberJsonType.toString()).isEqualTo("1564876800");
    }

    @Test
    public void convertLocalDateToJsonObject() {
        // given
        LocalDate localDate = LocalDate.of(2019, 8, 4);
        LocalDateTimeResolver resolver = new LocalDateTimeResolver(false);
        // when
        AbstractJsonType jsonObject = resolver.returnConcreteJsonType(null, localDate, "some.field");
        // then
        assertThat(jsonObject).isNotNull();
        ObjectJsonType numberJsonType = (ObjectJsonType)  jsonObject;
        assertThat(numberJsonType.toString()).isEqualTo("{\"month\":8,\"year\":2019,\"day\":4}");
    }
}