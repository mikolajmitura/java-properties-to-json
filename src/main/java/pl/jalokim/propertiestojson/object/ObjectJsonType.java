package pl.jalokim.propertiestojson.object;

import pl.jalokim.propertiestojson.util.StringToJsonStringWrapper;

import java.util.HashMap;
import java.util.Map;

import static pl.jalokim.propertiestojson.Constants.JSON_OBJECT_END;
import static pl.jalokim.propertiestojson.Constants.JSON_OBJECT_START;
import static pl.jalokim.propertiestojson.Constants.NEW_LINE_SIGN;
import static pl.jalokim.propertiestojson.Constants.EMPTY_STRING;
import static pl.jalokim.propertiestojson.util.ListUtil.getLastIndex;

public class ObjectJsonType extends AbstractJsonType {

    private Map<String, AbstractJsonType> fields = new HashMap<>();

    public void addField(final String field, final AbstractJsonType object) {
        fields.put(field, object);
    }

    public boolean containsField(String field) {
        return fields.containsKey(field);
    }

    public AbstractJsonType getJsonTypeByFieldName(String field) {
        return fields.get(field);
    }

    @Override
    public String toStringJson() {
        StringBuilder result = new StringBuilder().append(JSON_OBJECT_START);
        int index = 0;
        int lastIndex = getLastIndex(fields.keySet());
        for (String fieldName : fields.keySet()) {
            AbstractJsonType object = fields.get(fieldName);
            String lastSign = index == lastIndex ? EMPTY_STRING : NEW_LINE_SIGN;
            result.append(StringToJsonStringWrapper.wrap(fieldName))
                    .append(":")
                    .append(object.toStringJson())
                    .append(lastSign);
            index++;
        }
        result.append(JSON_OBJECT_END);
        return result.toString();
    }

}
