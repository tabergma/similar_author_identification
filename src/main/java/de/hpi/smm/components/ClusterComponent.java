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
            System.out.println("To start the program execute");
            System.out.println("  java -jar <jar-name> <method> <k> <content> <data-set-id>");
            System.out.println("<method> can be either 'k-nearest', 'euclidean' or 'svm'.");
            return;
        }

        String method = args[0];
        String k = args[1];
        String content = args[2];
        int dataSetId = Integer.parseInt(args[3]);

        Cluster cluster = run(content, method, k, dataSetId);

        System.out.println("Result: Cluster " + cluster.getNumber());
    }

    public static Cluster run(String content, String method, String k, int dataSetId) throws Exception {
        List<BlogPost> blogPosts = readBlogPosts(dataSetId);
        List<Cluster> clusters = readClusters(dataSetId);

        ClusterDetermination clusterDetermination = new ClusterDetermination(clusters, blogPosts);
        return clusterDetermination.run(content, method, k);
    }

    private static List<BlogPost> readBlogPosts(int dataSetId) {
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        Schema schema = SchemaConfig.getSchema();
        databaseAdapter.setSchema(schema);

        AbstractTableDefinition featureTableDefinition = schema.getTableDefinition(SchemaConfig.getFeatureTableName());
        AbstractTableDefinition documentClusterMapping = schema.getTableDefinition(SchemaConfig.getDocumentClusterMappingTableName());

        AbstractTableDefinition table = new JoinedTableDefinition(featureTableDefinition, documentClusterMapping, SchemaConfig.DOCUMENT_ID);

        table = databaseAdapter.getReadTable(table);

        List<BlogPost> blogPosts = new ArrayList<>();
        while(table.next()) {
            BlogPost blogPost = new BlogPost();
            blogPost.setNumber(table.getInt(SchemaConfig.CLUSTER_ID));
            blogPost.setDocumentId(table.getInt(SchemaConfig.DOCUMENT_ID));
            List<Float> features = new ArrayList<>();
            int i = 0;
            while (table.getFeatureValue(i) != -1) {
                features.add((float) table.getFeatureValue(i));
            }
            blogPost.setPoint(features.toArray(new Float[features.size()]));
            blogPosts.add(blogPost);
        }

        databaseAdapter.closeConnection();

        return blogPosts;
    }

    private static List<Cluster> readClusters(int dataSetId) {
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        Schema schema = SchemaConfig.getSchema();
        databaseAdapter.setSchema(schema);

        AbstractTableDefinition clusterTable = schema.getTableDefinition(SchemaConfig.getClusterTableName());
        AbstractTableDefinition labelTable = schema.getTableDefinition(SchemaConfig.getLabelTableName());

        AbstractTableDefinition table = new JoinedTableDefinition(clusterTable, labelTable, SchemaConfig.CLUSTER_ID);

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
            }
            cluster.setPoint(features.toArray(new Float[features.size()]));
            clusters.add(cluster);
        }

        databaseAdapter.closeConnection();

        return clusters;
    }

}
