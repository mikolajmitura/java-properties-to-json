package pl.jalokim.propertiestojson.object;

import static pl.jalokim.propertiestojson.Constants.ARRAY_END_SIGN;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;
import static pl.jalokim.propertiestojson.Constants.EMPTY_STRING;
import static pl.jalokim.propertiestojson.Constants.NEW_LINE_SIGN;
import static pl.jalokim.propertiestojson.object.JsonNullReferenceType.NULL_OBJECT;
import static pl.jalokim.propertiestojson.object.MergableObject.mergeObjectIfPossible;
import static pl.jalokim.utils.collection.CollectionUtils.getLastIndex;
import static pl.jalokim.utils.collection.CollectionUtils.isLastIndex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import pl.jalokim.propertiestojson.PropertyArrayHelper;
import pl.jalokim.propertiestojson.path.PathMetadata;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;
import pl.jalokim.propertiestojson.util.exception.CannotOverrideFieldException;

public class ArrayJsonType extends AbstractJsonType implements MergableObject<ArrayJsonType> {

    public static final int INIT_SIZE = 100;
    private AbstractJsonType[] elements = new AbstractJsonType[INIT_SIZE];
    private int maxIndex = -1;

    public ArrayJsonType() {
    }

    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public ArrayJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Collection<?> elements, PathMetadata currentPathMetadata, String propertyKey) {
        Iterator<?> iterator = elements.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Object element = iterator.next();
            addElement(index, primitiveJsonTypesResolver.resolvePrimitiveTypeAndReturn(element, propertyKey), currentPathMetadata);
            index++;
        }
    }

    public static ArrayJsonType createOrGetNextDimensionOfArray(ArrayJsonType currentArray, List<Integer> indexes, int indexToTest,
        PathMetadata currentPathMetadata) {
        if (currentArray.existElementByGivenIndex(indexes.get(indexToTest))) {
            AbstractJsonType element = currentArray.getElement(indexes.get(indexToTest));
            if (element instanceof ArrayJsonType) {
                return (ArrayJsonType) element;
            } else {
                List<Integer> currentIndexes = indexes.subList(0, indexToTest + 1);
                String indexesAsText = currentIndexes.stream()
                    .map(Object::toString)
                    .reduce(EMPTY_STRING, (oldText, index) -> oldText + ARRAY_START_SIGN + index + ARRAY_END_SIGN);
                throw new CannotOverrideFieldException(currentPathMetadata.getCurrentFullPathWithoutIndexes() + indexesAsText, element,
                    currentPathMetadata.getOriginalPropertyKey());
            }
        } else {
            ArrayJsonType newArray = new ArrayJsonType();
            currentArray.addElement(indexes.get(indexToTest), newArray, currentPathMetadata);
            return newArray;
        }
    }

    public void addElement(int index, AbstractJsonType elementToAdd, PathMetadata currentPathMetadata) {
        if (maxIndex < index) {
            maxIndex = index;
        }
        rewriteArrayWhenIsFull(index);
        AbstractJsonType oldObject = elements[index];

        if (oldObject == null) {
            elements[index] = elementToAdd;
        } else {
            if (oldObject instanceof MergableObject && elementToAdd instanceof MergableObject) {
                mergeObjectIfPossible(oldObject, elementToAdd, currentPathMetadata);
            } else {
                throw new CannotOverrideFieldException(currentPathMetadata.getCurrentFullPath(), oldObject, currentPathMetadata.getOriginalPropertyKey());
            }
        }
    }

    public void addElement(PropertyArrayHelper propertyArrayHelper, AbstractJsonType elementToAdd, PathMetadata currentPathMetadata) {
        List<Integer> indexes = propertyArrayHelper.getDimensionalIndexes();
        int size = propertyArrayHelper.getDimensionalIndexes().size();
        ArrayJsonType currentArray = this;
        for (int index = 0; index < size; index++) {
            if (isLastIndex(propertyArrayHelper.getDimensionalIndexes(), index)) {
                currentArray.addElement(indexes.get(index), elementToAdd, currentPathMetadata);
            } else {
                currentArray = createOrGetNextDimensionOfArray(currentArray, indexes, index, currentPathMetadata);
            }
        }
    }

    public AbstractJsonType getElementByGivenDimIndexes(PathMetadata currentPathMetaData) {
        PropertyArrayHelper propertyArrayHelper = currentPathMetaData.getPropertyArrayHelper();
        List<Integer> indexes = propertyArrayHelper.getDimensionalIndexes();
        int size = propertyArrayHelper.getDimensionalIndexes().size();
        ArrayJsonType currentArray = this;
        for (int i = 0; i < size; i++) {
            if (isLastIndex(propertyArrayHelper.getDimensionalIndexes(), i)) {
                return currentArray.getElement(indexes.get(i));
            } else {
                AbstractJsonType element = currentArray.getElement(indexes.get(i));
                if (element == null) {
                    return null;
                }
                if (element instanceof ArrayJsonType) {
                    currentArray = (ArrayJsonType) element;
                } else {
                    List<Integer> currentIndexes = indexes.subList(0, i + 1);
                    String indexesAsText = currentIndexes.stream()
                        .map(Object::toString)
                        .reduce(EMPTY_STRING, (oldText, index) -> oldText + ARRAY_START_SIGN + index + ARRAY_END_SIGN);
                    throw new CannotOverrideFieldException(currentPathMetaData.getCurrentFullPathWithoutIndexes() + indexesAsText, element,
                        currentPathMetaData.getOriginalPropertyKey());
                }
            }
        }
        throw new UnsupportedOperationException(
            "cannot return expected object for " + currentPathMetaData.getCurrentFullPath() + " " + currentPathMetaData.getPropertyArrayHelper()
                .getDimensionalIndexes());
    }

    public boolean existElementByGivenIndex(int index) {
        return getElement(index) != null;
    }

    private void rewriteArrayWhenIsFull(int index) {
        if (indexHigherThanArraySize(index)) {
            int predictedNewSize = elements.length + INIT_SIZE;
            int newSize = predictedNewSize > index ? predictedNewSize : index + 1;
            AbstractJsonType[] elementsTemp = new AbstractJsonType[newSize];
            System.arraycopy(elements, 0, elementsTemp, 0, elements.length);
            elements = elementsTemp;
        }
    }

    private boolean indexHigherThanArraySize(int index) {
        return index > getLastIndex(elements);
    }

    public AbstractJsonType getElement(int index) {
        rewriteArrayWhenIsFull(index);
        return elements[index];
    }

    @Override
    public String toStringJson() {
        StringBuilder result = new StringBuilder().append(ARRAY_START_SIGN);
        int index = 0;
        List<AbstractJsonType> elementsAsList = convertToListWithoutRealNull();
        int lastIndex = getLastIndex(elementsAsList);
        for (AbstractJsonType element : elementsAsList) {
            if (!(element instanceof SkipJsonField)) {
                String lastSign = index == lastIndex ? EMPTY_STRING : NEW_LINE_SIGN;
                result.append(element.toStringJson())
                    .append(lastSign);
            }
            index++;
        }
        return result.append(ARRAY_END_SIGN).toString();
    }

    public List<AbstractJsonType> convertToListWithoutRealNull() {
        List<AbstractJsonType> elementsList = new ArrayList<>();

        for (int i = 0; i < maxIndex + 1; i++) {
            AbstractJsonType element = elements[i];
            if (element == null) {
                elementsList.add(NULL_OBJECT);
            } else {
                elementsList.add(element);
            }
        }
        return elementsList;
    }

    private List<AbstractJsonType> convertToListWithRealNull() {
        List<AbstractJsonType> elementsList = new ArrayList<>();
        for (int i = 0; i < maxIndex + 1; i++) {
            AbstractJsonType element = elements[i];
            elementsList.add(element);
        }
        return elementsList;
    }

    @Override
    public void merge(ArrayJsonType mergeWith, PathMetadata currentPathMetadata) {
        int index = 0;
        for (AbstractJsonType abstractJsonType : mergeWith.convertToListWithRealNull()) {
            addElement(index, abstractJsonType, currentPathMetadata);
            index++;
        }
    }
}
