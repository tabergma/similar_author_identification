package de.hpi.smm.evaluation.cluster_determination;


import de.hpi.smm.clustering.BlogPost;
import de.hpi.smm.clustering.ClusterCentroid;

import java.util.List;

public class Validation {

    public Double validate(List<BlogPost> documents, List<ClusterCentroid> clusters, List<BlogPost> points, int k) {
        KNearestNeighbor kNearestNeighbor = new KNearestNeighbor();

        Double correct = 0.0;
        Double incorrect = 0.0;

        for (BlogPost point: points) {
            int cluster = kNearestNeighbor.getNearestCluster(documents, clusters, point.getPoint(), k);
            if (cluster == point.getClusterNumber()) correct++;
            else incorrect++;
        }

        return correct / (correct + incorrect);
    }

}
