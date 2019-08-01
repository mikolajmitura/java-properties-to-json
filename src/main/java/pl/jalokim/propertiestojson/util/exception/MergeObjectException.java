package pl.jalokim.propertiestojson.util.exception;

import pl.jalokim.propertiestojson.object.AbstractJsonType;

import static java.lang.String.format;

public class MergeObjectException extends RuntimeException {
    public MergeObjectException(AbstractJsonType oldJsonElement, AbstractJsonType elementToAdd) {
        super(format("Cannot merge objects with different types:%n Old object: %s%n New object: %s",
                     oldJsonElement, elementToAdd));
    }
}
