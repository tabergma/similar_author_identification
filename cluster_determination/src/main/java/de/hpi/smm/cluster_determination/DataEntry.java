package de.hpi.smm.cluster_determination;

public class DataEntry {

    private int number;
    private Float[] point;

    public DataEntry(int number, Float[] point) {
        this.number = number;
        this.point = point;
    }

    public Float[] getPoint() {
        return point;
    }

    public void setPoint(Float[] point) {
        this.point = point;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

}
