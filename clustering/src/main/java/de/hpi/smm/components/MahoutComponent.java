package de.hpi.smm.components;


import de.hpi.smm.clustering.KMeans;
import de.hpi.smm.database.AbstractTableDefinition;
import de.hpi.smm.database.DatabaseAdapter;
import de.hpi.smm.database.SchemaConfig;

import java.util.ArrayList;
import java.util.List;

public class MahoutComponent {

    // TODO check file access!!!
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Wrong number of arguments!");
            System.out.println("-----------------------------------------------");
            System.out.println("To start the program execute");
            System.out.println("  java -jar mahout_component.jar <data-set-id> <k> <max-iterations>");
            System.out.println("-----------------------------------------------");
            System.out.println("data-set-id:");
            System.out.println("  1 -> smm data");
            System.out.println("  2 -> springer data");
            System.out.println("k:              number of resulting clusters");
            System.out.println("max-iterations: max iterations for the k-means algorithm");
            return;
        }

        int dataSetId = Integer.parseInt(args[0]);
        int k = Integer.parseInt(args[1]);
        int maxIterations = Integer.parseInt(args[2]);

        run(dataSetId, k, maxIterations);
    }

    /**
     * Read the features from the database and
     * run the K-Means algorithm using Apache Mahout.
     */
    public static void run(int dataSetId, int k, int maxIterations) throws Exception {
        System.out.print("Reading features ... ");
        List<List<Float>> features = read(dataSetId);
        System.out.println("Done.");

        System.out.print("Performing K-Means ... ");
        KMeans kMeans = new KMeans(k, maxIterations);
        kMeans.run(features);
        System.out.println("Done.");

        System.out.println("Finished.");
    }


    private static List<List<Float>> read(int dataSetId) {
        List<List<Float>> allFeatures = new ArrayList<>();

        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        databaseAdapter.setSchema(SchemaConfig.getWholeSchema(dataSetId));

        AbstractTableDefinition table = databaseAdapter.getReadTable(SchemaConfig.getFeatureTableName());
        while(table.next()) {
            List<Float> features = new ArrayList<>();
            int i = 0;
            while (table.getFeatureValue(i) != -1) {
                features.add((float) table.getFeatureValue(i));
                i++;
            }
            allFeatures.add(features);
        }

        databaseAdapter.closeConnection();

        return allFeatures;
    }

}
