package de.hpi.smm.clustering;

public class BlogPost {

    int clusterNumber;
    int documentId;
    Double[] point;

    public BlogPost(int clusterNumber, int documentId, Double[] point) {
        this.clusterNumber = clusterNumber;
        this.point = point;
        this.documentId = documentId;
    }

    public BlogPost(int clusterNumber, Double[] point) {
        this.clusterNumber = clusterNumber;
        this.point = point;
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

    public Double[] getPoint() {
        return point;
    }

    public void setPoint(Double[] point) {
        this.point = point;
    }
}
