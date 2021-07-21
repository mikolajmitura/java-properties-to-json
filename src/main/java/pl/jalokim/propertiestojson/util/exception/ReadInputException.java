package pl.jalokim.propertiestojson.util.exception;

public class ReadInputException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReadInputException(Exception ex) {
        super(ex);
    }
}
