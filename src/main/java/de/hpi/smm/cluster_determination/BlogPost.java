package de.hpi.smm.cluster_determination;

public class BlogPost {

    private int number;
    private String documentId;
    private Float[] point;

    public BlogPost() {
    }

    public BlogPost(int number, Float[] point) {
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

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public int getSetValues() {
        int v = 0;
        for (Float p : point) {
            if (p != 0.0) v++;
        }
        return v;
    }
}
