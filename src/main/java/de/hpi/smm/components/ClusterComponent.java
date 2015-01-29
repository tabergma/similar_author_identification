package de.hpi.smm.components;


import de.hpi.smm.cluster_determination.BlogPost;
import de.hpi.smm.cluster_determination.Cluster;
import de.hpi.smm.cluster_determination.ClusterDetermination;
import de.hpi.smm.database.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClusterComponent {

    // TODO data set id
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Wrong number of arguments!");
            System.out.println("-----------------------------------------------");
            System.out.println("To start the program execute");
            System.out.println("  java -jar cluster_component.jar <data-set-id> <method> <k> <content>");
            System.out.println("-----------------------------------------------");
            System.out.println("data-set-id:");
            System.out.println("  1 -> smm data");
            System.out.println("  2 -> springer data");
            System.out.println("method: can be either 'k-nearest', 'euclidean' or 'svm'");
            System.out.println("k: parameter for k-nearest-neighbor");
            System.out.println("content: content to cluster");
            return;
        }

        int dataSetId = Integer.parseInt(args[0]);
        String method = args[1];
        String k = args[2];
        String content = args[3];

        Cluster cluster = run(content, method, k, dataSetId);

        System.out.println("Result: Cluster " + cluster.getNumber());
        System.out.print("Finished.");
    }

    public static Cluster run(String content, String method, String k, int dataSetId) throws Exception {
        System.out.print("Reading blog posts with features and cluster id ... ");
        List<BlogPost> blogPosts = readBlogPosts(dataSetId);
        System.out.print("Done.");
        System.out.print("Reading cluster centroids ... ");
        List<Cluster> clusters = readClusters(dataSetId);
        System.out.print("Done.");

        System.out.println("Determining cluster ... ");
        ClusterDetermination clusterDetermination = new ClusterDetermination(clusters, blogPosts);
        return clusterDetermination.run(content, method, k);
    }

    private static List<BlogPost> readBlogPosts(int dataSetId) {
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        Schema schema = SchemaConfig.getWholeSchema(dataSetId);
        databaseAdapter.setSchema(schema);

        AbstractTableDefinition featureTableDefinition = schema.getTableDefinition(SchemaConfig.getFeatureTableName());
        AbstractTableDefinition documentClusterMapping = schema.getTableDefinition(SchemaConfig.getDocumentClusterMappingTableName());

        AbstractTableDefinition table = new JoinedTableDefinition(featureTableDefinition, SchemaConfig.DOCUMENT_ID, documentClusterMapping, SchemaConfig.DOCUMENT_ID);

        table = databaseAdapter.getReadTable(table);

        List<BlogPost> blogPosts = new ArrayList<>();
        while(table.next()) {
            BlogPost blogPost = new BlogPost();
            blogPost.setNumber(table.getInt(SchemaConfig.CLUSTER_ID));
            blogPost.setDocumentId(table.getString(SchemaConfig.DOCUMENT_ID));
            List<Float> features = new ArrayList<>();
            int i = 0;
            while (table.getFeatureValue(i) != -1) {
                features.add((float) table.getFeatureValue(i));
                i++;
            }
            blogPost.setPoint(features.toArray(new Float[features.size()]));
            blogPosts.add(blogPost);
        }

        databaseAdapter.closeConnection();

        return blogPosts;
    }

    private static List<Cluster> readClusters(int dataSetId) {
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        Schema schema = SchemaConfig.getWholeSchema(dataSetId);
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
