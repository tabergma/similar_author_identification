package de.hpi.smm.components;


import de.hpi.smm.clustering.KMeans;
import de.hpi.smm.database.AbstractTableDefinition;
import de.hpi.smm.database.DatabaseAdapter;
import de.hpi.smm.database.SchemaConfig;

import java.util.ArrayList;
import java.util.List;

public class MahoutComponent {

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
     * Read the features from the database and
     * run the K-Means algorithm using Apache Mahout.
     */
    public static void run(int dataSetId) throws Exception {
        List<List<Float>> features = read(dataSetId);

        KMeans kMeans = new KMeans();
        kMeans.run(features);
    }


    private static List<List<Float>> read(int dataSetId) {
        List<List<Float>> allFeatures = new ArrayList<>();

        // TODO datasetid
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        databaseAdapter.setSchema(SchemaConfig.getSchema());

        AbstractTableDefinition table = databaseAdapter.getReadTable(SchemaConfig.getFeatureTableName());
        while(table.next()) {
            List<Float> features = new ArrayList<>();
            int i = 0;
            while (table.getFeatureValue(i) != -1) {
                features.add((float) table.getFeatureValue(i));
            }
            allFeatures.add(features);
        }

        databaseAdapter.closeConnection();

        return allFeatures;
    }

}
