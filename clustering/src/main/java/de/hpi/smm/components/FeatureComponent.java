package de.hpi.smm.components;


import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import de.hpi.smm.Config;
import de.hpi.smm.database.*;
import de.hpi.smm.features.FeatureExtractor;

import java.util.List;

public class FeatureComponent {

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
     * Get all documents where the features were not yet calculated,
     * calculate the features and
     * write them into the database.
     */
    public static void run(int dataSetId) throws LangDetectException {
        FeatureExtractor featureExtractor = new FeatureExtractor();
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();

        // Create language detector
        DetectorFactory.loadProfile(Config.PROFILES_DIR);
        Detector detector = DetectorFactory.create();

        // Load table
        AbstractTableDefinition table = getTable(databaseAdapter, dataSetId);

        // read content and extract features
        while(table.next()) {
            int id = table.getInt(SchemaConfig.DOCUMENT_ID);
            String content = table.getString(SchemaConfig.DOCUMENT_CONTENT);

            if (content != null) {
                // detect language
                detector.append(content);
                String lang = detector.detect();

                // extract features
                List<Float> features = featureExtractor.getFeatures(content, lang);

                // write features
                write(databaseAdapter, features, 1, 1);
            }
        }

        // Close database connection
        databaseAdapter.closeConnection();
    }

    private static AbstractTableDefinition getTable(DatabaseAdapter databaseAdapter, int dataSetId) {
        // TODO
        Schema schema = SchemaConfig.getSchema();
        databaseAdapter.setSchema(schema);

        AbstractTableDefinition featureTableDefinition = schema.getTableDefinition(SchemaConfig.getFeatureTableName());
        AbstractTableDefinition documentTableDefinition = schema.getTableDefinition(SchemaConfig.getDocumentTableName());

        AbstractTableDefinition joinedTableDefinition = new JoinedTableDefinition(featureTableDefinition, documentTableDefinition, SchemaConfig.DOCUMENT_ID);

        return databaseAdapter.getReadTable(joinedTableDefinition);
    }

    private static void write(DatabaseAdapter databaseAdapter, List<Float> features, int documentId, int dataSetId) {
        AbstractTableDefinition featureTableDefinition = databaseAdapter.getWriteTable(SchemaConfig.getFeatureTableName());

        featureTableDefinition.setRecordValuesToNull(); // this sets all values to NULL, so no value is left unattended
        featureTableDefinition.setValue(SchemaConfig.DATA_SET, dataSetId);
        featureTableDefinition.setValue(SchemaConfig.DOCUMENT_ID, documentId);
        for (int i = 0; i < features.size(); i++) {
            featureTableDefinition.setFeatureValue(i, features.get(i));
        }
        featureTableDefinition.writeRecord();

        databaseAdapter.closeConnection();
    }

}
