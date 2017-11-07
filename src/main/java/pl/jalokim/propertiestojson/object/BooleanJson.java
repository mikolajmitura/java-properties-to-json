package pl.jalokim.propertiestojson.object;

public class BooleanJson extends AbstractJsonType {

    private Boolean booleanValue;

    public BooleanJson(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    @Override
    public String toStringJson() {
        return booleanValue.toString();
    }

}
