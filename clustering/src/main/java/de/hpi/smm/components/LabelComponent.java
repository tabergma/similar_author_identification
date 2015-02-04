package de.hpi.smm.components;


import com.google.common.base.Joiner;
import de.hpi.smm.clustering.ClusterCentroid;
import de.hpi.smm.clustering.ClusterLabeling;
import de.hpi.smm.database.AbstractTableDefinition;
import de.hpi.smm.database.DatabaseAdapter;
import de.hpi.smm.database.SchemaConfig;
import de.hpi.smm.features.FeatureExtractor;

import java.util.ArrayList;
import java.util.List;

public class LabelComponent {

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Wrong number of arguments!");
            System.out.println("-----------------------------------------------");
            System.out.println("To start the program execute");
            System.out.println("  java -cp similar_author_identification.jar de.hpi.smm.components.LabelComponent <run-id> <language> <label-count>");
            System.out.println("-----------------------------------------------");
            System.out.println("run-id:      this id distinguish between different runs");
            System.out.println("label-count: number of labels for a cluster");
            System.out.println("language: language of the blog posts, can be 'de' or 'en'");
            return;
        }

        int runId = Integer.parseInt(args[0]);
        String language = args[1];
        int labelCount = Integer.parseInt(args[2]);

        run(runId, labelCount, language);
    }

    /**
     * Get cluster centroids from the database,
     * calculate labels and
     * write them into the database
     *
     * @param runId      distinguish between different runs
     * @param labelCount number of labels for a cluster
     * @param language   the language of the blog posts, can be either 'de' or 'en'
     */
    public static void run(int runId, int labelCount, String language) throws Exception {
        System.out.print("Reading cluster centroids ... ");
        List<ClusterCentroid> clusters = read(runId);
        System.out.println("Done.");

        System.out.print("Calculate labels ... ");
        ClusterLabeling labeling = new ClusterLabeling(clusters, FeatureExtractor.getIndexToFeatureMap(language));
        List<ClusterCentroid> clusterCentroids = labeling.labelClusters();
        System.out.println("Done.");

        System.out.print("Writing cluster labels ... ");
        write(clusterCentroids, runId, labelCount);
        System.out.println("Done.");

        System.out.println("Finished.");
    }

    private static List<ClusterCentroid> read(int runId) {
        List<ClusterCentroid> clusters = new ArrayList<>();

        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        databaseAdapter.setSchema(SchemaConfig.getSchemaForClusterAccess(runId));
        
        AbstractTableDefinition table = databaseAdapter.getReadTable(SchemaConfig.getClusterTableName());
        while(table.next()) {
            int id = table.getInt(SchemaConfig.CLUSTER_ID);
            int nrOfDocuments = table.getInt(SchemaConfig.NUMBER_OF_DOCUMENTS);
            List<Double> features = new ArrayList<>();
            int i = 0;
            while (table.getFeatureValue(i) != -1) {
                features.add(table.getFeatureValue(i));
                i++;
            }
            clusters.add(new ClusterCentroid(id, "cluster" + id, nrOfDocuments, features));
        }

        databaseAdapter.closeConnection();

        return clusters;
    }

    private static void write(List<ClusterCentroid> clusters, int runId, int labelCount) {
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        databaseAdapter.setSchema(SchemaConfig.getSchemaForClusterAccess(runId));
        AbstractTableDefinition table = databaseAdapter.getWriteTable(SchemaConfig.getLabelTableName());

        for (ClusterCentroid cluster : clusters) {
            table.setRecordValuesToNull();
            table.setValue(SchemaConfig.CLUSTER_ID, cluster.getId());
            table.setValue(SchemaConfig.RUN_ID, runId);

            List<String> labels = cluster.getMostSignificantLabels(labelCount);
            String label = Joiner.on("; ").join(labels);

            table.setValue(SchemaConfig.LABELS, label);
            table.writeRecord();
        }

        databaseAdapter.closeConnection();
    }

}
