package pl.jalokim.propertiestojson.object;


import pl.jalokim.propertiestojson.resolvers.PrimitiveJsonTypesResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static pl.jalokim.propertiestojson.Constants.ARRAY_END_SIGN;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;
import static pl.jalokim.propertiestojson.Constants.EMPTY_STRING;
import static pl.jalokim.propertiestojson.Constants.NEW_LINE_SIGN;


public class ArrayJsonType extends AbstractJsonType {

    public static final int INIT_SIZE = 100;
    private AbstractJsonType[] elements = new AbstractJsonType[INIT_SIZE];

    public void addElement(int index, AbstractJsonType element) {
        rewriteArrayWhenIsFull(index);
        elements[index] = element;
    }

    private void rewriteArrayWhenIsFull(int index) {
        if (indexHigherThanArraySize(index)) {
            AbstractJsonType[] elementsTemp = new AbstractJsonType[elements.length + INIT_SIZE];
            for (int i = 0; i < elements.length; i++) {
                elementsTemp[i] = elements[i];
            }
            elements = elementsTemp;
        }
    }

    private boolean indexHigherThanArraySize(int index) {
        return index > elements.length - 1;
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
        while (iterator.hasNext()) {
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
        int lastIndex = elementsAsList.size() - 1;
        for (AbstractJsonType element : elementsAsList) {
            String lastSign = index == lastIndex ? EMPTY_STRING : NEW_LINE_SIGN;
            result.append(element.toStringJson())
                    .append(lastSign);
            index++;
        }
        return result.append(ARRAY_END_SIGN).toString();
    }

    private List<AbstractJsonType> convertToList() {
        List<AbstractJsonType> elementsList = new ArrayList<>();
        for (AbstractJsonType element : elements) {
            if (element != null) {
                elementsList.add(element);
            }
        }
        return elementsList;
    }
}
