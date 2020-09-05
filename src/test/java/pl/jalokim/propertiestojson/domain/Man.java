package pl.jalokim.propertiestojson.domain;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Man {

    private String name;
    private String surname;
    private Address address;
    private List<String> emails;
    private List<String> children;
    private List<Group> groups;
    private List<String> hoobies;
    private Insurance insurance;
    private Boolean married;
}
