package pl.jalokim.propertiestojson.util;

import java.util.Collection;
import java.util.List;

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
}
