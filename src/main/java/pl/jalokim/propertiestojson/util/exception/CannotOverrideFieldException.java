package pl.jalokim.propertiestojson.util.exception;

import com.google.common.annotations.VisibleForTesting;
import pl.jalokim.propertiestojson.object.AbstractJsonType;

public class CannotOverrideFieldException extends RuntimeException {

    private static final String CANNOT_OVERRIDE_VALUE = "Cannot override value at path: '%s', current value is: '%s', problematic property key: '%s'";

    public CannotOverrideFieldException(String currentPath, AbstractJsonType currentValue, String propertyKey) {
        this(currentPath, currentValue.toString(), propertyKey);
    }

    @VisibleForTesting
    public CannotOverrideFieldException(String currentPath, String currentValue, String propertyKey) {
        super(String.format(CANNOT_OVERRIDE_VALUE, currentPath, currentValue, propertyKey));
    }
}
