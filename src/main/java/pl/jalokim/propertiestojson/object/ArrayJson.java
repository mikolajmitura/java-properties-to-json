package pl.jalokim.propertiestojson.object;


import pl.jalokim.propertiestojson.util.JsonStringWrapper;

import java.util.ArrayList;
import java.util.List;

import static pl.jalokim.propertiestojson.Constants.ARRAY_END_SIGN;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;
import static pl.jalokim.propertiestojson.Constants.NEW_LINE_SIGN;
import static pl.jalokim.propertiestojson.Constants.EMPTY_STRING;


public class ArrayJson extends AbstractJsonType{

    public List<AbstractJsonType> elements = new ArrayList<>();

    public void addElement(AbstractJsonType element){
        elements.add(element);
    }

    public ArrayJson(){

    }

    public ArrayJson(String[] elements){
        for (String element : elements){
            addElement(new StringJson(element.trim()));
        }
    }

    @Override
    public String toStringJson() {
        StringBuilder result = new StringBuilder().append(ARRAY_START_SIGN);
        int index = 0;
        int lastIndex = elements.size() - 1;
        for (AbstractJsonType element : elements) {
            String lastSign = index == lastIndex ? EMPTY_STRING : NEW_LINE_SIGN;
            result.append(element.toStringJson())
                    .append(lastSign);
            index++;
        }
        return result.append(ARRAY_END_SIGN).toString();
    }
}
