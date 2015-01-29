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
        if (args.length != 1) {
            System.out.println("Wrong number of arguments!");
            System.out.println("To start the program execute");
            System.out.println("  java -jar <jar-name> <data-set-id> <label-count>");
            System.out.println("data-set-id: 1 -> smm data, 2 -> springer data ");
            return;
        }

        run(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }

    /**
     * Get cluster centroids from the database,
     * calculate labels and
     * write them into the database
     */
    public static void run(int dataSetId, int labelCount) throws Exception {
        List<ClusterCentroid> clusters = read(dataSetId);

        ClusterLabeling labeling = new ClusterLabeling(clusters, FeatureExtractor.getIndexToFeatureMap());
        List<ClusterCentroid> clusterCentroids = labeling.labelClusters();

        write(clusterCentroids, dataSetId, labelCount);
    }

    private static List<ClusterCentroid> read(int dataSetId) {
        List<ClusterCentroid> clusters = new ArrayList<>();

        // TODO datasetid
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        databaseAdapter.setSchema(SchemaConfig.getSchema());

        AbstractTableDefinition table = databaseAdapter.getReadTable(SchemaConfig.getClusterTableName());
        while(table.next()) {
            int id = table.getInt(SchemaConfig.CLUSTER_ID);
            List<Double> features = new ArrayList<>();
            int i = 0;
            while (table.getFeatureValue(i) != -1) {
                features.add(table.getFeatureValue(i));
            }
            clusters.add(new ClusterCentroid(id, "cluster" + id, features));
        }

        databaseAdapter.closeConnection();

        return clusters;
    }

    private static void write(List<ClusterCentroid> clusters, int dataSetId, int labelCount) {
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        databaseAdapter.setSchema(SchemaConfig.getSchema());
        AbstractTableDefinition table = databaseAdapter.getWriteTable(SchemaConfig.getLabelTableName());

        for (ClusterCentroid cluster : clusters) {
            table.setRecordValuesToNull();
            table.setValue(SchemaConfig.CLUSTER_ID, cluster.getId());
            table.setValue(SchemaConfig.DATA_SET, dataSetId);

            List<String> labels = cluster.getMostSignificantLabels(labelCount);
            String label = Joiner.on("; ").join(labels);

            table.setValue(SchemaConfig.LABELS, label);
            table.writeRecord();
        }

        databaseAdapter.closeConnection();
    }

}
