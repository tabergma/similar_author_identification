package de.hpi.smm.cluster_determination;


public class Cluster {

    private int number;
    private String name;
    private Float[] points;

    public Float[] getPoints() {
        return points;
    }

    public void setPoints(Float[] points) {
        this.points = points;
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
