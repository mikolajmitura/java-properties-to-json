package pl.jalokim.propertiestojson;

import com.google.gson.Gson;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pl.jalokim.propertiestojson.domain.MainObject;
import pl.jalokim.propertiestojson.util.PropertiesToJsonParser;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.UNEXPECTED_JSON_OBJECT;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.UNEXPECTED_PRIMITIVE_TYPE;

public class PropertiesToJsonParserTest {

    private static final String FIELD2_VALUE = "die3";
    private static final String FIELD1_VALUE = "die2";
    private static final String COST_STRING_VALUE = "123";
    private static final Integer COST_INT_VALUE = 123;
    private static final String INSRANCE_TYPE = "Medical";
    private static final String STREET = "Jp2";
    private static final String CITY = "Waraw";
    private static final String SURNAME = "Surname";
    private static final String NAME = "John";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private Map<String,String> initProperlyProperties(){
        Map<String,String> properties = new HashMap<>();
        properties.put("man.name", NAME);
        properties.put("man.surname", SURNAME);
        properties.put("man.adress.city", CITY);
        properties.put("man.adress.street", STREET);
        properties.put("insurance.type",INSRANCE_TYPE);
        properties.put("insurance.cost", COST_STRING_VALUE);
        properties.put("field1", FIELD1_VALUE);
        properties.put("field2", FIELD2_VALUE);
        return properties;
    }

    private Map<String,String> addWrongParams(Map<String, String> properties, String wrongKey){
        properties.put(wrongKey,wrongKey);
        return properties;
    }

    @Test
    public void returnExpectedJson(){
        //when
        //given
        String json = PropertiesToJsonParser.parseToJson(initProperlyProperties());
        //then
        assertJsonIsAsExpected(json);
    }

    @Test
    public void ThrowWhenUnexpectedPrimitiveType(){
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(UNEXPECTED_PRIMITIVE_TYPE, "man"));
        //when
        //given
        PropertiesToJsonParser.parseToJson(addWrongParams(initProperlyProperties(), "man"));
      }

    @Test
    public void ThrowWhenUnexpectedJsonObject(){
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(UNEXPECTED_JSON_OBJECT, "field1.prop2"));
        //when
        //given
        PropertiesToJsonParser.parseToJson(addWrongParams(initProperlyProperties(), "field1.prop2"));
    }

    private void assertJsonIsAsExpected(String json) {
        Gson gson = new Gson();
        MainObject mainObject = gson.fromJson(json, MainObject.class);
        Assertions.assertThat(mainObject.getField1()).isEqualTo(FIELD1_VALUE);
        Assertions.assertThat(mainObject.getField2()).isEqualTo(FIELD2_VALUE);
        Assertions.assertThat(mainObject.getInsurance().getCost()).isEqualTo(COST_INT_VALUE);
        Assertions.assertThat(mainObject.getInsurance().getType()).isEqualTo(INSRANCE_TYPE);
        Assertions.assertThat(mainObject.getMan().getAdress().getCity()).isEqualTo(CITY);
        Assertions.assertThat(mainObject.getMan().getAdress().getStreet()).isEqualTo(STREET);
        Assertions.assertThat(mainObject.getMan().getName()).isEqualTo(NAME);
        Assertions.assertThat(mainObject.getMan().getSurname()).isEqualTo(SURNAME);
    }
}
