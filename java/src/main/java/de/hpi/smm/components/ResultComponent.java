package de.hpi.smm.components;


import de.hpi.smm.clustering.ClusterAnalyzer;
import de.hpi.smm.clustering.DatabaseResultHandler;

public class ResultComponent {

    public static void main(String[] args) throws Exception {
        run();
    }

    /**
     * Read mahout cluster files from disk,
     * extract the results and
     * write them into the database.
     */
    public static void run() throws Exception {
        String clusterFile = "";
        String clusterCenterFile = "";

        ClusterAnalyzer analyzer = new ClusterAnalyzer(clusterFile, clusterCenterFile);
        DatabaseResultHandler resultHandler = new DatabaseResultHandler();

        analyzer.analyzeMahout(resultHandler);
    }

}
