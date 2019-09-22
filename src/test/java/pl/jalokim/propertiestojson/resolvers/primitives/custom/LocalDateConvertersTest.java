package pl.jalokim.propertiestojson.resolvers.primitives.custom;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;
import pl.jalokim.propertiestojson.util.PropertiesToJsonConverter;
import pl.jalokim.propertiestojson.util.PropertiesToJsonConverterBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalDateConvertersTest {

    private static final JsonParser jp = new JsonParser();

    @Test
    public void convertFromTextDateThroughConverterToObjectJson() {
        // given
        TextToLocalDateResolver textToLocalDateResolver = new TextToLocalDateResolver();
        LocalDateToJsonTypeConverter localDateToJsonTypeConverter = new LocalDateToJsonTypeConverter();
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder()
                                                                              .onlyCustomTextToObjectResolvers(textToLocalDateResolver)
                                                                              .onlyCustomObjectToJsonTypeConverters(localDateToJsonTypeConverter)
                                                                              .build();
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
        TextToLocalDateResolver textToLocalDateResolver = new TextToLocalDateResolver();
        LocalDateToJsonTypeConverter localDateToJsonTypeConverter = new LocalDateToJsonTypeConverter(true);
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder()
                                                                              .onlyCustomTextToObjectResolvers(textToLocalDateResolver)
                                                                              .onlyCustomObjectToJsonTypeConverters(localDateToJsonTypeConverter)
                                                                              .build();
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
}
