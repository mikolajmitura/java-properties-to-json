package pl.jalokim.propertiestojson.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainComplexObject {
    private ComplexObject complexObject;
    private String simpleText;
}
