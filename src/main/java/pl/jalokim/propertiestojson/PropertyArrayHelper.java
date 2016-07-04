package pl.jalokim.propertiestojson;

public class PropertyArrayHelper {

    private int indexArray;
    private String arrayfieldName;

    public PropertyArrayHelper(String field){
        int indexOfStartArraySign = field.indexOf("[")+1;
        int indexofEndArraySign = field.indexOf("]");
        indexArray = Integer.valueOf(field.substring(indexOfStartArraySign, indexofEndArraySign));
        arrayfieldName = field.substring(0, indexOfStartArraySign-1);
    }

    public int getIndexArray() {
        return indexArray;
    }

    public String getArrayfieldName() {
        return arrayfieldName;
    }
}
