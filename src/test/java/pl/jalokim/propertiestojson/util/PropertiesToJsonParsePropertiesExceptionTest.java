package pl.jalokim.propertiestojson.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pl.jalokim.propertiestojson.helper.PropertyKeysOrderResolverForTest;
import pl.jalokim.propertiestojson.resolvers.primitives.NumberJsonTypeResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.StringJsonTypeResolver;
import pl.jalokim.propertiestojson.util.exception.CannotOverrideFieldException;
import pl.jalokim.propertiestojson.util.exception.ParsePropertiesException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.PROPERTY_KEY_NEEDS_TO_BE_STRING_TYPE;
import static pl.jalokim.propertiestojson.util.exception.ParsePropertiesException.STRING_RESOLVER_AS_NOT_LAST;

public class PropertiesToJsonParsePropertiesExceptionTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    // TODO check tests for
    // array element in 2 dim -> object
    // array element in 2 dim -> primitive
    // array element in 2 dim -> array
    // array element in 3 dim -> object
    // array element in 3 dim -> primitive
    // array element in 3 dim -> array

    @Test
    public void throwWhenCannotOverrideObjectByPrimitiveValue() {
        //then
        expectedEx.expect(CannotOverrideFieldException.class);
        String expectedMsg = new CannotOverrideFieldException("man", "{\"someField\":\"test\"}", "man").getMessage();
        expectedEx.expectMessage(expectedMsg);
        //given
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter();
        setUpMockPickupKeysOrder(converter, "man.someField", "man");
        //when
        converter.convertToJson(addWrongParam(initProperties(), "man"));
    }

    @Test
    public void throwWhenCannotOverrideObjectByArray() {
        //then
        expectedEx.expect(CannotOverrideFieldException.class);
        String expectedMsg = new CannotOverrideFieldException("some.object", "{\"nextObject\":\"nextObjectValue\"}", "some.object[0]").getMessage();
        expectedEx.expectMessage(expectedMsg);
        //given
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter();
        setUpMockPickupKeysOrder(converter, "some.object.nextObject", "some.object[0]");
        //when
        converter.convertToJson(addWrongParam(initProperties(), "some.object[0]"));
    }

    @Test
    public void throwWhenCannotOverridePrimitiveByJsonObject() {
        //then
        expectedEx.expect(CannotOverrideFieldException.class);
        String expectedMsg = new CannotOverrideFieldException("field1", "\"test\"", "field1.prop2").getMessage();
        expectedEx.expectMessage(expectedMsg);
        //given
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter();
        setUpMockPickupKeysOrder(converter, "field1", "field1.prop2");
        //when
        converter.convertToJson(addWrongParam(initProperties(), "field1.prop2"));
    }

    @Test
    public void throwWhenCannotOverrideArrayByObject() {
        //then
        expectedEx.expect(CannotOverrideFieldException.class);
        String expectedMsg = new CannotOverrideFieldException("some.someArray", "[{\"field\":\"elementFieldValue\"}]", "some.someArray.unExpectedField").getMessage();
        expectedEx.expectMessage(expectedMsg);
        //given
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter();
        setUpMockPickupKeysOrder(converter, "some.someArray[0].field", "some.someArray.unExpectedField");
        //when
        converter.convertToJson(addWrongParam(initProperties(), "some.someArray.unExpectedField"));
    }


    @Test
    public void throwWhenCannotOverrideArrayByObjectPrimitiveType() {
        //then
        expectedEx.expect(CannotOverrideFieldException.class);
        String expectedMsg = new CannotOverrideFieldException("some.someArray", "[{\"field\":\"elementFieldValue\"}]", "some.someArray").getMessage();
        expectedEx.expectMessage(expectedMsg);
        //given
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter();
        setUpMockPickupKeysOrder(converter, "some.someArray[0].field", "some.someArray");
        //when
        converter.convertToJson(addWrongParam(initProperties(), "some.someArray"));
    }

    @Test
    public void throwWhenExpectedWhenCannotOverrideCurrentTextByArray() {
        //then
        expectedEx.expect(CannotOverrideFieldException.class);
        String expectedMsg = new CannotOverrideFieldException("groups", "\"groupsVALUE\"", "groups[0]").getMessage();
        expectedEx.expectMessage(expectedMsg);
        //given
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter();
        setUpMockPickupKeysOrder(converter, "groups", "groups[0]");
        //when
        converter.convertToJson(addWrongParam(initProperties(), "groups"));
    }

    @Test
    public void throwWhenCannotOverrideArrayElementByPrimitiveType() {
        //then
        expectedEx.expect(CannotOverrideFieldException.class);
        String expectedMsg = new CannotOverrideFieldException("array[0]", "{\"name\":\"array[0].nameVALUE\"}", "array[0]").getMessage();
        expectedEx.expectMessage(expectedMsg);
        //given
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter();
        setUpMockPickupKeysOrder(converter, "array[0].name", "array[0]");
        //when
        String s = converter.convertToJson(addWrongParam(initProperties(), "array[0].name"));
        System.out.println(s);
    }

    @Test
    public void throwWhenCannotOverrideArrayElementByObjectType() {
        //then
        expectedEx.expect(CannotOverrideFieldException.class);
        String expectedMsg = new CannotOverrideFieldException("some.someArray[0]", "{\"field\":\"elementFieldValue\"}", "some.someArray[0]").getMessage();
        expectedEx.expectMessage(expectedMsg);
        //given
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter();
        setUpMockPickupKeysOrder(converter, "some.someArray[0].field", "some.someArray[0]");
        //when
        String s = converter.convertToJson(addWrongParam(initProperties(), "some.someArray[0]", "{\"someField\": \"someValue\"}"));
        System.out.println(s);
    }

    @Test
    public void throwWhenCannotOverrideArrayElementByArray() {
        //then
        expectedEx.expect(CannotOverrideFieldException.class);
        String expectedMsg = new CannotOverrideFieldException("some.someArray[0]", "{\"field\":\"elementFieldValue\"}", "some.someArray[0]").getMessage();
        expectedEx.expectMessage(expectedMsg);
        //given
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter();
        setUpMockPickupKeysOrder(converter, "some.someArray[0].field", "some.someArray[0]");
        //when
        String s = converter.convertToJson(addWrongParam(initProperties(), "some.someArray[0]", "[1, 2, 3]"));
        System.out.println(s);
    }

    @Test
    public void cannotOverridePrimitiveArrayElementByObject() {
        //then
        expectedEx.expect(CannotOverrideFieldException.class);
        String expectedMsg = new CannotOverrideFieldException("array[0]", "\"valueOfArray0\"", "array[0].name").getMessage();
        expectedEx.expectMessage(expectedMsg);

        //given
        PropertiesToJsonConverter converter = new PropertiesToJsonConverter();
        setUpMockPickupKeysOrder(converter, "array[0]", "array[0].name");
        //when
        converter.convertToJson(addWrongParam(initProperties(), "array[0].name"));
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
        PropertyKeysOrderResolverForTest pickupOrderedForTest = new PropertyKeysOrderResolverForTest();
        pickupOrderedForTest.setUpMockKeys(keys);
        converter.setPropertyKeysOrderResolver(pickupOrderedForTest);
    }

    private Map<String, String> initProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("man.someField", "test");
        properties.put("field1", "test");
        properties.put("groups[0]", "test");
        properties.put("array[0]", "valueOfArray0");
        properties.put("some.object.nextObject", "nextObjectValue");
        properties.put("some.someArray[0].field", "elementFieldValue");
        return properties;
    }

    private Map<String, String> addWrongParam(Map<String, String> properties, String wrongKey) {
            properties.put(wrongKey, wrongKey + "VALUE");
        return properties;
    }

    private Map<String, String> addWrongParam(Map<String, String> properties, String wrongKey, String value) {
        properties.put(wrongKey, value);
        return properties;
    }
}
