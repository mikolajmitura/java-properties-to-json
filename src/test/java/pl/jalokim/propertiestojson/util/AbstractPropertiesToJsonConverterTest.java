package pl.jalokim.propertiestojson.util;

import org.assertj.core.api.Assertions;
import pl.jalokim.propertiestojson.domain.MainObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class AbstractPropertiesToJsonConverterTest {

    protected static final String FIELD2_VALUE = "die3";
    protected static final String FIELD1_VALUE = "die2";
    protected static final String COST_STRING_VALUE = "123.0";
    protected static final Double COST_INT_VALUE = 123.0;
    protected static final String INSRANCE_TYPE = "Medical";
    protected static final String STREET = "Jp2";
    protected static final String CITY = "Waraw";
    protected static final String SURNAME = "Surname";
    protected static final String NAME = "John";
    protected static final String EMAIL_1 = "example@gg.com";
    protected static final String EMAIL_2 = "example2@cc.com";
    protected static final String EMAIL_3 = "example3@gg.com";
    protected static final String EMAILS = String.format("%s,%s,%s,%s", EMAIL_1, EMAIL_2, EMAIL_3, EMAIL_3);
    protected static final String GROUP_1 = "group1";
    protected static final String COMMERCIAL = "Commercial";
    protected static final String GROUP_3 = "group3";
    protected static final String GROUP_2 = "group2";
    protected static final String FREE = "Free";
    protected static final String CARS = "cars";
    protected static final String COMPUTERS = "computers";
    protected static final String WOMEN = "women";
    protected static final String SCIENCE = "science";
    protected static final String FILE_PATH = "src/test/resources/example.properties";
    protected static final String MAN_COST = "126.543";
    protected static final Double EXPECTED_MAN_COST = 126.543;

    protected InputStream getPropertiesFromFile() throws IOException {
        return new FileInputStream(FILE_PATH);
    }

    protected Map<String, String> initProperlyPropertiesMap() {
        Map<String, String> properties = new HashMap<>();
        properties.put("man.name", NAME);
        properties.put("man.surname", SURNAME);
        properties.put("man.address.city", CITY);
        properties.put("man.address.street", STREET);
        properties.put("insurance.type", INSRANCE_TYPE);
        properties.put("insurance.cost", COST_STRING_VALUE);
        properties.put("field1", FIELD1_VALUE);
        properties.put("field2", FIELD2_VALUE);
        properties.put("man.emails", EMAILS);
        properties.put("man.groups[0].name", GROUP_1);
        properties.put("man.groups[0].type", COMMERCIAL);
        properties.put("man.groups[2].name", GROUP_3);
        properties.put("man.groups[2].type", COMMERCIAL);
        properties.put("man.groups[1].name", GROUP_2);
        properties.put("man.groups[1].type", FREE);
        properties.put("man.hoobies[0]", CARS);
        properties.put("man.hoobies[3]", COMPUTERS);
        properties.put("man.hoobies[2]", WOMEN);
        properties.put("man.hoobies[1]", SCIENCE);
        properties.put("man.married", "false");
        properties.put("man.insurance.cost", MAN_COST);
        properties.put("man.insurance.valid", "true");
        return properties;
    }

    protected Properties initProperlyProperties() {
        Properties properties = new Properties();
        properties.put("man.name", NAME);
        properties.put("man.surname", SURNAME);
        properties.put("man.address.city", CITY);
        properties.put("man.address.street", STREET);
        properties.put("insurance.type", INSRANCE_TYPE);
        properties.put("insurance.cost", COST_INT_VALUE);
        properties.put("field1", FIELD1_VALUE);
        properties.put("field2", FIELD2_VALUE);
        properties.put("man.emails", Arrays.asList(EMAIL_1, EMAIL_2, EMAIL_3, EMAIL_3));
        properties.put("man.groups[0].name", GROUP_1);
        properties.put("man.groups[0].type", COMMERCIAL);
        properties.put("man.groups[2].name", GROUP_3);
        properties.put("man.groups[2].type", COMMERCIAL);
        properties.put("man.groups[1].name", GROUP_2);
        properties.put("man.groups[1].type", FREE);
        properties.put("man.hoobies[0]", CARS);
        properties.put("man.hoobies[3]", COMPUTERS);
        properties.put("man.hoobies[2]", WOMEN);
        properties.put("man.hoobies[1]", SCIENCE);
        properties.put("man.married", false);
        properties.put("man.insurance.cost", EXPECTED_MAN_COST);
        properties.put("man.insurance.valid", true);
        return properties;
    }

    protected void assertGroupByIdAndExpectedValues(MainObject mainObject, int index, String name, String type) {
        Assertions.assertThat(mainObject.getMan().getGroups().get(index).getName()).isEqualTo(name);
        Assertions.assertThat(mainObject.getMan().getGroups().get(index).getType()).isEqualTo(type);
    }

    protected void assertEmailList(MainObject mainObject) {
        List<String> emails = mainObject.getMan().getEmails();
        Assertions.assertThat(emails.get(0)).isEqualTo(EMAIL_1);
        Assertions.assertThat(emails.get(1)).isEqualTo(EMAIL_2);
        Assertions.assertThat(emails.get(2)).isEqualTo(EMAIL_3);
        Assertions.assertThat(emails.get(3)).isEqualTo(EMAIL_3);
    }

    protected void assertHobbiesList(MainObject mainObject) {
        List<String> hobbies = mainObject.getMan().getHoobies();
        Assertions.assertThat(hobbies.get(0)).isEqualTo(CARS);
        Assertions.assertThat(hobbies.get(1)).isEqualTo(SCIENCE);
        Assertions.assertThat(hobbies.get(2)).isEqualTo(WOMEN);
        Assertions.assertThat(hobbies.get(3)).isEqualTo(COMPUTERS);
    }
}
