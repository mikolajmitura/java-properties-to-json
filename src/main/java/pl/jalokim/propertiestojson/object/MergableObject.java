package pl.jalokim.propertiestojson.object;

import static java.lang.String.format;

public interface MergableObject<T extends AbstractJsonType> {
    void merge(T mergeWith);

    static void mergeObjectIfPossible(AbstractJsonType oldJsonElement, AbstractJsonType elementToAdd) {
        MergableObject oldObject = (MergableObject) oldJsonElement;
        if (oldObject.getClass().isAssignableFrom(elementToAdd.getClass())) {
            oldObject.merge(elementToAdd);
        } else {
            // TODO test this
            throw new RuntimeException(
                    format("Cannot merge objects with different types, old object %s%n new object: %s",
                           oldObject, elementToAdd));
        }
    }
}
