package pl.jalokim.propertiestojson.resolvers.primitives;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import static pl.jalokim.propertiestojson.Constants.ARRAY_END_SIGN;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;
import static pl.jalokim.propertiestojson.Constants.JSON_OBJECT_END;
import static pl.jalokim.propertiestojson.Constants.JSON_OBJECT_START;

public class ObjectFromTextJsonTypeResolver extends PrimitiveJsonTypeResolver<Object> {

    private Gson gson = new Gson();

    @Override
    public Object returnConcreteValueWhenCanBeResolved(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
        if (hasJsonObjectSignature(propertyValue) || hasJsonArraySignature(propertyValue)) {
            try {
                JsonParser jp = new JsonParser();
                jp.parse(propertyValue);
                return new ObjectFromTextJsonType(propertyValue);
            } catch (Exception exception) {
                return null;
            }
        }
        return null;
    }

    public static boolean isValidJsonObject(String propertyValue) {
        if (hasJsonObjectSignature(propertyValue)) {
            JsonParser jp = new JsonParser();
            jp.parse(propertyValue);
            return true;
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
        if (ObjectFromTextJsonType.class.isAssignableFrom(propertyValue.getClass())) {
            return (ObjectFromTextJsonType) propertyValue;
        }
        return new ObjectFromTextJsonType(gson.toJson(propertyValue));
    }

    // TODO maybe should be ObjectJsonType not wrapped json... but how???
    // TODO test it...
    public static class ObjectFromTextJsonType extends AbstractJsonType {

        private String json;

        private ObjectFromTextJsonType(String json) {
            this.json = json;
        }

        @Override
        public String toStringJson() {
            return json;
        }
    }
}
