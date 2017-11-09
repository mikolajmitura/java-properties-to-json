package pl.jalokim.propertiestojson.object;


public class DoubleNumberJson extends AbstractJsonType {

    private Double number;

    public DoubleNumberJson(Double number) {
        this.number = number;
    }

    @Override
    public String toStringJson() {
        return number.toString();
    }
}
