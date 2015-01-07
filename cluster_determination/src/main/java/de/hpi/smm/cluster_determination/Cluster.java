package de.hpi.smm.cluster_determination;


public class Cluster {

    private int number;
    private String name;
    private Float[] point;

    public Float[] getPoint() {
        return point;
    }

    public void setPoint(Float[] point) {
        this.point = point;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
