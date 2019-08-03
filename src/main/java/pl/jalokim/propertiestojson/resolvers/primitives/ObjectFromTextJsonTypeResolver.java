package pl.jalokim.propertiestojson.resolvers.primitives;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.object.StringJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.util.PropertiesToJsonConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static pl.jalokim.propertiestojson.Constants.ARRAY_END_SIGN;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;
import static pl.jalokim.propertiestojson.Constants.JSON_OBJECT_END;
import static pl.jalokim.propertiestojson.Constants.JSON_OBJECT_START;
import static pl.jalokim.propertiestojson.object.JsonNullReferenceType.NULL_OBJECT;
import static pl.jalokim.propertiestojson.resolvers.primitives.NumberJsonTypeResolver.convertToNumber;

/**
 * When given text contains parsable json value, json object or json array then try build instance of ObjectJsonType or ArrayJsonType
 * It will invoke {@link #returnConcreteJsonType(PrimitiveJsonTypesResolver, Object, String)} after conversion from string (raw property value to some object)
 */
public class ObjectFromTextJsonTypeResolver extends PrimitiveJsonTypeResolver<Object> {

    private final PrimitiveJsonTypesResolver primitiveJsonTypesResolver;
    private final Gson gson = new Gson();
    private final JsonParser jp = new JsonParser();

    {
        List<PrimitiveJsonTypeResolver> resolvers  = new ArrayList<>();
        resolvers.add(new NumberJsonTypeResolver());
        resolvers.add(new BooleanJsonTypeResolver());
        resolvers.add(PropertiesToJsonConverter.STRING_RESOLVER);
        primitiveJsonTypesResolver = new PrimitiveJsonTypesResolver(resolvers);
    }

    @Override
    public Object returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue, String propertyKey) {
        if(hasJsonObjectSignature(propertyValue) || hasJsonArraySignature(propertyValue)) {
            try {
                return convertToAbstractJsonType(jp.parse(propertyValue), propertyKey);
            } catch(Exception exception) {
                return null;
            }
        }
        return null;
    }

    public static boolean isValidJsonObjectOrArray(String propertyValue) {
        if(hasJsonObjectSignature(propertyValue) || hasJsonArraySignature(propertyValue)) {
            JsonParser jp = new JsonParser();
            try {
                jp.parse(propertyValue);
                return true;
            } catch(Exception ex) {
                return false;
            }
        }
        return false;
    }

    public static boolean hasJsonArraySignature(String propertyValue) {
        return hasJsonSignature(propertyValue.trim(), ARRAY_START_SIGN, ARRAY_END_SIGN);
    }

    private static boolean hasJsonObjectSignature(String propertyValue) {
        return hasJsonSignature(propertyValue.trim(), JSON_OBJECT_START, JSON_OBJECT_END);
    }

    private static boolean hasJsonSignature(String propertyValue, String startSign, String endSign) {
        return firsLetter(propertyValue).contains(startSign) && lastLetter(propertyValue).contains(endSign);
    }

    private static String firsLetter(String text) {
        return text.substring(0, 1);
    }

    private static String lastLetter(String text) {
        return text.substring(text.length() - 1);
    }

    /**
     * It return instance of ArrayJsonType or ObjectJsonType
     * if propertyValue is not one of above types then will convert it to gson JsonElement instance and then convert to ArrayJsonType, ObjectJsonType, StringJsonType
     * @param primitiveJsonTypesResolver resolver which is main resolver for leaf value from property
     * @param propertyValue property key.
     * @return instance of ArrayJsonType, ObjectJsonType or StringJsonType
     */
    @Override
    public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue, String propertyKey) {
        if(ObjectJsonType.class.isAssignableFrom(propertyValue.getClass()) ||  ArrayJsonType.class.isAssignableFrom(propertyValue.getClass())) {
            return (AbstractJsonType) propertyValue;
        }
        return convertToObjectArrayOrJsonText(jp.parse(gson.toJson(propertyValue)), propertyKey);
    }

    private AbstractJsonType convertToObjectArrayOrJsonText(JsonElement someField, String propertyKey) {
        AbstractJsonType valueOfNextField = null;
        if(someField.isJsonNull()) {
            valueOfNextField = NULL_OBJECT;
        }
        if(someField.isJsonObject()) {
            valueOfNextField = createObjectJsonType(someField, propertyKey);
        }
        if(someField.isJsonArray()) {
            valueOfNextField = createArrayJsonType(someField, propertyKey);
        }
        if(someField.isJsonPrimitive()) {
            return new StringJsonType(someField.toString());
        }
        return valueOfNextField;
    }

    private AbstractJsonType convertToAbstractJsonType(JsonElement someField, String propertyKey) {
        AbstractJsonType valueOfNextField = null;
        if(someField.isJsonNull()) {
            valueOfNextField = NULL_OBJECT;
        }
        if(someField.isJsonObject()) {
            valueOfNextField = createObjectJsonType(someField, propertyKey);
        }
        if(someField.isJsonArray()) {
            valueOfNextField = createArrayJsonType(someField, propertyKey);
        }
        if(someField.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = someField.getAsJsonPrimitive();
            if(jsonPrimitive.isString()) {
                valueOfNextField = primitiveJsonTypesResolver.resolvePrimitiveTypeAndReturn(jsonPrimitive.getAsString(), propertyKey);
            } else if(jsonPrimitive.isNumber()) {
                String numberAsText = jsonPrimitive.getAsNumber().toString();
                valueOfNextField = primitiveJsonTypesResolver.resolvePrimitiveTypeAndReturn(convertToNumber(numberAsText), propertyKey);
            } else if(jsonPrimitive.isBoolean()) {
                valueOfNextField = primitiveJsonTypesResolver.resolvePrimitiveTypeAndReturn(jsonPrimitive.getAsBoolean(), propertyKey);
            }
        }
        return valueOfNextField;
    }

    private ObjectJsonType createObjectJsonType(JsonElement parsedJson , String propertyKey) {
        ObjectJsonType objectJsonType = new ObjectJsonType();
        JsonObject asJsonObject = parsedJson.getAsJsonObject();
        for(Map.Entry<String, JsonElement> entry : asJsonObject.entrySet()) {
            JsonElement someField = entry.getValue();
            AbstractJsonType valueOfNextField = convertToAbstractJsonType(someField, propertyKey);
            objectJsonType.addField(entry.getKey(), valueOfNextField, null);
        }
        return objectJsonType;
    }

    private ArrayJsonType createArrayJsonType(JsonElement parsedJson, String propertyKey) {
        ArrayJsonType arrayJsonType = new ArrayJsonType();
        JsonArray asJsonArray = parsedJson.getAsJsonArray();
        int index = 0;
        for(JsonElement element : asJsonArray) {
            arrayJsonType.addElement(index, convertToAbstractJsonType(element, propertyKey), null);
            index++;
        }
        return arrayJsonType;
    }
}
