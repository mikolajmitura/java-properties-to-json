package pl.jalokim.propertiestojson.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pl.jalokim.propertiestojson.helper.PropertyKeysPickupOrderedForTest;
import pl.jalokim.propertiestojson.resolvers.primitives.NumberJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.StringJsonTypeResolver;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.EXPECTED_ARRAY_JSON_TYPE;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.EXPECTED_ELEMENT_ARRAY_JSON_OBJECT_TYPES;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.EXPECTED_ELEMENT_ARRAY_PRIMITIVE_TYPES;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.EXPECTED_OBJECT_JSON_TYPE;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.EXPECTED_PRIMITIVE_JSON_TYPE;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.PROPERTY_KEY_NEEDS_TO_BE_STRING_TYPE;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.STRING_RESOLVER_AS_NOT_LAST;

public class PropertiesToJsonParsePropertiesExceptionTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void throwWhenExpectedObjectNotPrimitiveType() {
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_OBJECT_JSON_TYPE, "man", "{\"someField\":\"test\"}", "man"));
        //given
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter();
        setUpMockPickupKeysOrder(converter, "man.someField", "man");
        //when
        converter.convertToJson(addWrongParams(initProperties(), "man"));
    }

    @Test
    public void throwWhenExpectedPrimitiveNotJsonObject() {
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_PRIMITIVE_JSON_TYPE, "field1", "\"test\"", "field1.prop2"));
        //given
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter();
        setUpMockPickupKeysOrder(converter, "field1", "field1.prop2");
        //when
        converter.convertToJson(addWrongParams(initProperties(), "field1.prop2"));
    }


    @Test
    public void throwWhenExpectedJsonArrayNotPrimitiveType() {
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_ARRAY_JSON_TYPE, "groups", "[\"test\"]", "groups"));
        //given
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter();
        setUpMockPickupKeysOrder(converter, "groups[0]", "groups");
        //when
        converter.convertToJson(addWrongParams(initProperties(), "groups"));
    }

    @Test
    public void throwWhenExpectedPrimitiveTypeNotArray() {
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_PRIMITIVE_JSON_TYPE, "groups", "\"groupsVALUE\"", "groups[0]"));
        //given
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter();
        setUpMockPickupKeysOrder(converter, "groups", "groups[0]");
        //when
        converter.convertToJson(addWrongParams(initProperties(), "groups"));
    }

    @Test
    public void throwWhenExpectedPrimitiveArrayElementNotObject() {
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_ELEMENT_ARRAY_PRIMITIVE_TYPES, "array", 0, "\"valueOfArray0\"", "array[0].name"));
        //given
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter();
        setUpMockPickupKeysOrder(converter, "array[0]", "array[0].name");
        //when
        converter.convertToJson(addWrongParams(initProperties(), "array[0].name"));
    }

    @Test
    public void throwWhenExpectedJsonObjectArrayElementNotPrimitiveType() {
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_ELEMENT_ARRAY_JSON_OBJECT_TYPES, "array", 0, "{\"name\":\"array[0].nameVALUE\"}", "array[0]"));
        //given
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter();
        setUpMockPickupKeysOrder(converter, "array[0].name", "array[0]");
        //when
        String s = converter.convertToJson(addWrongParams(initProperties(), "array[0].name"));
        System.out.println(s);
    }

    @Test
    public void throwExceptionWhenGaveSomeTypeResolverAfterStringJsonTypeResolver() {
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(STRING_RESOLVER_AS_NOT_LAST);
        //given
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter(
                new StringJsonTypeResolver(),
                new NumberJsonTypeResolver()
        );
        //when
        converter.convertToJson(initProperties());
    }

    @Test
    public void throwExceptionWhenPropertyHasKeyAnotherTypeThanString() {
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(PROPERTY_KEY_NEEDS_TO_BE_STRING_TYPE, "class java.lang.Integer", "1555"));
        //given
        Properties properties = new Properties();
        properties.put(1555, 123L);
        //when
        new PropertiesToJsonConverter().convertToJson(properties);
    }

    public static void setUpMockPickupKeysOrder(PropertiesToJsonConverter converter, String... keys) {
        PropertyKeysPickupOrderedForTest pickupOrderedForTest = new PropertyKeysPickupOrderedForTest();
        pickupOrderedForTest.setUpMockKeys(keys);
        converter.setPropertyKeysPickup(pickupOrderedForTest);
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
