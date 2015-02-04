package de.hpi.smm.components;


import de.hpi.smm.Config;
import de.hpi.smm.clustering.ClusterAnalyzer;
import de.hpi.smm.clustering.DatabaseResultHandler;

public class ResultComponent {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Wrong number of arguments!");
            System.out.println("-----------------------------------------------");
            System.out.println("To start the program execute");
            System.out.println("  java -cp similar_author_identification.jar de.hpi.smm.components.ResultComponent <run-id>");
            System.out.println("-----------------------------------------------");
            System.out.println("run-id: this id distinguish between different runs");
            return;
        }

        run(Integer.parseInt(args[0]));
    }

    /**
     * Read mahout cluster files from disk,
     * extract the results and
     * write them into the database.
     *
     * @param runId     distinguish between different runs
     */
    public static void run(int runId) throws Exception {
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
