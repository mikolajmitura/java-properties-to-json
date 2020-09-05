package pl.jalokim.propertiestojson;

import static pl.jalokim.propertiestojson.Constants.ARRAY_END_SIGN;
import static pl.jalokim.propertiestojson.Constants.ARRAY_START_SIGN;
import static pl.jalokim.propertiestojson.Constants.EMPTY_STRING;
import static pl.jalokim.propertiestojson.Constants.SIMPLE_ARRAY_DELIMITER;
import static pl.jalokim.propertiestojson.path.PathMetadata.INDEXES_PATTERN;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class PropertyArrayHelper {

    private List<Integer> dimensionalIndexes;
    private String arrayFieldName;

    public PropertyArrayHelper(String field) {
        arrayFieldName = getNameFromArray(field);
        dimensionalIndexes = getIndexesFromArrayField(field);
    }

    public static String getNameFromArray(String fieldName) {
        return fieldName.replaceFirst(INDEXES_PATTERN + "$", EMPTY_STRING);
    }

    public static List<Integer> getIndexesFromArrayField(String fieldName) {
        String indexesAsText = fieldName.replace(getNameFromArray(fieldName), EMPTY_STRING);
        String[] indexesAsTextArray = indexesAsText
            .replace(ARRAY_START_SIGN, EMPTY_STRING)
            .replace(ARRAY_END_SIGN, SIMPLE_ARRAY_DELIMITER)
            .replaceAll("\\s", EMPTY_STRING)
            .split(SIMPLE_ARRAY_DELIMITER);
        List<Integer> indexes = new ArrayList<>();
        for (String indexAsText : indexesAsTextArray) {
            indexes.add(Integer.valueOf(indexAsText));
        }
        return indexes;
    }
}
