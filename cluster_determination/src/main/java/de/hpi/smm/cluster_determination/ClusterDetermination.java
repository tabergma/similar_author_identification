package de.hpi.smm.cluster_determination;


import de.hpi.smm.Config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClusterDetermination {

    List<Cluster> clusters = new ArrayList<>();

    public Cluster determineCluster(List<Float> features) throws IOException {
        readClusterFile();

        // TODO find nearest cluster

        return clusters.get(0);
    }

    public void readClusterFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(Config.CLUSTER_FILE));

        String line = "";
        while((line = br.readLine()) != null){
            String[] l = line.split(",");

            Cluster cluster = new Cluster();
            cluster.setNumber(Integer.parseInt(l[0]));
            cluster.setName(l[1]);

            Float[] points = new Float[l.length - 2];
            for (int i = 2; i < l.length; i++) {
                points[i - 2] = Float.valueOf(l[i]);
            }

            cluster.setPoints(points);

            clusters.add(cluster);
        }
        br.close();
    }
}
