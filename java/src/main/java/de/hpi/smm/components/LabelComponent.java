package de.hpi.smm.components;


import de.hpi.smm.clustering.ClusterCentroid;
import de.hpi.smm.clustering.ClusterLabeling;
import de.hpi.smm.features.FeatureExtractor;

import java.util.ArrayList;
import java.util.List;

public class LabelComponent {

    public static void main(String[] args) throws Exception {
        run();
    }

    /**
     * Get cluster centroids from the database,
     * calculate labels and
     * write them into the database
     */
    public static void run() throws Exception {
        /**
         * Read cluster centroids from the database
         */
        // TODO
        List<ClusterCentroid> clusters = new ArrayList<>();

        /**
         * Label clusters
         */
        ClusterLabeling labeling = new ClusterLabeling(clusters, FeatureExtractor.getIndexToFeatureMap());
        List<ClusterCentroid> clusterCentroids = labeling.labelClusters();

        /**
         * Write labels into the database
         */
        // TODO
    }

}
