package pl.jalokim.propertiestojson.object;


import java.util.ArrayList;
import java.util.List;

import static pl.jalokim.propertiestojson.Constants.ARRAY_END_SIGN;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;
import static pl.jalokim.propertiestojson.Constants.NEW_LINE_SIGN;
import static pl.jalokim.propertiestojson.Constants.EMPTY_STRING;


public class ArrayJson extends AbstractJsonType {

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

    public ArrayJson() {

    }

    public AbstractJsonType getElement(int index) {
        return elements[index];
    }

    public ArrayJson(String[] elements) {
        for (int index = 0; index < elements.length; index++) {
            String element = elements[index];
            addElement(index, new StringJson(element.trim()));
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
