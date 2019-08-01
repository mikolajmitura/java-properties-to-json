package pl.jalokim.propertiestojson.resolvers.primitives;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.object.ArrayJsonType;
import pl.jalokim.propertiestojson.object.ObjectJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.util.PropertiesToJsonConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static pl.jalokim.propertiestojson.Constants.ARRAY_END_SIGN;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;
import static pl.jalokim.propertiestojson.Constants.JSON_OBJECT_END;
import static pl.jalokim.propertiestojson.Constants.JSON_OBJECT_START;
import static pl.jalokim.propertiestojson.object.JsonNullReferenceType.NULL_OBJECT;
import static pl.jalokim.propertiestojson.resolvers.primitives.NumberJsonTypeResolver.convertToNumber;

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
    public Object returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
        if(hasJsonObjectSignature(propertyValue) || hasJsonArraySignature(propertyValue)) {
            try {
                return convertToAbstractJsonType(jp.parse(propertyValue));
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

    @Override
    public AbstractJsonType returnConcreteJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Object propertyValue) {
        if(ObjectJsonType.class.isAssignableFrom(propertyValue.getClass()) ||  ArrayJsonType.class.isAssignableFrom(propertyValue.getClass())) {
            return (AbstractJsonType) propertyValue;
        }
        return convertToAbstractJsonType(jp.parse(gson.toJson(propertyValue)));
    }

    private AbstractJsonType convertToAbstractJsonType(JsonElement someField) {
        AbstractJsonType valueOfNextField = null;
        if(someField.isJsonNull()) {
            valueOfNextField = NULL_OBJECT;
        }
        if(someField.isJsonObject()) {
            valueOfNextField = createObjectJsonType(someField);
        }
        if(someField.isJsonArray()) {
            valueOfNextField = createArrayJsonType(someField);
        }
        if(someField.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = someField.getAsJsonPrimitive();
            if(jsonPrimitive.isString()) {
                valueOfNextField = primitiveJsonTypesResolver.resolvePrimitiveTypeAndReturn(jsonPrimitive.getAsString());
            } else if(jsonPrimitive.isNumber()) {
                String numberAsText = jsonPrimitive.getAsNumber().toString();
                valueOfNextField = primitiveJsonTypesResolver.resolvePrimitiveTypeAndReturn(convertToNumber(numberAsText));
            } else if(jsonPrimitive.isBoolean()) {
                valueOfNextField = primitiveJsonTypesResolver.resolvePrimitiveTypeAndReturn(jsonPrimitive.getAsBoolean());
            } else {
                throw new JsonParseException("cannot get primitive value from" + jsonPrimitive);
            }
        }
        if(valueOfNextField == null) {
            throw new JsonParseException(format("valueOfNextField is null, why? problematic property key: %s, with value %s", primitiveJsonTypesResolver.getPropertiesKey(), someField));
        }
        return valueOfNextField;
    }

    private ObjectJsonType createObjectJsonType(JsonElement parsedJson) {
        ObjectJsonType objectJsonType = new ObjectJsonType();
        JsonObject asJsonObject = parsedJson.getAsJsonObject();
        for(Map.Entry<String, JsonElement> entry : asJsonObject.entrySet()) {
            JsonElement someField = entry.getValue();
            AbstractJsonType valueOfNextField = convertToAbstractJsonType(someField);
            objectJsonType.addField(entry.getKey(), valueOfNextField, null);
        }
        return objectJsonType;
    }

    private ArrayJsonType createArrayJsonType(JsonElement parsedJson) {
        ArrayJsonType arrayJsonType = new ArrayJsonType();
        JsonArray asJsonArray = parsedJson.getAsJsonArray();
        int index = 0;
        for(JsonElement element : asJsonArray) {
            arrayJsonType.addElement(index, convertToAbstractJsonType(element), null);
            index++;
        }
        return arrayJsonType;
    }
}
