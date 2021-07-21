package pl.jalokim.propertiestojson.exception;

public class NotLeafValueException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotLeafValueException(String message) {
        super(message);
    }
}
