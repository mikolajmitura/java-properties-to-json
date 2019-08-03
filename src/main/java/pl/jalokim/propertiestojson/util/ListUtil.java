package pl.jalokim.propertiestojson.util;

import java.util.Collection;
import java.util.List;

import static java.lang.String.format;

public class ListUtil {
    public static boolean isLastIndex(List<?> list, int index) {
        return getLastIndex(list) == index;
    }

    public static int getLastIndex(Collection<?> list) {
        return list.size() - 1;
    }

    public static int getLastIndex(Object[] array) {
        return array.length - 1;
    }

    public static String everyElementAsNewLine(Collection<?> collection) {
        StringBuilder stringBuilder = new StringBuilder();
        for(Object element : collection) {
            stringBuilder.append(element.toString());
            stringBuilder.append(format("%n"));
        }
        return stringBuilder.toString();
    }
}
