package de.hpi.smm.cluster_determination;


import java.util.List;

public class EuclideanDistance {

    public static int getNearestCluster(List<Cluster> clusters, Float[] blogPostPoint) {
        double[] distances = new double[clusters.size()];
        for (int i = 0; i < clusters.size(); i++) {
            distances[i] = getEuclideanDistance(clusters.get(i).getPoint(), blogPostPoint);
        }

        return findClusterWithSmallestDistance(distances);
    }

    private static double getEuclideanDistance(Float[] pointA, Float[] pointB) {
        double sum = 0.0;
        for(int i = 0; i < pointA.length; i++) {
            sum = sum + Math.pow((pointA[i] - pointB[i]), 2.0);
        }
        return Math.sqrt(sum);
    }

    private static int findClusterWithSmallestDistance(double[] distances) {
        double smallest = Integer.MAX_VALUE;
        int index = 0;

        for(int i = 0; i < distances.length; i++) {
            if(smallest > distances[i]) {
                smallest = distances[i];
                index = i;
            }
        }

        return index;
    }

}
