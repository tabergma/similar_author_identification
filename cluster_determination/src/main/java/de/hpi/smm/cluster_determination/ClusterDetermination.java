package de.hpi.smm.cluster_determination;


import de.hpi.smm.Config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ClusterDetermination {

    List<Cluster> clusters = new ArrayList<>();

    public ClusterDetermination() throws IOException {
        readClusterFile();
    }

    public Cluster determineCluster(List<Float> features) {
        Float[] blogPostPoint = features.toArray(new Float[features.size()]);

        double[] distances = new double[clusters.size()];
        for (int i = 0; i < clusters.size(); i++) {
            distances[i] = getEuclideanDistance(clusters.get(i).getPoint(), blogPostPoint);
        }

        int index = findClusterWithSmallestDistance(distances);
        return clusters.get(index);
    }

    private double getEuclideanDistance(Float[] pointA, Float[] pointB) {
        double sum = 0.0;
        for(int i = 0; i < pointA.length; i++) {
            sum = sum + Math.pow((pointA[i] - pointB[i]), 2.0);
        }
        return Math.sqrt(sum);
    }

    private int findClusterWithSmallestDistance(double[] distances) {
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

    public void readClusterFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(Config.CLUSTER_FILE));

        String line = "";
        while((line = br.readLine()) != null){
            String[] l = line.split(",");

            Cluster cluster = new Cluster();
            cluster.setNumber(Integer.parseInt(l[0]));
            cluster.setName(l[1]);

            String[] labelsStr = l[2].split(";");
            List<String> labels = new ArrayList<>();
            for (String label : labelsStr) {
                labels.add(label);
            }
            cluster.setLabels(labels);

            Float[] points = new Float[l.length - 3];
            for (int i = 3; i < l.length; i++) {
                points[i - 3] = Float.valueOf(l[i]);
            }
            cluster.setPoint(points);

            clusters.add(cluster);
        }
        br.close();
    }
}
