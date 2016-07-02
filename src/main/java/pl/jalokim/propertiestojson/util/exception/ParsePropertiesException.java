package pl.jalokim.propertiestojson.util.exception;

public class ParsePropertiesException extends RuntimeException{

    public static final String UNEXPECTED_PRIMITIVE_TYPE = "Already given key %s is resonable as object not as primitive Type";
    public static final String UNEXPECTED_JSON_OBJECT = "Already given key %s is resonale as primitive type not as JSON Object";

    public ParsePropertiesException(String message){
        super(message);
    }
}
