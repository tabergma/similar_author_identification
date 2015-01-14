package de.hpi.smm.cluster_determination;


import de.hpi.smm.Config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ClusterDetermination {

    List<Cluster> clusters = new ArrayList<>();
    List<DataEntry> dataEntries = new ArrayList<>();

    public ClusterDetermination() throws IOException {
        readClusterFile();
        readBlogPostFile();
    }

    public Cluster determineCluster(List<Float> features) {
        Float[] blogPostPoint = features.toArray(new Float[features.size()]);

//        int index = EuclideanDistance.getNearestCluster(clusters, blogPostPoint);

        KNearestNeighbour kNearestNeighbour = new KNearestNeighbour();
        int index = kNearestNeighbour.getNearestCluster(dataEntries, clusters, blogPostPoint, 6);

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
            Collections.addAll(labels, labelsStr);
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

    public void readBlogPostFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(Config.BLOG_POST_FILE));

        String line = "";
        while((line = br.readLine()) != null){
            String[] l = line.split(",");

            DataEntry dataEntry = new DataEntry();
            dataEntry.setNumber(Integer.parseInt(l[0]));

            Float[] points = new Float[l.length - 2];
            for (int i = 2; i < l.length; i++) {
                points[i - 2] = Float.valueOf(l[i]);
            }
            dataEntry.setPoint(points);

            dataEntries.add(dataEntry);
        }
        br.close();
    }
}
