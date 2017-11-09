package pl.jalokim.propertiestojson;

import static pl.jalokim.propertiestojson.Constants.ARRAY_END_SIGN;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;

public class PropertyArrayHelper {

    private int indexArray;
    private String arrayfieldName;

    public PropertyArrayHelper(String field) {
        int indexOfStartArraySign = field.indexOf(ARRAY_START_SIGN) + 1;
        int indexofEndArraySign = field.indexOf(ARRAY_END_SIGN);
        indexArray = Integer.valueOf(field.substring(indexOfStartArraySign, indexofEndArraySign));
        arrayfieldName = field.substring(0, indexOfStartArraySign - 1);
    }

    public int getIndexArray() {
        return indexArray;
    }

    public String getArrayfieldName() {
        return arrayfieldName;
    }
}
