package de.hpi.smm.clustering;


import de.hpi.smm.features.Feature;

import java.util.List;
import java.util.Map;

public class ClusterLabeling {

    List<ClusterCentroid> centroids;
    Map<Integer, Feature> index2Feature;

    public ClusterLabeling(List<ClusterCentroid> centers, Map<Integer, Feature> index2FeatureMap) {
        this.centroids = centers;
        this.index2Feature = index2FeatureMap;
    }

    public List<ClusterCentroid> labelClusters() {
        for (Feature feature : index2Feature.values()) {
            double min = 1.0;
            double max = 0.0;

            ClusterCentroid minCluster = null;
            ClusterCentroid maxCluster = null;

            int range = feature.end - feature.start;

            for (ClusterCentroid cluster : centroids) {
                double sum = 0.0;
                for (int i = feature.start; i < feature.end; i++) {
                    sum += cluster.getValues().get(i);
                }
                double avg = sum / range;

                if (avg > max) {
                    max = avg;
                    maxCluster = cluster;
                }
                if (avg < min) {
                    min = avg;
                    minCluster = cluster;
                }
            }

            if (maxCluster != null) maxCluster.addLabel(feature.maxName);
            if (minCluster != null) minCluster.addLabel(feature.minName);
        }

        return this.centroids;
    }
}
