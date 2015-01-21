package de.hpi.smm.clustering;


import de.hpi.smm.features.Feature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusterLabeling {

    private final static double SIGNIFICANT_BELOW = 66.0;
    private final static double SIGNIFICANT_OVER = 150.0;

    List<ClusterCentroid> centroids;
    private Map<Integer, List<Integer>> cluster2document = new HashMap<Integer, List<Integer>>();
    Map<Integer, Feature> index2Feature;

    public ClusterLabeling(List<ClusterCentroid> centers, Map<Integer, List<Integer>> cluster2document, Map<Integer, Feature> index2FeatureMap) {
        this.centroids = centers;
        this.index2Feature = index2FeatureMap;
        this.cluster2document = cluster2document;
    }

    public List<ClusterCentroid> labelClusters() {
        //method1();
        //method2();
    	labelingMethod3();
    	
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

                if (isSignificantOverAvg(distance) && !cluster.getLabels().contains(feature.maxName)) {
                    cluster.addLabel(feature.highName);
                } else if (isSignificantBelowAvg(distance) && !cluster.getLabels().contains(feature.minName)) {
                    cluster.addLabel(feature.lowName);
                }
            }
        }
    }
    
    private void labelingMethod3() {
    	int numberOfDataPoints = getNumberOfDataPoints();
        int numberOfFeatures = centroids.get(0).getValues().size();
        
        double[] averagePoint = new double[numberOfFeatures];
        double[] maxPoint = new double[numberOfFeatures];
        double[] minPoint = new double[numberOfFeatures];

        for (int i = 0; i < numberOfFeatures; i++) {
            double sum = 0.0;
            double max = Double.NEGATIVE_INFINITY;
            double min = Double.POSITIVE_INFINITY;

            for (ClusterCentroid cluster : centroids) {
            	double value = cluster.getValues().get(i);
                int numberOfDocs = cluster2document.get(cluster.getId()).size();
                sum += value * numberOfDocs;
                if (value > max) max = value;
                if (value < min) min = value;
            }

            averagePoint[i] = sum / numberOfDataPoints;
            maxPoint[i] = max;
            minPoint[i] = min;
        }
        
        for (Feature feature : index2Feature.values()) {
            int range = feature.end - feature.start;

            // average value
            double avgAvgValue = 0.0;
            double avgMaxValue = 0.0;
            double avgMinValue = 0.0;
            for (int i = feature.start; i < feature.end; i++) {
                avgAvgValue += averagePoint[i];
                avgMaxValue += maxPoint[i];
                avgMinValue += minPoint[i];
                
            }
            avgAvgValue /= range;
            avgMaxValue /= range;
            avgMinValue /= range;

            for (ClusterCentroid cluster : centroids) {
                // cluster value
                double clusterValue = 0.0;
                for (int i = feature.start; i < feature.end; i++) {
                    clusterValue += cluster.getValues().get(i);
                }
                clusterValue = clusterValue / range;

                double significance = 0.0;
                
                if (clusterValue >= avgAvgValue){
                	significance += (clusterValue - avgAvgValue) / (avgMaxValue - avgAvgValue);
                }
                else {
                	significance -= (avgAvgValue - clusterValue) / (avgAvgValue - avgMinValue);
                }
                
                if (significance <= -0.99)
                	cluster.addLabel(feature.minName, significance);
                else if (significance < -0.7)
                	cluster.addLabel(feature.veryLowName, significance);
                else if (significance < -0.3)
                	cluster.addLabel(feature.lowName, significance);
                else if (significance < 0.3)
                	cluster.addLabel(feature.averageName, significance);
                else if (significance < 0.7)
                	cluster.addLabel(feature.highName, significance);
                else if (significance < 0.99)
                	cluster.addLabel(feature.veryHighName, significance);
                else
                	cluster.addLabel(feature.maxName, significance);
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
        return number;
    }
}
