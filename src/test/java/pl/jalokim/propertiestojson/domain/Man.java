package pl.jalokim.propertiestojson.domain;

import java.util.List;

public class Man {
    private String name;
    private String surname;
    private Address address;
    private List<String> emails;
    private List<String> children;
    private List<Group> groups;
    private List<String> hoobies;
    private Insurance insurance;

    public String getName() {
        return name;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<String> getChildren() {
        return children;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<String> getHoobies() {
        return hoobies;
    }

    public void setHoobies(List<String> hoobies) {
        this.hoobies = hoobies;
    }

    public Insurance getInsurance() {
        return insurance;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }
}
