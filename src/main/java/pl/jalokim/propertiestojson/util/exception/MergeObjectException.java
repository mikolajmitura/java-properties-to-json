package pl.jalokim.propertiestojson.util.exception;

import static java.lang.String.format;

import com.google.common.annotations.VisibleForTesting;
import pl.jalokim.propertiestojson.object.AbstractJsonType;
import pl.jalokim.propertiestojson.path.PathMetadata;

public class MergeObjectException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MergeObjectException(AbstractJsonType oldJsonElement, AbstractJsonType elementToAdd, PathMetadata currentPathMetadata) {
        this(oldJsonElement.toStringJson(), elementToAdd.toStringJson(), currentPathMetadata);
    }

    @VisibleForTesting
    public MergeObjectException(String oldJsonElementValue, String elementToAddValue, PathMetadata currentPathMetadata) {
        super(format("Cannot merge objects with different types:%n Old object: %s%n New object: %s%n problematic key: '%s'%n with value: %s",
            oldJsonElementValue, elementToAddValue, currentPathMetadata.getOriginalPropertyKey(), currentPathMetadata.getRawValue()));
    }
}
