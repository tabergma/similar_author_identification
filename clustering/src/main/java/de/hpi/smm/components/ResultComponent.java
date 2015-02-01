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
            System.out.println("  java -jar result_component.jar <data-set-id> <run-id>");
            System.out.println("-----------------------------------------------");
            System.out.println("data-set-id:");
            System.out.println("  1 -> smm data");
            System.out.println("  2 -> springer data");
            System.out.println("run-id: this id distinguish between different runs");
            return;
        }

        run(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }

    /**
     * Read mahout cluster files from disk,
     * extract the results and
     * write them into the database.
     *
     * @param runId     distinguish between different runs
     * @param dataSetId identifies the original data set, 1 for smm data and 2 for springer data
     */
    public static void run(int dataSetId, int runId) throws Exception {
        String clusterFile = Config.CLUSTER_FILE;
        String clusterCenterFile = Config.CLUSTER_CENTER_FILE;

        System.out.print("Preparing ... ");
        ClusterAnalyzer analyzer = new ClusterAnalyzer(clusterFile, clusterCenterFile);
        DatabaseResultHandler resultHandler = new DatabaseResultHandler(dataSetId, runId);
        System.out.println("Done.");

        analyzer.analyzeMahout(resultHandler);
        resultHandler.closeConnection();

        System.out.println("Finished.");
    }

}
