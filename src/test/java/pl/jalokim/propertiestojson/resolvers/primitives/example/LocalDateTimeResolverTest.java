package pl.jalokim.propertiestojson.resolvers.primitives.example;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.NumberJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.util.PropertiesToJsonConverter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.time.Month.AUGUST;
import static org.assertj.core.api.Assertions.assertThat;

public class LocalDateTimeResolverTest {

    private static final JsonParser jp = new JsonParser();

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
    public void convertFromTextDateThroughConverterToObjectJson() {
        // given
        LocalDateTimeResolver resolver = new LocalDateTimeResolver();
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(resolver);
        Map<String, String> properties = new HashMap<>();
        properties.put("object.localDateField", "04-08-2019");
        // when
        String json = converter.convertToJson(properties);
        // then
        System.out.println(json);
        JsonElement jsonElement = jp.parse(json);
        JsonObject asJsonObject = jsonElement.getAsJsonObject().getAsJsonObject("object");
        JsonObject localDateJson = asJsonObject.getAsJsonObject("localDateField");
        assertThat(localDateJson.get("year").getAsInt()).isEqualTo(2019);
        assertThat(localDateJson.get("month").getAsInt()).isEqualTo(8);
        assertThat(localDateJson.get("day").getAsInt()).isEqualTo(4);
    }

    @Test
    public void convertFromTextDateThroughConverterToSimpleTimestamp() {
        // given
        LocalDateTimeResolver resolver = new LocalDateTimeResolver(true);
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(resolver);
        Map<String, String> properties = new HashMap<>();
        properties.put("object.localDateField", "04-08-2019");
        // when
        String json = converter.convertToJson(properties);
        // then
        System.out.println(json);
        JsonElement jsonElement = jp.parse(json);
        JsonObject asJsonObject = jsonElement.getAsJsonObject().getAsJsonObject("object");
        assertThat(asJsonObject.getAsJsonPrimitive("localDateField").getAsInt()).isEqualTo(1564876800);
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