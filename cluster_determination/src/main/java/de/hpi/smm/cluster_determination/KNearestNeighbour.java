package de.hpi.smm.cluster_determination;

import java.util.HashMap;
import java.util.List;

/**
 * @author Andreas Thiele
 *
 * An implementation of knn.
 * Uses Euclidean distance weighted by 1/distance
 */
public class KNearestNeighbour {
    
    public static int getNearestCluster(List<DataEntry> documents, Float[] point, int k) {
        HashMap<Integer, Double> classCount = new HashMap<Integer, Double>();
        DataEntry[] kNearest = getKNearestNeighbourType(documents, point, k);

        for (DataEntry aKNearest : kNearest) {
            double distance = convertDistance(distance(aKNearest.getPoint(), point));
            if (!classCount.containsKey(aKNearest.getNumber())) {
                classCount.put(aKNearest.getNumber(), distance);
            } else {
                classCount.put(aKNearest.getNumber(), classCount.get(aKNearest.getNumber()) + distance);
            }
        }

        //Find right choice
        Integer nearestCluster = -1;
        double max = 0;
        for(Integer clusterNumber : classCount.keySet()){
            if(classCount.get(clusterNumber) > max){
                max = classCount.get(clusterNumber);
                nearestCluster = clusterNumber;
            }
        }

        return nearestCluster;
    }

    private static DataEntry[] getKNearestNeighbourType(List<DataEntry> dataSet, Float[] point, int k){
        DataEntry[] kNearest = new DataEntry[k];
        double minDistance = Double.MIN_VALUE;
        int index = 0;

        for (DataEntry dataEntry : dataSet) {
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

    private static double convertDistance(double d){
        return 1.0 / d;
    }

    private static double distance(Float[] a, Float[] b){
        double distance = 0.0;
        int length = a.length;

        for(int i = 0; i < length; i++){
            double t = a[i] - b[i];
            distance = distance + t * t;
        }

        return Math.sqrt(distance);
    }


}
