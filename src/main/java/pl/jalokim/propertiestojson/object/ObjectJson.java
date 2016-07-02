package pl.jalokim.propertiestojson.object;

import pl.jalokim.propertiestojson.util.JsonStringWrapper;

import java.util.HashMap;
import java.util.Map;

public class ObjectJson extends AbstractJsonType {
    public static final String EMPTY_STRING = "";
    public static final String NEW_LINE_SIGN = ",\n";
    Map<String, AbstractJsonType> fields = new HashMap<>();
    private static final String JSON_OBJECT_START = "{\n";
    private static final String JSON_OBJECT_END = "}\n";

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
        int lastIndex = fields.size() - 1;
        for (String fieldName : fields.keySet()) {
            AbstractJsonType object = fields.get(fieldName);
            String lastSign = index == lastIndex ? EMPTY_STRING : NEW_LINE_SIGN;
            result.append(JsonStringWrapper.wrap(fieldName))
                    .append(":")
                    .append(object.toStringJson())
                    .append(lastSign);
            index++;
        }
        result.append(JSON_OBJECT_END);
        return result.toString();
    }

}
