package pl.jalokim.propertiestojson.domain;

public class MainComplexObject {
    private ComplexObject complexObject;
    private String simpleText;

    public ComplexObject getComplexObject() {
        return complexObject;
    }

    public void setComplexObject(ComplexObject complexObject) {
        this.complexObject = complexObject;
    }

    public String getSimpleText() {
        return simpleText;
    }

    public void setSimpleText(String simpleText) {
        this.simpleText = simpleText;
    }
}
