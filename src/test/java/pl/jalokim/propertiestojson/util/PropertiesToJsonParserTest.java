package pl.jalokim.propertiestojson.util;

import com.google.gson.Gson;
import org.assertj.core.api.Assertions;

import org.junit.Test;
import pl.jalokim.propertiestojson.domain.MainObject;
import pl.jalokim.propertiestojson.helper.PropertyKeysPickup;
import pl.jalokim.propertiestojson.util.PropertiesToJsonParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private static final String EMAIL_1 = "example@gg.com";
    private static final String EMAIL_2 = "example2@cc.com";
    private static final String EMAIL_3 = "example3@gg.com";
    private static final String EMAILS = String.format(" %s ,%s, %s,%s",EMAIL_1, EMAIL_2, EMAIL_3, EMAIL_3);
    public static final String GROUP_1 = "group1";
    public static final String COMMERCIAL = "Commercial";
    public static final String GROUP_3 = "group3";
    public static final String GROUP_2 = "group2";
    public static final String FREE = "Free";
    public static final String CARS = "cars";
    public static final String COMPUTERS = "computers";
    public static final String WOMEN = "women";
    public static final String SCIENCE = "science";

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
        properties.put("man.emails", EMAILS);
        properties.put("man.groups[0].name", GROUP_1);
        properties.put("man.groups[0].type",COMMERCIAL);
        properties.put("man.groups[2].name", GROUP_3);
        properties.put("man.groups[2].type",COMMERCIAL);
        properties.put("man.groups[1].name", GROUP_2);
        properties.put("man.groups[1].type", FREE);
        properties.put("man.hoobies[0]", CARS);
        properties.put("man.hoobies[3]", COMPUTERS);
        properties.put("man.hoobies[2]", WOMEN);
        properties.put("man.hoobies[1]", SCIENCE);
        return properties;
    }

    @Test
    public void returnExpectedJson(){
        //when
        //given
        PropertiesToJsonParser.setPropertyKeysPickup(new PropertyKeysPickup());
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
        Assertions.assertThat(mainObject.getMan().getAdress().getCity()).isEqualTo(CITY);
        Assertions.assertThat(mainObject.getMan().getAdress().getStreet()).isEqualTo(STREET);
        Assertions.assertThat(mainObject.getMan().getName()).isEqualTo(NAME);
        Assertions.assertThat(mainObject.getMan().getSurname()).isEqualTo(SURNAME);
        assertEmailList(mainObject);
        assertGroupByIdAndExpectedValues(mainObject,0,GROUP_1, COMMERCIAL);
        assertGroupByIdAndExpectedValues(mainObject,1,GROUP_2, FREE);
        assertGroupByIdAndExpectedValues(mainObject,2,GROUP_3, COMMERCIAL);
        assertHobbiesList(mainObject);
    }

    private void assertGroupByIdAndExpectedValues(MainObject mainObject, int index, String name, String type) {
        Assertions.assertThat(mainObject.getMan().getGroups().get(index).getName()).isEqualTo(name);
        Assertions.assertThat(mainObject.getMan().getGroups().get(index).getType()).isEqualTo(type);
    }

    private void assertEmailList(MainObject mainObject) {
        List<String> emails = mainObject.getMan().getEmails();
        Assertions.assertThat(emails.get(0)).isEqualTo(EMAIL_1);
        Assertions.assertThat(emails.get(1)).isEqualTo(EMAIL_2);
        Assertions.assertThat(emails.get(2)).isEqualTo(EMAIL_3);
        Assertions.assertThat(emails.get(3)).isEqualTo(EMAIL_3);
    }

    private void assertHobbiesList(MainObject mainObject){
        List<String> hobbies = mainObject.getMan().getHoobies();
        Assertions.assertThat(hobbies.get(0)).isEqualTo(CARS);
        Assertions.assertThat(hobbies.get(1)).isEqualTo(SCIENCE);
        Assertions.assertThat(hobbies.get(2)).isEqualTo(WOMEN);
        Assertions.assertThat(hobbies.get(3)).isEqualTo(COMPUTERS);
    }

}

