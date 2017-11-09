package pl.jalokim.propertiestojson.util.exception;

public class ParsePropertiesException extends RuntimeException {

    private static final String REASON_DESCRIBE = " because already this field has value: %s";
    private static final String FIRST_PART_OF_MESSAGE = "Current field: '%s' in given property key: '%s' is already considered as ";
    public static final String EXPECTED_OBJECT_JSON_TYPE = FIRST_PART_OF_MESSAGE + "JSON object" + REASON_DESCRIBE;
    public static final String EXPECTED_PRIMITIVE_JSON_TYPE = FIRST_PART_OF_MESSAGE + "primitive type" + REASON_DESCRIBE;
    public static final String EXPECTED_ARRAY_JSON_TYPE = FIRST_PART_OF_MESSAGE + "array JSON type" + REASON_DESCRIBE;
    public static final String EXPECTED_ARRAY_WITH_PRIMITIVE_TYPES = FIRST_PART_OF_MESSAGE + "array only for primitive types" + REASON_DESCRIBE;
    public static final String EXPECTED_ARRAY_WITH_JSON_OBJECT_TYPES = FIRST_PART_OF_MESSAGE + "array only for JSON object types" + REASON_DESCRIBE;

    public ParsePropertiesException(String message) {
        super(message);
    }
}
