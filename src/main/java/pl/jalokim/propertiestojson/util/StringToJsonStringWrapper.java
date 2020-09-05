package pl.jalokim.propertiestojson.util;

public class StringToJsonStringWrapper {

    private static final String JSON_STRING_SCHEMA = "\"%s\"";

    private StringToJsonStringWrapper() {
    }

    public static String wrap(String textToWrap) {
        return String.format(JSON_STRING_SCHEMA, textToWrap.replace("\"", "\\\""));
    }

}
