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

//        int index = EuclideanDistance.getNearestCluster(clusters, blogPostPoint);

        KNearestNeighbour kNearestNeighbour = new KNearestNeighbour();
        int index = kNearestNeighbour.getNearestCluster(new ArrayList<DataEntry>(), clusters, blogPostPoint, 3);

        return clusters.get(index);
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
