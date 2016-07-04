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
    public void ThrowWhenExpectedObjectNotPrimitiveType(){
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_OBJECT_JSON_TYPE, "man", "man","{'someField':'test'}"));
        //when
        //given
        setUpMockPickupKeysOrder("man.someField","man");
        PropertiesToJsonParser.parseToJson(addWrongParams(initProperties(), "man"));
    }

    @Test
    public void ThrowWhenExpectedPrimitiveNotJsonObject(){
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_PRIMITIVE_JSON_TYPE,"field1","field1.prop2","'test'"));
        //when
        //given
        setUpMockPickupKeysOrder("field1","field1.prop2");
        PropertiesToJsonParser.parseToJson(addWrongParams(initProperties(), "field1.prop2"));
    }


    @Test
    public void ThrowWhenExpectedJsonArrayNotPrimitiveType(){
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_ARRAY_JSON_TYPE, "groups","groups","['test']"));
        //when
        //given
        setUpMockPickupKeysOrder("groups[0]","groups");
        PropertiesToJsonParser.parseToJson(addWrongParams(initProperties(), "groups"));
    }

    @Test
    public void ThrowWhenExpectedPrimitiveTypeNotArray(){
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_PRIMITIVE_JSON_TYPE, "groups", "groups[0]","'groupsVALUE'"));
        //when
        //given
        setUpMockPickupKeysOrder("groups","groups[0]");
        PropertiesToJsonParser.parseToJson(addWrongParams(initProperties(), "groups"));
    }

    @Test
    public void ThrowWhenExpectedPrimitiveArrayNotObjectInArray(){
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_ARRAY_WITH_PRIMITIVE_TYPES, "array", "array[0].name","['valueOfArray0']"));
        //when
        //given
        setUpMockPickupKeysOrder("array[0]","array[0].name");
        PropertiesToJsonParser.parseToJson(addWrongParams(initProperties(), "array[0].name"));
    }

    @Test
    public void ThrowWhenExpectedJsonObjectArrayNotPrimitiveArray(){
        //then
        expectedEx.expect(ParsePropertiesException.class);
        expectedEx.expectMessage(String.format(EXPECTED_ARRAY_WITH_JSON_OBJECT_TYPES, "array","array[0]","[{'name':'array[0].nameVALUE'}]"));
        //when
        //given
        setUpMockPickupKeysOrder("array[0].name","array[0]");
        PropertiesToJsonParser.parseToJson(addWrongParams(initProperties(), "array[0].name"));
    }

    private void setUpMockPickupKeysOrder(String... keys) {
        PropertyKeysPickupOrderedForTest pickupOrderedForTest= new PropertyKeysPickupOrderedForTest();
        pickupOrderedForTest.setUpMockKeys(keys);
        PropertiesToJsonParser.setPropertyKeysPickup(pickupOrderedForTest);
    }

    private Map<String, String> initProperties(){
        Map<String,String> properties = new HashMap<>();
        properties.put("man.someField","test");
        properties.put("field1","test");
        properties.put("groups[0]","test");
        properties.put("array[0]","valueOfArray0");
        return properties;
    }

    private Map<String,String> addWrongParams(Map<String, String> properties, String... wrongKeys){
        for (String wrongKey: wrongKeys){
            properties.put(wrongKey,wrongKey+"VALUE");
        }

        return properties;
    }
}
