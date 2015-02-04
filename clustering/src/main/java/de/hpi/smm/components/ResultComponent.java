package de.hpi.smm.components;


import de.hpi.smm.Config;
import de.hpi.smm.clustering.ClusterAnalyzer;
import de.hpi.smm.clustering.DatabaseResultHandler;

public class ResultComponent {

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Wrong number of arguments!");
            System.out.println("-----------------------------------------------");
            System.out.println("To start the program execute");
            System.out.println("  java -cp similar_author_identification.jar de.hpi.smm.components.ResultComponent <data-set-id> <run-id> <language>");
            System.out.println("-----------------------------------------------");
            System.out.println("data-set-id:");
            System.out.println("  1 -> smm data");
            System.out.println("  2 -> springer data");
            System.out.println("run-id: this id distinguish between different runs");
            System.out.println("language: language of the blog posts, can be 'de' or 'en'");
            return;
        }

        run(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2]);
    }

    /**
     * Read mahout cluster files from disk,
     * extract the results and
     * write them into the database.
     *
     * @param dataSetId identifies the original data set, 1 for smm data and 2 for springer data
     * @param runId     distinguish between different runs
     * @param language  language of the blog posts, can be 'de' or 'en'
     */
    public static void run(int dataSetId, int runId, String language) throws Exception {
        String clusterFile = Config.CLUSTER_FILE;
        String clusterCenterFile = Config.CLUSTER_CENTER_FILE;

        System.out.print("Preparing ... ");
        ClusterAnalyzer analyzer = new ClusterAnalyzer(clusterFile, clusterCenterFile);
        DatabaseResultHandler resultHandler = new DatabaseResultHandler(dataSetId, runId, language);
        System.out.println("Done.");

        analyzer.analyzeMahout(resultHandler);
        resultHandler.closeConnection();

        System.out.println("Finished.");
    }

}
