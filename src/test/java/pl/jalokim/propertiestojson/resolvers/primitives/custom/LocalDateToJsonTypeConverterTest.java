package pl.jalokim.propertiestojson.resolvers.primitives.custom;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.Test;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.NumberJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;

public class LocalDateToJsonTypeConverterTest {

    @Test
    public void convertLocalDateToUtcTimestamp() {
        // given
        LocalDate localDate = LocalDate.of(2019, 8, 4);
        LocalDateToJsonTypeConverter resolver = new LocalDateToJsonTypeConverter(true);
        // when
        AbstractJsonType jsonObject = resolver.convertToJsonTypeOrEmpty(null, localDate, "some.field").get();
        // then
        assertThat(jsonObject).isNotNull();
        NumberJsonType numberJsonType = (NumberJsonType) jsonObject;
        assertThat(numberJsonType.toString()).isEqualTo("1564876800");
    }

    @Test
    public void convertLocalDateToJsonObject() {
        // given
        LocalDate localDate = LocalDate.of(2019, 8, 4);
        LocalDateToJsonTypeConverter resolver = new LocalDateToJsonTypeConverter(false);
        // when
        AbstractJsonType jsonObject = resolver.convertToJsonTypeOrEmpty(null, localDate, "some.field").get();
        // then
        assertThat(jsonObject).isNotNull();
        ObjectJsonType numberJsonType = (ObjectJsonType) jsonObject;
        assertThat(numberJsonType.toString()).isEqualTo("{\"year\":2019,\"month\":8,\"day\":4}");
    }
}