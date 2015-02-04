package de.hpi.smm.components;


import de.hpi.smm.cluster_determination.BlogPost;
import de.hpi.smm.cluster_determination.Cluster;
import de.hpi.smm.cluster_determination.ClusterDetermination;
import de.hpi.smm.database.*;
import de.hpi.smm.helper.FileReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClusterComponent {

    public static void main(String[] args) throws Exception {
        if (args.length != 6) {
            System.out.println("Wrong number of arguments!");
            System.out.println("-----------------------------------------------");
            System.out.println("To start the program execute");
            System.out.println("  java -cp similar_author_identification.jar de.hpi.smm.components.ClusterComponent <data-set-id> <run-id> <language> <method> <k> <svm-model-file> <blog-post-file>");
            System.out.println("-----------------------------------------------");
            System.out.println("<data-set-id> and <run-id> have to identical to the ones used for the result component!");
            System.out.println("data-set-id:");
            System.out.println("  1 -> smm data");
            System.out.println("  2 -> springer data");
            System.out.println("run-id:         this id distinguish between different runs");
            System.out.println("language: language of the blog posts, can be 'de' or 'en'");
            System.out.println("method:         can be either 'k-nearest', 'euclidean' or 'svm'");
            System.out.println("k:              parameter for k-nearest-neighbor");
            System.out.println("svm-model-file: file location for svm model file");
            System.out.println("blog-post-file: file, which content should be clustered");
            return;
        }

        int dataSetId = Integer.parseInt(args[0]);
        int runId = Integer.parseInt(args[1]);
        String language = args[2];
        String method = args[3];
        String k = args[4];
        String modelFile = args[5];
        String content = FileReader.readFile(args[6]);

        Cluster cluster = run(runId, dataSetId, content, method, k, modelFile, language);

        System.out.println("Result: Cluster " + cluster.getNumber());
    }

    /**
     * Determine to which cluster a given blog posts belongs using a certain method.
     * @param runId        distinguish between different runs
     * @param dataSetId    identifies the original data set, 1 for smm data and 2 for springer data
     * @param content      the content of the blog post, which should be clustered
     * @param method       the cluster determination method to use, can be either 'k-nearest', 'euclidean' or 'svm'
     * @param k            the number of neighbors the k-nearest-neighbor algorithm uses
     * @param svmModelFile the file location of the svm model file
     * @param language  the language of the blog posts, can be either 'de' or 'en'
     * @return the resulting cluster
     * @throws Exception
     */
    public static Cluster run(int runId, int dataSetId, String content, String method, String k, String svmModelFile, String language) throws Exception {
        System.out.print("Reading blog posts with features and cluster id ... ");
        List<BlogPost> blogPosts = SvmComponent.readBlogPosts(runId, dataSetId, language);
        System.out.println("Done.");
        System.out.print("Reading cluster centroids ... ");
        List<Cluster> clusters = readClusters(runId);
        System.out.println("Done.");

        System.out.print("Determining cluster ... ");
        ClusterDetermination clusterDetermination = new ClusterDetermination(clusters, blogPosts, svmModelFile);
        Cluster resultCluster = clusterDetermination.run(content, method, k, language);
        System.out.println("Done.");

        System.out.println("Finished.");

        return resultCluster;
    }

    private static List<Cluster> readClusters(int runId) {
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        Schema schema = SchemaConfig.getSchemaForClusterAccess(runId);
        databaseAdapter.setSchema(schema);

        AbstractTableDefinition clusterTable = schema.getTableDefinition(SchemaConfig.getClusterTableName());
        AbstractTableDefinition labelTable = schema.getTableDefinition(SchemaConfig.getLabelTableName());

        AbstractTableDefinition table = new JoinedTableDefinition(clusterTable, SchemaConfig.CLUSTER_ID, labelTable, SchemaConfig.CLUSTER_ID);

        table = databaseAdapter.getReadTable(table);

        List<Cluster> clusters = new ArrayList<>();
        while(table.next()) {
            Cluster cluster = new Cluster();
            cluster.setNumber(table.getInt(SchemaConfig.CLUSTER_ID));
            String label = table.getString(SchemaConfig.LABELS);
            cluster.setLabels(Arrays.asList(label.split(";")));
            List<Float> features = new ArrayList<>();
            int i = 0;
            while (table.getFeatureValue(i) != -1) {
                features.add((float) table.getFeatureValue(i));
                i++;
            }
            cluster.setPoint(features.toArray(new Float[features.size()]));
            clusters.add(cluster);
        }

        databaseAdapter.closeConnection();

        return clusters;
    }

}
