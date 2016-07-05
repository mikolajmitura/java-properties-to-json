package pl.jalokim.propertiestojson.domain;

import java.util.List;

public class Man {
    private String name;
    private String surname;
    private Adress adress;
    private List<String> emails;
    private List<String> children;
    private List<Group> groups;
    private List<String> hoobies;

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

    public Adress getAdress() {
        return adress;
    }

    public void setAdress(Adress adress) {
        this.adress = adress;
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
}
