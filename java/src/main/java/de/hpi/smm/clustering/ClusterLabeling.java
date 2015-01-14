package de.hpi.smm.clustering;


import de.hpi.smm.features.Feature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusterLabeling {

    private static double SIGNIFICANT_BELOW = 66.0;
    private static double SIGNIFICANT_OVER = 150.0;

    List<ClusterCentroid> centroids;
    private Map<Integer, List<Integer>> cluster2document = new HashMap<Integer, List<Integer>>();
    Map<Integer, Feature> index2Feature;

    public ClusterLabeling(List<ClusterCentroid> centers, Map<Integer, List<Integer>> cluster2document, Map<Integer, Feature> index2FeatureMap) {
        this.centroids = centers;
        this.index2Feature = index2FeatureMap;
        this.cluster2document = cluster2document;
    }

    public List<ClusterCentroid> labelClusters() {
        method1();
        method2();

        return this.centroids;
    }

    private void method1() {
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
    }

    private void method2() {
        // calculate over all average document
        double[] averageDataPoint = getAverageDataPoint();

        // for each feature and cluster get distance to avg
        // if distance over a certain threshold add label cluster
        for (Feature feature : index2Feature.values()) {
            int range = feature.end - feature.start;

            // average value
            double avgValue = 0.0;
            for (int i = feature.start; i < feature.end; i++) {
                avgValue += averageDataPoint[i];
            }
            avgValue = avgValue / range;

            for (ClusterCentroid cluster : centroids) {
                // cluster value
                double clusterValue = 0.0;
                for (int i = feature.start; i < feature.end; i++) {
                    clusterValue += cluster.getValues().get(i);
                }
                clusterValue = clusterValue / range;

                // get percentage distance
                double distance = getDistance(avgValue, clusterValue);

                if (isSignificantOverAvg(distance)) {
                    cluster.addLabel(feature.highName);
                } else if (isSignificantBelowAvg(distance)) {
                    cluster.addLabel(feature.lowName);
                }
            }
        }
    }

    private boolean isSignificantBelowAvg(double distance) {
        return distance <= SIGNIFICANT_BELOW;
    }

    private boolean isSignificantOverAvg(double distance) {
        return distance >= SIGNIFICANT_OVER;
    }

    private double getDistance(double avgValue, double clusterValue) {
        return 100 / avgValue * clusterValue;
    }

    private double[] getAverageDataPoint() {
        int numberOfDataPoints = getNumberOfDataPoints();
        int numberOfFeatures = centroids.get(0).getValues().size();
        double[] averagePoint = new double[numberOfFeatures];

        for (int i = 0; i < numberOfFeatures; i++) {
            double sum = 0.0;

            for (ClusterCentroid cluster : centroids) {
                int numberOfDocs = cluster2document.get(cluster.getId()).size();
                sum += cluster.getValues().get(i) * numberOfDocs;
            }

            averagePoint[i] = sum / numberOfDataPoints;
        }

        return averagePoint;
    }

    private int getNumberOfDataPoints() {
        int number = 0;
        for (List<Integer> docs : cluster2document.values()) {
            number += docs.size();
        }
        return number + centroids.size();
    }
}
