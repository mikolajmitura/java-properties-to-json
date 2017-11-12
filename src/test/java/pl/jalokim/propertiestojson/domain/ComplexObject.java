package pl.jalokim.propertiestojson.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplexObject {
    private Numbers numbers;
    private Booleans booleans;
    private String text;
}
