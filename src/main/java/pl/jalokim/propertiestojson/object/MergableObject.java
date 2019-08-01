package pl.jalokim.propertiestojson.object;

import pl.jalokim.propertiestojson.util.exception.MergeObjectException;

public interface MergableObject<T extends AbstractJsonType> {
    void merge(T mergeWith);

    static void mergeObjectIfPossible(AbstractJsonType oldJsonElement, AbstractJsonType elementToAdd) {
        MergableObject oldObject = (MergableObject) oldJsonElement;
        if (oldObject.getClass().isAssignableFrom(elementToAdd.getClass())) {
            oldObject.merge(elementToAdd);
        } else {
            // TODO test this
            throw new MergeObjectException(oldJsonElement, elementToAdd);
        }
    }
}
