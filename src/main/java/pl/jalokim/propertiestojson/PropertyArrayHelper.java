package pl.jalokim.propertiestojson;

import lombok.Getter;

import static pl.jalokim.propertiestojson.Constants.ARRAY_END_SIGN;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;

@Getter
public class PropertyArrayHelper {

    private int indexArray;
    private String arrayFieldName;

    public PropertyArrayHelper(String field) {
        int indexOfStartArraySign = field.indexOf(ARRAY_START_SIGN) + 1;
        int indexOfEndArraySign = field.indexOf(ARRAY_END_SIGN);
        indexArray = Integer.valueOf(field.substring(indexOfStartArraySign, indexOfEndArraySign));
        arrayFieldName = field.substring(0, indexOfStartArraySign - 1);
    }
}
