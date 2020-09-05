package pl.jalokim.propertiestojson.resolvers.primitives.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.object.BooleanToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.object.NumberToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.object.ObjectToJsonTypeConverter;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToBooleanResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToConcreteObjectResolver;
import pl.jalokim.propertiestojson.resolvers.primitives.string.TextToNumberResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static pl.jalokim.propertiestojson.Constants.ARRAY_END_SIGN;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;
import static pl.jalokim.propertiestojson.Constants.JSON_OBJECT_END;
import static pl.jalokim.propertiestojson.Constants.JSON_OBJECT_START;
import static pl.jalokim.propertiestojson.object.JsonNullReferenceType.NULL_OBJECT;
import static pl.jalokim.propertiestojson.resolvers.primitives.object.NullToJsonTypeConverter.NULL_TO_JSON_RESOLVER;
import static pl.jalokim.propertiestojson.resolvers.primitives.object.StringToJsonTypeConverter.STRING_TO_JSON_RESOLVER;
import static pl.jalokim.propertiestojson.resolvers.primitives.string.TextToNumberResolver.convertToNumber;
import static pl.jalokim.propertiestojson.resolvers.primitives.string.TextToStringResolver.TO_STRING_RESOLVER;

public class JsonObjectHelper {

    private static final PrimitiveJsonTypesResolver primitiveJsonTypesResolver;
    private static final JsonParser jp = new JsonParser();
    private static final Gson gson = new Gson();

    static {
        List<ObjectToJsonTypeConverter<?>> toJsonResolvers = new ArrayList<>();
        toJsonResolvers.add(new NumberToJsonTypeConverter());
        toJsonResolvers.add(new BooleanToJsonTypeConverter());
        toJsonResolvers.add(STRING_TO_JSON_RESOLVER);

        List<TextToConcreteObjectResolver<?>> toObjectsResolvers = new ArrayList<>();
        toObjectsResolvers.add(new TextToNumberResolver());
        toObjectsResolvers.add(new TextToBooleanResolver());
        toObjectsResolvers.add(TO_STRING_RESOLVER);
        primitiveJsonTypesResolver = new PrimitiveJsonTypesResolver(toObjectsResolvers, toJsonResolvers, false, NULL_TO_JSON_RESOLVER);
    }

    private JsonObjectHelper() {
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static JsonElement toJsonElement(String json) {
        return jp.parse(json);
    }

    public static ObjectJsonType createObjectJsonType(JsonElement parsedJson, String propertyKey) {
        ObjectJsonType objectJsonType = new ObjectJsonType();
        JsonObject asJsonObject = parsedJson.getAsJsonObject();
        for(Map.Entry<String, JsonElement> entry : asJsonObject.entrySet()) {
            JsonElement someField = entry.getValue();
            AbstractJsonType valueOfNextField = convertToAbstractJsonType(someField, propertyKey);
            objectJsonType.addField(entry.getKey(), valueOfNextField, null);
        }
        return objectJsonType;
    }

    public static AbstractJsonType convertToAbstractJsonType(JsonElement someField, String propertyKey) {
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

    public static ArrayJsonType createArrayJsonType(JsonElement parsedJson, String propertyKey) {
        ArrayJsonType arrayJsonType = new ArrayJsonType();
        JsonArray asJsonArray = parsedJson.getAsJsonArray();
        int index = 0;
        for(JsonElement element : asJsonArray) {
            arrayJsonType.addElement(index, convertToAbstractJsonType(element, propertyKey), null);
            index++;
        }
        return arrayJsonType;
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

    public static boolean hasJsonObjectSignature(String propertyValue) {
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
}
