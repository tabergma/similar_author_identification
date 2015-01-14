package de.hpi.smm.cluster_determination;

import java.util.HashMap;
import java.util.List;

/**
 * @author Andreas Thiele
 *
 * An implementation of knn.
 * Uses Euclidean distance weighted by 1/distance
 */
public class KNearestNeighbour{

    private int k;
    private List<DataEntry> dataSet;
    
    public int getNearestCluster(List<DataEntry> documents, List<Cluster> clusters, Float[] point, int k) {
        this.k = k;
        this.dataSet = documents;
        // add cluster points to data set
        for (Cluster cluster : clusters) {
            this.dataSet.add(new DataEntry(cluster.getNumber(), cluster.getPoint()));
        }
        
        // classify new blog post
        return classify(point);
    }

    private void initialize(List<DataEntry> documents, List<Cluster> clusters) {

    }

    private Integer classify(Float[] point){
        HashMap<Integer, Double> classCount = new HashMap<Integer, Double>();
        DataEntry[] kNearest = this.getKNearestNeighbourType(point);

        for (DataEntry aKNearest : kNearest) {
            double distance = convertDistance(distance(aKNearest.getPoint(), point));
            if (!classCount.containsKey(aKNearest.getNumber())) {
                classCount.put(aKNearest.getNumber(), distance);
            } else {
                classCount.put(aKNearest.getNumber(), classCount.get(aKNearest.getNumber()) + distance);
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

    private DataEntry[] getKNearestNeighbourType(Float[] point){
        DataEntry[] kNearest = new DataEntry[this.k];
        double minDistance = Double.MIN_VALUE;
        int index = 0;

        for (DataEntry dataEntry : this.dataSet) {
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

    private double distance(Float[] a, Float[] b){
        double distance = 0.0;
        int length = a.length;

        for(int i = 0; i < length; i++){
            double t = a[i] - b[i];
            distance = distance + t * t;
        }

        return Math.sqrt(distance);
    }


}
