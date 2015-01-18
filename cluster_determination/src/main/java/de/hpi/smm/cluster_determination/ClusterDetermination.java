package de.hpi.smm.cluster_determination;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ClusterDetermination {

    List<Cluster> clusters = new ArrayList<>();
    List<DataEntry> dataEntries = new ArrayList<>();

    public ClusterDetermination() throws IOException {
        this.clusters = de.hpi.smm.helper.FileReader.readClusterFile();
        this.dataEntries = de.hpi.smm.helper.FileReader.readBlogPostFile();
    }

    public Cluster determineCluster(List<Float> features) {
        Float[] blogPostPoint = features.toArray(new Float[features.size()]);

//        int index = EuclideanDistance.getNearestCluster(clusters, blogPostPoint);

        KNearestNeighbour kNearestNeighbour = new KNearestNeighbour();
        int index = kNearestNeighbour.getNearestCluster(dataEntries, blogPostPoint, 6);

        return clusters.get(index);
    }

}
