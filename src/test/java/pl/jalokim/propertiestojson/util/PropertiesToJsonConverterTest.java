package pl.jalokim.propertiestojson.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import org.junit.Test;
import pl.jalokim.propertiestojson.domain.MainComplexObject;
import pl.jalokim.propertiestojson.domain.MainObject;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.object.AbstractObjectToJsonTypeConverter;


public class PropertiesToJsonConverterTest extends AbstractPropertiesToJsonConverterTest {

    @Test
    public void returnExpectedJsonFromGivenFile() {
        //when
        String json = new PropertiesToJsonConverter().convertPropertiesFromFileToJson(new File("src/test/resources/primitiveTypes.properties"));
        // then
        assertJsonWithPrimitivesTypes(json);
    }

    @Test
    public void returnExpectedJsonWithGivenIncludeFromGivenFile() {
        //when
        String json = new PropertiesToJsonConverter()
            .convertPropertiesFromFileToJson(new File("src/test/resources/primitiveTypes.properties"), "complexObject");
        // then
        assertJsonWithPrimitivesTypesWithoutSimpleText(json);
    }

    @Test
    public void returnExpectedJsonFromGivenFilePath() {
        //when
        String json = new PropertiesToJsonConverter().convertPropertiesFromFileToJson("src/test/resources/primitiveTypes.properties");
        // then
        assertJsonWithPrimitivesTypes(json);
    }

    @Test
    public void returnExpectedJsonWithGivenIncludeFromGivenFilePath() {
        //when
        String json = new PropertiesToJsonConverter().convertPropertiesFromFileToJson("src/test/resources/primitiveTypes.properties", "complexObject");
        // then
        assertJsonWithPrimitivesTypesWithoutSimpleText(json);
    }

    @Test
    public void returnExpectedJsonGivenFromInputStream() throws Exception {
        // given
        InputStream inputStream = getPropertiesFromFile();
        // when
        String json = new PropertiesToJsonConverter().convertToJson(inputStream);
        // then
        assertJsonIsAsExpected(json);
    }

    @Test
    public void returnExpectedJsonGivenByMap() {
        //when
        //given
        String json = new PropertiesToJsonConverter().convertToJson(initProperlyPropertiesMap());
        //then
        assertJsonIsAsExpected(json);
    }


    @Test
    public void cannotCreateJsonWhenIsNotFormattedCorrectly() {
        // given
        PropertiesToJsonConverter converter = PropertiesToJsonConverterBuilder.builder()
            .defaultAndCustomObjectToJsonTypeConverters(new InvalidConverter())
            .build();
        Properties properties = new Properties();
        properties.put("some.given.path", "someText");
        // when
        try {
            String json = converter.convertToJson(properties);
            System.out.println(json);
            fail();
        } catch (JsonSyntaxException ex) {
            // then
            assertThat(ex.getMessage())
                .isEqualTo("com.google.gson.stream.MalformedJsonException: Expected ':' at line 1 column 36 path $.some.given.path.someText");
        }
    }

    @Test
    public void returnExpectedJsonGivenByProperties() {
        //when
        //given
        String json = new PropertiesToJsonConverter().convertToJson(initProperlyProperties());
        //then
        assertJsonIsAsExpected(json);
    }

    @Test
    public void jsonWithExpectedOrderOfProperties() {
        // when
        String json = new PropertiesToJsonConverter().convertPropertiesFromFileToJson("src/test/resources/order-of-properties.properties");
        // then
        assertThat(json).isEqualTo("{\n" +
            "  \"someField\": {\n" +
            "    \"nextField0\": {\n" +
            "      \"leaf1\": 1,\n" +
            "      \"leaf2\": 2,\n" +
            "      \"leaf3\": 3\n" +
            "    },\n" +
            "    \"nextField1\": {\n" +
            "      \"leaf1\": 1\n" +
            "    }\n" +
            "  },\n" +
            "  \"anotherField\": {\n" +
            "    \"nextField\": {\n" +
            "      \"leaf1\": 1,\n" +
            "      \"leaf2\": 2\n" +
            "    }\n" +
            "  },\n" +
            "  \"0field\": 0\n" +
            "}");
    }

    @Test
    public void jsonWithExpectedOrderOfPropertiesDuringFiltering() {
        // when
        String json = new PropertiesToJsonConverter()
            .convertPropertiesFromFileToJson("src/test/resources/order-of-properties.properties", "someField.nextField0", "0field");
        // then
        assertThat(json).isEqualTo("{\n" +
            "  \"someField\": {\n" +
            "    \"nextField0\": {\n" +
            "      \"leaf1\": 1,\n" +
            "      \"leaf2\": 2,\n" +
            "      \"leaf3\": 3\n" +
            "    }\n" +
            "  },\n" +
            "  \"0field\": 0\n" +
            "}");
    }

    private void assertJsonWithPrimitivesTypesWithoutSimpleText(String json) {
        Gson gson = new Gson();
        MainComplexObject mainComplexObject = gson.fromJson(json, MainComplexObject.class);
        assertThat(mainComplexObject.getComplexObject().getBooleans().getTrueValue()).isTrue();
        assertThat(mainComplexObject.getComplexObject().getBooleans().getFalseValue()).isFalse();
        assertThat(mainComplexObject.getComplexObject().getNumbers().getDoubleValue()).isEqualTo(11.0d);
        assertThat(mainComplexObject.getComplexObject().getNumbers().getIntegerValue()).isEqualTo(11);
        assertThat(mainComplexObject.getComplexObject().getText()).isEqualTo("text");
    }

    private void assertJsonWithPrimitivesTypes(String json) {
        assertJsonWithPrimitivesTypesWithoutSimpleText(json);
        Gson gson = new Gson();
        MainComplexObject mainComplexObject = gson.fromJson(json, MainComplexObject.class);
        assertThat(mainComplexObject.getSimpleText()).isEqualTo("text2");
    }

    private void assertJsonIsAsExpected(String json) {
        Gson gson = new Gson();
        MainObject mainObject = gson.fromJson(json, MainObject.class);
        assertThat(mainObject.getField1()).isEqualTo(FIELD1_VALUE);
        assertThat(mainObject.getField2()).isEqualTo(FIELD2_VALUE);
        assertThat(mainObject.getInsurance().getCost()).isEqualTo(COST_INT_VALUE);
        assertThat(mainObject.getInsurance().getType()).isEqualTo(INSRANCE_TYPE);
        assertThat(mainObject.getMan().getAddress().getCity()).isEqualTo(CITY);
        assertThat(mainObject.getMan().getAddress().getStreet()).isEqualTo(STREET);
        assertThat(mainObject.getMan().getName()).isEqualTo(NAME);
        assertThat(mainObject.getMan().getSurname()).isEqualTo(SURNAME);
        assertThat(mainObject.getMan().getMarried()).isEqualTo(false);
        assertThat(mainObject.getMan().getInsurance().getCost()).isEqualTo(EXPECTED_MAN_COST);
        assertThat(mainObject.getMan().getInsurance().getValid()).isEqualTo(true);
        assertEmailList(mainObject);
        assertGroupByIdAndExpectedValues(mainObject, 0, GROUP_1, COMMERCIAL);
        assertGroupByIdAndExpectedValues(mainObject, 1, GROUP_2, FREE);
        assertGroupByIdAndExpectedValues(mainObject, 2, GROUP_3, COMMERCIAL);
        assertHobbiesList(mainObject);
    }

    private static class InvalidConverter extends AbstractObjectToJsonTypeConverter<String> {

        @Override
        public Optional<AbstractJsonType> convertToJsonTypeOrEmpty(PrimitiveJsonTypesResolver primitiveJsonTypesResolver,
            String convertedValue,
            String propertyKey) {
            return Optional.of(new OwnAbstractJsonType(convertedValue));
        }
    }

    private static class OwnAbstractJsonType extends AbstractJsonType {

        private String value;

        OwnAbstractJsonType(String value) {
            this.value = value;
        }

        @Override
        public String toStringJson() {
            return "{" + value + "}";
        }
    }

}

