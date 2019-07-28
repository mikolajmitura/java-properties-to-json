package pl.jalokim.propertiestojson.object;


import pl.jalokim.propertiestojson.PropertyArrayHelper;
import pl.jalokim.propertiestojson.path.PathMetadata;
import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static pl.jalokim.propertiestojson.Constants.ARRAY_END_SIGN;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;
import static pl.jalokim.propertiestojson.Constants.EMPTY_STRING;
import static pl.jalokim.propertiestojson.Constants.NEW_LINE_SIGN;
import static pl.jalokim.propertiestojson.util.ListUtil.getLastIndex;
import static pl.jalokim.propertiestojson.util.ListUtil.isLastIndex;


public class ArrayJsonType extends AbstractJsonType {

    public static final int INIT_SIZE = 100;
    private AbstractJsonType[] elements = new AbstractJsonType[INIT_SIZE];

    public void addElement(int index, AbstractJsonType element) {
        rewriteArrayWhenIsFull(index);
        elements[index] = element;
    }

    public void addElement(PropertyArrayHelper propertyArrayHelper, AbstractJsonType elementToAdd) {
        List<Integer> indexes = propertyArrayHelper.getDimensionalIndexes();
        int size = propertyArrayHelper.getDimensionalIndexes().size();
        ArrayJsonType currentArray = this;
        for(int index = 0; index < size; index++) {
            if(isLastIndex(propertyArrayHelper.getDimensionalIndexes(), index)) {
                currentArray.addElement(indexes.get(index), elementToAdd);
            } else {
                currentArray = createOrGetNextDimensionOfArray(currentArray, indexes, index);
            }
        }
    }

    public static ArrayJsonType createOrGetNextDimensionOfArray(ArrayJsonType currentArray, List<Integer> indexes, int index) {
        if(currentArray.existElementByGivenIndex(indexes.get(index))) {
            AbstractJsonType element = currentArray.getElement(indexes.get(index));
            if(element instanceof ArrayJsonType) {
                return (ArrayJsonType) element;
            } else {
                // TODO done it and test this one
                // expected type which is in (AbstractJsonType element)  at given array in given path...
                //throwException();
                List<Integer> currentIndexes = indexes.subList(0, index);
                throw new RuntimeException("current type " + element.getClass() + " with value: " + element
                                           + " at given array in given path " + currentIndexes);
            }
        } else {
            ArrayJsonType newArray = new ArrayJsonType();
            currentArray.addElement(indexes.get(index), newArray);
            return newArray;
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
            }  else {
                AbstractJsonType element = currentArray.getElement(indexes.get(i));
                if(element == null) {
                    return null;
                }
                if (element instanceof ArrayJsonType) {
                    currentArray = (ArrayJsonType) element;
                } else {
                    // TODO done it and test this one
                    // expected type which is in (AbstractJsonType element)  at given array in given path...
                    //throwException();
                    List<Integer> currentIndexes = indexes.subList(0, i);
                    throw new RuntimeException("expected type " + element.getClass() +  " at given array in given path: " + currentPathMetaData.getCurrentFullPath() + " current indexes: " +  currentIndexes);
                }
            }
        }
        throw new UnsupportedOperationException("cannot return expected object for " + currentPathMetaData.getCurrentFullPath() + " " + currentPathMetaData.getPropertyArrayHelper().getDimensionalIndexes());
    }

    public boolean existElementByGivenIndex(int index) {
        return getElement(index) != null;
    }

    private void rewriteArrayWhenIsFull(int index) {
        // TODO fix it, when someone put 1000 and array is empty then will ArrayIndexOutOfBoundsException arise
        if(indexHigherThanArraySize(index)) {
            AbstractJsonType[] elementsTemp = new AbstractJsonType[elements.length + INIT_SIZE];
            for(int i = 0; i < elements.length; i++) {
                elementsTemp[i] = elements[i];
            }
            elements = elementsTemp;
        }
    }

    private boolean indexHigherThanArraySize(int index) {
        return index > getLastIndex(elements);
    }

    public ArrayJsonType() {

    }

    public AbstractJsonType getElement(int index) {
        rewriteArrayWhenIsFull(index);
        return elements[index];
    }

    public ArrayJsonType(PrimitiveJsonTypesResolver primitiveJsonTypesResolver, Collection<?> elements) {
        Iterator<?> iterator = elements.iterator();
        int index = 0;
        while(iterator.hasNext()) {
            Object element = iterator.next();
            addElement(index, primitiveJsonTypesResolver.resolvePrimitiveTypeAndReturn(element));
            index++;
        }
    }

    @Override
    public String toStringJson() {
        StringBuilder result = new StringBuilder().append(ARRAY_START_SIGN);
        int index = 0;
        List<AbstractJsonType> elementsAsList = convertToList();
        int lastIndex = getLastIndex(elementsAsList);
        for(AbstractJsonType element : elementsAsList) {
            String lastSign = index == lastIndex ? EMPTY_STRING : NEW_LINE_SIGN;
            result.append(element.toStringJson())
                  .append(lastSign);
            index++;
        }
        return result.append(ARRAY_END_SIGN).toString();
    }

    private List<AbstractJsonType> convertToList() {
        List<AbstractJsonType> elementsList = new ArrayList<>();
        for(AbstractJsonType element : elements) {
            if(element != null) {
                elementsList.add(element);
            }
        }
        return elementsList;
    }
}
