package pl.jalokim.propertiestojson.object;

import pl.jalokim.propertiestojson.util.StringToJsonStringWrapper;

public class StringJsonType extends PrimitiveJsonType<String> {

    public StringJsonType(String value) {
        super(value.trim());
    }

    @Override
    public String toStringJson() {
        return StringToJsonStringWrapper.wrap(value);
    }
}
