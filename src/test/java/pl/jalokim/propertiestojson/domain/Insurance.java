package pl.jalokim.propertiestojson.domain;

/**
 * Created by mikolaj on 7/1/16.
 */
public class Insurance {
    String type;
    Integer cost;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }
}
