package pl.jalokim.propertiestojson.util;

import com.google.gson.Gson;
import org.assertj.core.api.Assertions;

import org.junit.Test;
import pl.jalokim.propertiestojson.domain.MainObject;

import java.io.InputStream;


public class PropertiesToJsonParserTest extends AbstractPropertiesToJsonParserTest{



    @Test
    public void returnExpectedJsonGivenFromFile() throws Exception{
        // given
        InputStream inputStream = getPropertiesFromFile();
        // when
        String json = PropertiesToJsonParser.parseToJson(inputStream);
        // then
        assertJsonIsAsExpected(json);
    }

    @Test
    public void returnExpectedJsonGivenByMap(){
        //when
        //given
        String json = PropertiesToJsonParser.parseToJson(initProperlyProperties());
        //then
        assertJsonIsAsExpected(json);
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
        assertGroupByIdAndExpectedValues(mainObject,0,GROUP_1, COMMERCIAL);
        assertGroupByIdAndExpectedValues(mainObject,1,GROUP_2, FREE);
        assertGroupByIdAndExpectedValues(mainObject,2,GROUP_3, COMMERCIAL);
        assertHobbiesList(mainObject);
    }



}

