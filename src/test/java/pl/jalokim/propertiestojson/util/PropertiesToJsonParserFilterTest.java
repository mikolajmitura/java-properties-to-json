package pl.jalokim.propertiestojson.util;

import com.google.gson.Gson;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import pl.jalokim.propertiestojson.domain.MainObject;

import java.io.IOException;
import java.io.InputStream;

public class PropertiesToJsonParserFilterTest extends AbstractPropertiesToJsonParserTest {

    @Test
    public void parsePropertiesOnlyByIncludedKeys() {
        //when
        String json = PropertiesToJsonParser.parseToJson(initProperlyProperties(), "man.groups", "man.hoobies", "insurance.cost");
        // then
        assertJsonIsAsExpected(json);
    }

    @Test
    public void parseInputStreamOnlyByIncludedKeys() throws IOException {
        // given
        InputStream inputStream = getPropertiesFromFile();
        // when
        String json = PropertiesToJsonParser.parseToJson(inputStream, "man.groups", "man.hoobies", "insurance.cost");
        // then
        assertJsonIsAsExpected(json);
    }

    private void assertJsonIsAsExpected(String json) {
        Gson gson = new Gson();
        MainObject mainObject = gson.fromJson(json, MainObject.class);
        Assertions.assertThat(mainObject.getField1()).isNull();
        Assertions.assertThat(mainObject.getField2()).isNull();
        Assertions.assertThat(mainObject.getInsurance().getCost()).isEqualTo(COST_INT_VALUE);
        Assertions.assertThat(mainObject.getInsurance().getType()).isNull();
        Assertions.assertThat(mainObject.getMan().getAddress()).isNull();
        Assertions.assertThat(mainObject.getMan().getName()).isNull();
        Assertions.assertThat(mainObject.getMan().getSurname()).isNull();
        Assertions.assertThat(mainObject.getMan().getInsurance()).isNull();
        Assertions.assertThat(mainObject.getMan().getEmails()).isNull();
        assertGroupByIdAndExpectedValues(mainObject, 0, GROUP_1, COMMERCIAL);
        assertGroupByIdAndExpectedValues(mainObject, 1, GROUP_2, FREE);
        assertGroupByIdAndExpectedValues(mainObject, 2, GROUP_3, COMMERCIAL);
        assertHobbiesList(mainObject);
    }

    @Test
    public void parseInputStreamOnlyWithWholeInsuranceByIncludedKeys() throws IOException {
        // given
        InputStream inputStream = getPropertiesFromFile();
        // when
        String json = PropertiesToJsonParser.parseToJson(inputStream, "man.groups", "man.hoobies", "insurance");
        // then
        assertJsonIsAsExpectedWithWholeInsurance(json);
    }

    private void assertJsonIsAsExpectedWithWholeInsurance(String json) {
        Gson gson = new Gson();
        MainObject mainObject = gson.fromJson(json, MainObject.class);
        Assertions.assertThat(mainObject.getField1()).isNull();
        Assertions.assertThat(mainObject.getField2()).isNull();
        Assertions.assertThat(mainObject.getInsurance().getCost()).isEqualTo(COST_INT_VALUE);
        Assertions.assertThat(mainObject.getInsurance().getType()).isEqualTo("Medical");
        Assertions.assertThat(mainObject.getMan().getAddress()).isNull();
        Assertions.assertThat(mainObject.getMan().getName()).isNull();
        Assertions.assertThat(mainObject.getMan().getSurname()).isNull();
        Assertions.assertThat(mainObject.getMan().getInsurance()).isNull();
        Assertions.assertThat(mainObject.getMan().getEmails()).isNull();
        assertGroupByIdAndExpectedValues(mainObject, 0, GROUP_1, COMMERCIAL);
        assertGroupByIdAndExpectedValues(mainObject, 1, GROUP_2, FREE);
        assertGroupByIdAndExpectedValues(mainObject, 2, GROUP_3, COMMERCIAL);
        assertHobbiesList(mainObject);
    }

}
