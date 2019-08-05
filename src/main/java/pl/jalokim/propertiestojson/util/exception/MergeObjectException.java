package pl.jalokim.propertiestojson.util.exception;

import com.google.common.annotations.VisibleForTesting;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.path.PathMetadata;

import static java.lang.String.format;

public class MergeObjectException extends RuntimeException {
    public MergeObjectException(AbstractJsonType oldJsonElement, AbstractJsonType elementToAdd, PathMetadata currentPathMetadata) {
        this(oldJsonElement.toStringJson(), elementToAdd.toStringJson(), currentPathMetadata);
    }

    @VisibleForTesting
    public MergeObjectException(String oldJsonElementValue, String elementToAddValue, PathMetadata currentPathMetadata) {
        super(format("Cannot merge objects with different types:%n Old object: %s%n New object: %s%n problematic key: '%s'%n with value: %s",
                     oldJsonElementValue, elementToAddValue, currentPathMetadata.getOriginalPropertyKey(), currentPathMetadata.getValue()));
    }
}
