package de.hpi.smm.clustering;

import org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable;
import org.apache.mahout.math.Vector;

public class BlogPost {

    int clusterNumber;
    int documentId;
    Double[] point;

    public static BlogPost createFromVector(int clusterId, int documentId, WeightedPropertyVectorWritable value) {
        int featureNr = value.getVector().size();
        Vector vector = value.getVector();
        Double[] point = new Double[featureNr];

        for (int i = 0; i < featureNr; i++) {
            point[i] = vector.get(i);
        }

        return new BlogPost(clusterId, documentId, point);
    }

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
