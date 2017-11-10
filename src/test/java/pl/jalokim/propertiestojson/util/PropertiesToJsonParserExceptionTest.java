package pl.jalokim.propertiestojson.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pl.jalokim.propertiestojson.helper.PropertyKeysPickupOrderedForTest;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.util.HashMap;
import java.util.Map;

import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.*;

public class PropertiesToJsonParserExceptionTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void throwWhenExpectedObjectNotPrimitiveType() {
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_OBJECT_JSON_TYPE, "man", "{\"someField\":\"test\"}", "man"));
        //when
        //given
        setUpMockPickupKeysOrder("man.someField", "man");
        PropertiesToJsonParser.parseToJson(addWrongParams(initProperties(), "man"));
    }

    @Test
    public void throwWhenExpectedPrimitiveNotJsonObject() {
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_PRIMITIVE_JSON_TYPE, "field1", "\"test\"", "field1.prop2"));
        //when
        //given
        setUpMockPickupKeysOrder("field1", "field1.prop2");
        PropertiesToJsonParser.parseToJson(addWrongParams(initProperties(), "field1.prop2"));
    }


    @Test
    public void throwWhenExpectedJsonArrayNotPrimitiveType() {
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_ARRAY_JSON_TYPE, "groups", "[\"test\"]", "groups"));
        //when
        //given
        setUpMockPickupKeysOrder("groups[0]", "groups");
        PropertiesToJsonParser.parseToJson(addWrongParams(initProperties(), "groups"));
    }

    @Test
    public void throwWhenExpectedPrimitiveTypeNotArray() {
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_PRIMITIVE_JSON_TYPE, "groups", "\"groupsVALUE\"", "groups[0]"));
        //when
        //given
        setUpMockPickupKeysOrder("groups", "groups[0]");
        PropertiesToJsonParser.parseToJson(addWrongParams(initProperties(), "groups"));
    }

    @Test
    public void throwWhenExpectedPrimitiveArrayElementNotObject() {
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_ELEMENT_ARRAY_PRIMITIVE_TYPES, "array", 0, "\"valueOfArray0\"", "array[0].name"));
        //when
        //given
        setUpMockPickupKeysOrder("array[0]", "array[0].name");
        PropertiesToJsonParser.parseToJson(addWrongParams(initProperties(), "array[0].name"));
    }

    @Test
    public void throwWhenExpectedJsonObjectArrayElementNotPrimitiveType() {
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_ELEMENT_ARRAY_JSON_OBJECT_TYPES, "array", 0, "{\"name\":\"array[0].nameVALUE\"}", "array[0]" ));
        //when
        //given
        setUpMockPickupKeysOrder("array[0].name", "array[0]");
        String s = PropertiesToJsonParser.parseToJson(addWrongParams(initProperties(), "array[0].name"));
        System.out.println(s);
    }

    private void setUpMockPickupKeysOrder(String... keys) {
        PropertyKeysPickupOrderedForTest pickupOrderedForTest = new PropertyKeysPickupOrderedForTest();
        pickupOrderedForTest.setUpMockKeys(keys);
        PropertiesToJsonParser.setPropertyKeysPickup(pickupOrderedForTest);
    }

    private Map<String, String> initProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("man.someField", "test");
        properties.put("field1", "test");
        properties.put("groups[0]", "test");
        properties.put("array[0]", "valueOfArray0");
        return properties;
    }

    private Map<String, String> addWrongParams(Map<String, String> properties, String... wrongKeys) {
        for (String wrongKey : wrongKeys) {
            properties.put(wrongKey, wrongKey + "VALUE");
        }

        return properties;
    }
}
