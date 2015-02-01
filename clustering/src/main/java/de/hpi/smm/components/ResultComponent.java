package de.hpi.smm.components;


import de.hpi.smm.Config;
import de.hpi.smm.clustering.ClusterAnalyzer;
import de.hpi.smm.clustering.DatabaseResultHandler;

public class ResultComponent {

    // TODO: location of mahout files
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Wrong number of arguments!");
            System.out.println("-----------------------------------------------");
            System.out.println("To start the program execute");
            System.out.println("  java -jar result_component.jar <run-id> <data-set-id>");
            System.out.println("-----------------------------------------------");
            System.out.println("run-id: this id distinguish between different runs");
            System.out.println("data-set-id:");
            System.out.println("  1 -> smm data");
            System.out.println("  2 -> springer data");
            return;
        }

        run(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }

    /**
     * Read mahout cluster files from disk,
     * extract the results and
     * write them into the database.
     */
    public static void run(int runId, int dataSetId) throws Exception {
        String clusterFile = Config.CLUSTER_FILE;
        String clusterCenterFile = Config.CLUSTER_CENTER_FILE;

        System.out.print("Preparing ... ");
        ClusterAnalyzer analyzer = new ClusterAnalyzer(clusterFile, clusterCenterFile);
        DatabaseResultHandler resultHandler = new DatabaseResultHandler(runId);
        System.out.println("Done.");

        analyzer.analyzeMahout(resultHandler);
        resultHandler.closeConnection();

        System.out.println("Finished.");
    }

}
