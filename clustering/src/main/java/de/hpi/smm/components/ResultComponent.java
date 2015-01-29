package de.hpi.smm.components;


import de.hpi.smm.Config;
import de.hpi.smm.clustering.ClusterAnalyzer;
import de.hpi.smm.clustering.DatabaseResultHandler;

public class ResultComponent {

    // TODO: location of mahout files
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Wrong number of arguments!");
            System.out.println("To start the program execute");
            System.out.println("  java -jar <jar-name> <data-set-id>");
            System.out.println("data-set-id: 1 -> smm data, 2 -> springer data ");
            return;
        }

        run(Integer.parseInt(args[0]));
    }

    /**
     * Read mahout cluster files from disk,
     * extract the results and
     * write them into the database.
     */
    public static void run(int dataSetId) throws Exception {
        String clusterFile = Config.CLUSTER_FILE;
        String clusterCenterFile = Config.CLUSTER_CENTER_FILE;

        ClusterAnalyzer analyzer = new ClusterAnalyzer(clusterFile, clusterCenterFile);
        DatabaseResultHandler resultHandler = new DatabaseResultHandler(dataSetId);

        analyzer.analyzeMahout(resultHandler);
    }

}
