package pl.jalokim.propertiestojson.util;

import com.google.gson.Gson;
import org.assertj.core.api.Assertions;

import org.junit.Test;
import pl.jalokim.propertiestojson.domain.MainComplexObject;
import pl.jalokim.propertiestojson.domain.MainObject;

import java.io.File;
import java.io.InputStream;


public class PropertiesToJsonParserTest extends AbstractPropertiesToJsonParserTest {

    @Test
    public void returnExpectedJsonFromGivenFile() {
        //when
        String json = PropertiesToJsonParser.parsePropertiesFromFileToJson(new File("src/test/resources/primitiveTypes.properties"));
        // then
        assertJsonWithPrimitivesTypes(json);
    }

    @Test
    public void returnExpectedJsonWithGivenIncludeFromGivenFile() {
        //when
        String json = PropertiesToJsonParser.parsePropertiesFromFileToJson(new File("src/test/resources/primitiveTypes.properties"), "complexObject");
        // then
        assertJsonWithPrimitivesTypesWithoutSimpleText(json);
    }

    @Test
    public void returnExpectedJsonFromGivenFilePath() {
        //when
        String json = PropertiesToJsonParser.parsePropertiesFromFileToJson("src/test/resources/primitiveTypes.properties");
        // then
        assertJsonWithPrimitivesTypes(json);
    }

    @Test
    public void returnExpectedJsonWithGivenIncludeFromGivenFilePath() {
        //when
        String json = PropertiesToJsonParser.parsePropertiesFromFileToJson("src/test/resources/primitiveTypes.properties", "complexObject");
        // then
        assertJsonWithPrimitivesTypesWithoutSimpleText(json);
    }

    @Test
    public void returnExpectedJsonGivenFromInputStream() throws Exception {
        // given
        InputStream inputStream = getPropertiesFromFile();
        // when
        String json = PropertiesToJsonParser.parseToJson(inputStream);
        // then
        assertJsonIsAsExpected(json);
    }

    @Test
    public void returnExpectedJsonGivenByMap() {
        //when
        //given
        String json = PropertiesToJsonParser.parseToJson(initProperlyProperties());
        //then
        assertJsonIsAsExpected(json);
    }

    private void assertJsonWithPrimitivesTypesWithoutSimpleText(String json) {
        Gson gson = new Gson();
        MainComplexObject mainComplexObject = gson.fromJson(json, MainComplexObject.class);
        Assertions.assertThat(mainComplexObject.getComplexObject().getBooleans().getTrueValue()).isTrue();
        Assertions.assertThat(mainComplexObject.getComplexObject().getBooleans().getFalseValue()).isFalse();
        Assertions.assertThat(mainComplexObject.getComplexObject().getNumbers().getDoubleValue()).isEqualTo(11.0d);
        Assertions.assertThat(mainComplexObject.getComplexObject().getNumbers().getIntegerValue()).isEqualTo(11);
        Assertions.assertThat(mainComplexObject.getComplexObject().getText()).isEqualTo("text");
    }

    private void assertJsonWithPrimitivesTypes(String json) {
        assertJsonWithPrimitivesTypesWithoutSimpleText(json);
        Gson gson = new Gson();
        MainComplexObject mainComplexObject = gson.fromJson(json, MainComplexObject.class);
        Assertions.assertThat(mainComplexObject.getSimpleText()).isEqualTo("text2");
    }

    private void assertJsonIsAsExpected(String json) {
        Gson gson = new Gson();
        MainObject mainObject = gson.fromJson(json, MainObject.class);
        Assertions.assertThat(mainObject.getField1()).isEqualTo(FIELD1_VALUE);
        Assertions.assertThat(mainObject.getField2()).isEqualTo(FIELD2_VALUE);
        Assertions.assertThat(mainObject.getInsurance().getCost()).isEqualTo(COST_INT_VALUE);
        Assertions.assertThat(mainObject.getInsurance().getType()).isEqualTo(INSRANCE_TYPE);
        Assertions.assertThat(mainObject.getMan().getAddress().getCity()).isEqualTo(CITY);
        Assertions.assertThat(mainObject.getMan().getAddress().getStreet()).isEqualTo(STREET);
        Assertions.assertThat(mainObject.getMan().getName()).isEqualTo(NAME);
        Assertions.assertThat(mainObject.getMan().getSurname()).isEqualTo(SURNAME);
        Assertions.assertThat(mainObject.getMan().getInsurance().getCost()).isEqualTo(EXPECTED_MAN_COST);
        assertEmailList(mainObject);
        assertGroupByIdAndExpectedValues(mainObject, 0, GROUP_1, COMMERCIAL);
        assertGroupByIdAndExpectedValues(mainObject, 1, GROUP_2, FREE);
        assertGroupByIdAndExpectedValues(mainObject, 2, GROUP_3, COMMERCIAL);
        assertHobbiesList(mainObject);
    }

}

