package de.hpi.smm.evaluation.cluster_determination;

import de.hpi.smm.clustering.BlogPost;
import de.hpi.smm.clustering.ClusterCentroid;

import java.util.HashMap;
import java.util.List;

/**
 * @author Andreas Thiele
 *
 * An implementation of knn.
 * Uses Euclidean distance weighted by 1/distance
 */
public class KNearestNeighbor {

    private int k;
    private List<BlogPost> dataSet;

    public int getNearestCluster(List<BlogPost> documents, List<ClusterCentroid> clusters, Double[] point, int k) {
        this.k = k;
        this.dataSet = documents;
        // add cluster points to data set
        for (ClusterCentroid cluster : clusters) {
            this.dataSet.add(new BlogPost(cluster.getId(), cluster.getValues().toArray(new
                    Double[cluster.getValues().size()])));
        }

        // classify new blog post
        return classify(point);
    }

    private void initialize(List<BlogPost> documents, List<ClusterCentroid> clusters) {

    }

    private Integer classify(Double[] point){
        HashMap<Integer, Double> classCount = new HashMap<Integer, Double>();
        BlogPost[] kNearest = this.getKNearestNeighbourType(point);

        for (BlogPost aKNearest : kNearest) {
            double distance = convertDistance(distance(aKNearest.getPoint(), point));
            if (!classCount.containsKey(aKNearest.getClusterNumber())) {
                classCount.put(aKNearest.getClusterNumber(), distance);
            } else {
                classCount.put(aKNearest.getClusterNumber(), classCount.get(aKNearest.getClusterNumber()) + distance);
            }
        }

        //Find right choice
        Integer nearestCluster = null;
        double max = 0;
        for(Integer clusterNumber : classCount.keySet()){
            if(classCount.get(clusterNumber) > max){
                max = classCount.get(clusterNumber);
                nearestCluster = clusterNumber;
            }
        }

        return nearestCluster;
    }

    private BlogPost[] getKNearestNeighbourType(Double[] point){
        BlogPost[] kNearest = new BlogPost[this.k];
        double minDistance = Double.MIN_VALUE;
        int index = 0;

        for (BlogPost dataEntry : this.dataSet) {
            double distance = distance(point, dataEntry.getPoint());
            if (kNearest[kNearest.length - 1] == null) {
                int j = 0;
                while (j < kNearest.length) {
                    if (kNearest[j] == null) {
                        kNearest[j] = dataEntry;
                        break;
                    }
                    j++;
                }
                if (distance > minDistance) {
                    index = j;
                    minDistance = distance;
                }
            } else {
                if (distance < minDistance) {
                    kNearest[index] = dataEntry;
                    double f = 0.0;
                    int i = 0;
                    for(int j = 0; j < kNearest.length; j++){
                        double d = distance(kNearest[j].getPoint(), point);
                        if(d > f){
                            f = d;
                            i = j;
                        }
                    }
                    minDistance = f;
                    index = i;
                }
            }
        }
        return kNearest;
    }

    private double convertDistance(double d){
        return 1.0 / d;
    }

    private double distance(Double[] a, Double[] b){
        double distance = 0.0;
        int length = a.length;

        for(int i = 0; i < length; i++){
            double t = a[i] - b[i];
            distance = distance + t * t;
        }

        return Math.sqrt(distance);
    }


}
