package de.hpi.smm.components;


import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import de.hpi.smm.Config;
import de.hpi.smm.database.*;
import de.hpi.smm.features.FeatureExtractor;
import org.jsoup.Jsoup;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FeatureComponent {

    public static final String SELECT_STATEMENT = "SELECT TABLE1.ID, TABLE1.POSTCONTENT FROM (SELECT %s AS ID, %s AS POSTCONTENT FROM %s LIMIT %d) AS TABLE1 FULL OUTER JOIN SMA1415.SAI_FEATURES AS TABLE2 ON TABLE1.ID = TABLE2.DOCUMENT_ID WHERE TABLE2.FEATURE_0 IS NULL AND TABLE1.POSTCONTENT IS NOT NULL";
    public static final String ID = "ID";
    public static final String CONTENT = "POSTCONTENT";

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Wrong number of arguments!");
            System.out.println("-----------------------------------------------");
            System.out.println("To start the program execute");
            System.out.println("  java -jar feature_component.jar <data-set-id> <limit>");
            System.out.println("-----------------------------------------------");
            System.out.println("data-set-id:");
            System.out.println("  1 -> smm data");
            System.out.println("  2 -> springer data");
            System.out.println("limit: number of documents for which the features should be extracted");
            return;
        }

        run(Integer.parseInt(args[0]));
    }

    /**
     * Get all documents where the features were not yet calculated,
     * calculate the features and
     * write them into the database.
     */
    public static void run(int dataSetId) throws LangDetectException, SQLException {
        FeatureExtractor featureExtractor = new FeatureExtractor();
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        databaseAdapter.setSchema(SchemaConfig.getWholeSchema(dataSetId));

        // Create language detector
        DetectorFactory.loadProfile(Config.PROFILES_DIR);
        Detector detector = DetectorFactory.create();

        // Get documents
        System.out.print("Executing SQL statement ... ");
        ResultSet resultSet = getResultSet(dataSetId, databaseAdapter);
        System.out.println("Done.");

        System.out.print("Extracting features ... ");
        while(resultSet.next()) {
            String id = resultSet.getString(ID);
            String content = resultSet.getString(CONTENT);

            if (content != null) {
                // clean text
                content = html2text(content);

                // detect language
                detector.append(content);
                String lang = detector.detect();

                // extract features
                List<Float> features = featureExtractor.getFeatures(content, lang);

                // write features
                write(databaseAdapter, features, id, dataSetId);
            }
        }
        System.out.println("Done.");

        // Close database connection
        databaseAdapter.closeConnection();
        System.out.println("Finished.");
    }

    private static ResultSet getResultSet(int dataSetId, DatabaseAdapter databaseAdapter) {
        String idColumn = null, contentColumn = null, tableName = null;
        if (dataSetId == 1){
            idColumn = SchemaConfig.SMM_ID;
            contentColumn = SchemaConfig.SMM_CONTENT;
            tableName = SchemaConfig.getSmmTableName();
        } else if (dataSetId == 2) {
            idColumn = SchemaConfig.SPINN3R_ID;
            contentColumn = SchemaConfig.SPINN3R_CONTENT;
            tableName = SchemaConfig.getSpinn3rTableName();
        }
        String statement = String.format(SELECT_STATEMENT, idColumn, contentColumn, tableName, 1000);

        return databaseAdapter.executeQuery(statement);
    }

    private static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    private static void write(DatabaseAdapter databaseAdapter, List<Float> features, String documentId, int dataSetId) {
        AbstractTableDefinition featureTableDefinition = databaseAdapter.getWriteTable(SchemaConfig.getFeatureTableName());

        featureTableDefinition.setRecordValuesToNull(); // this sets all values to NULL, so no value is left unattended
        featureTableDefinition.setValue(SchemaConfig.RUN_ID, dataSetId);
        featureTableDefinition.setValue(SchemaConfig.DOCUMENT_ID, documentId);
        for (int i = 0; i < features.size(); i++) {
            featureTableDefinition.setFeatureValue(i, features.get(i));
        }
        featureTableDefinition.writeRecord();
    }

}
