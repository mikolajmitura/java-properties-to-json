package pl.jalokim.propertiestojson.domain;

public class ComplexObject {
    private Numbers numbers;
    private Booleans booleans;
    private String text;

    public Numbers getNumbers() {
        return numbers;
    }

    public void setNumbers(Numbers numbers) {
        this.numbers = numbers;
    }

    public Booleans getBooleans() {
        return booleans;
    }

    public void setBooleans(Booleans booleans) {
        this.booleans = booleans;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
