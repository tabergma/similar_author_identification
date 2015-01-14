package de.hpi.smm.clustering;

public class BlogPost {

    int clusterNumber;
    int documentId;
    double[] point;

    public BlogPost(int clusterNumber, int documentId, double[] point) {
        this.clusterNumber = clusterNumber;
        this.point = point;
        this.documentId = documentId;
    }

    public int getClusterNumber() {
        return clusterNumber;
    }

    public void setClusterNumber(int clusterNumber) {
        this.clusterNumber = clusterNumber;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public double[] getPoint() {
        return point;
    }

    public void setPoint(double[] point) {
        this.point = point;
    }
}
