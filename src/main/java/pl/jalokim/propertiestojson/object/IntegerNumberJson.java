package pl.jalokim.propertiestojson.object;

public class IntegerNumberJson extends AbstractJsonType {

    private Integer number;

    public IntegerNumberJson(Integer number) {
        this.number = number;
    }

    @Override
    public String toStringJson() {
        return number.toString();
    }

}
