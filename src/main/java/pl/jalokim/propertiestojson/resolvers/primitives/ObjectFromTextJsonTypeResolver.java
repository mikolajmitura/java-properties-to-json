package pl.jalokim.propertiestojson.resolvers.primitives;

import com.google.gson.JsonParser;
import pl.jalokim.propertiestojson.Constants;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import static pl.jalokim.propertiestojson.Constants.JSON_OBJECT_END;
import static pl.jalokim.propertiestojson.Constants.JSON_OBJECT_START;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;
import static pl.jalokim.propertiestojson.Constants.ARRAY_END_SIGN;

public class ObjectFromTextJsonTypeResolver extends PrimitiveJsonTypeResolver {

    @Override
    public AbstractJsonType returnJsonTypeWhenCanBeParsed(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, String propertyValue) {
        if (hasJsonSignature(propertyValue, JSON_OBJECT_START, JSON_OBJECT_END) ||
                (hasJsonSignature(propertyValue, ARRAY_START_SIGN, ARRAY_END_SIGN))) {
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

    private boolean hasJsonSignature(String propertyValue, String startSign, String endSign) {
        return firsLetter(propertyValue).contains(startSign) && lastLetter(propertyValue).contains(endSign);
    }

    private String firsLetter(String text) {
        return text.substring(0, 1);
    }

    private String lastLetter(String text) {
        return text.substring(text.length() - 1, text.length());
    }

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
