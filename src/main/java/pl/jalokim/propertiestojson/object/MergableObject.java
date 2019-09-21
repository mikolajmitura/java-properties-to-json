package pl.jalokim.propertiestojson.object;

import pl.jalokim.propertiestojson.path.PathMetadata;
import pl.jalokim.propertiestojson.util.exception.MergeObjectException;

@SuppressWarnings("unchecked")
public interface MergableObject<T extends AbstractJsonType> {
    void merge(T mergeWith, PathMetadata currentPathMetadata);

    static void mergeObjectIfPossible(AbstractJsonType oldJsonElement, AbstractJsonType elementToAdd, PathMetadata currentPathMetadata) {
        MergableObject oldObject = (MergableObject) oldJsonElement;
        if (oldObject.getClass().isAssignableFrom(elementToAdd.getClass())) {
            oldObject.merge(elementToAdd, currentPathMetadata);
        } else {
            throw new MergeObjectException(oldJsonElement, elementToAdd, currentPathMetadata);
        }
    }
}
