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

    private static final String SELECT_STATEMENT = "SELECT TABLE1.ID, TABLE1.POSTCONTENT FROM (SELECT %s AS ID, %s AS POSTCONTENT FROM %s LIMIT %d OFFSET %d) AS TABLE1 FULL OUTER JOIN SMA1415.SAI_FEATURES AS TABLE2 ON TABLE1.ID = TABLE2.DOCUMENT_ID WHERE TABLE2.FEATURE_0 IS NULL AND TABLE1.POSTCONTENT IS NOT NULL";
    private static final String ID = "ID";
    private static final String CONTENT = "POSTCONTENT";
    private static final int CHUNK_SIZE = 1000;

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Wrong number of arguments!");
            System.out.println("-----------------------------------------------");
            System.out.println("To start the program execute");
            System.out.println("  java -cp similar_author_identification.jar de.hpi.smm.components.FeatureComponent <data-set-id> <limit> <language>");
            System.out.println("-----------------------------------------------");
            System.out.println("data-set-id:");
            System.out.println("  1 -> smm data");
            System.out.println("  2 -> springer data");
            System.out.println("limit: number of documents for which the features should be extracted");
            System.out.println("language: language of the blog posts, can be 'de' or 'en'");
            return;
        }

        int dataSetId = Integer.parseInt(args[0]);
        int limit = Integer.parseInt(args[1]);
        String language = args[2];

        run(dataSetId, limit, language);
    }

    /**
     * Get all documents where the features were not yet calculated,
     * calculate the features and
     * write them into the database.
     *
     * @param dataSetId identifies the original data set, 1 for smm data and 2 for springer data
     * @param limit     number of documents for which the features should be extracted
     * @param language  the language of the blog posts, can be either 'de' or 'en'
     */
    public static void run(int dataSetId, int limit, String language) throws LangDetectException, SQLException {
        FeatureExtractor featureExtractor = new FeatureExtractor(language);
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        databaseAdapter.setSchema(SchemaConfig.getSchemaForFeatureAccess(dataSetId, language));

        // Create language detector
        DetectorFactory.loadProfile(Config.PROFILES_DIR);
        Detector detector;

        // Get documents
        int offset = 0;
        ResultSet resultSet = getResultSet(dataSetId, databaseAdapter, CHUNK_SIZE, offset);

        System.out.println("Extracting features ... ");
        int count = 0;
        int skipped = 0;
        while(count < limit) {
            if (!resultSet.next()) {
                offset += CHUNK_SIZE;
                resultSet = getResultSet(dataSetId, databaseAdapter, CHUNK_SIZE, offset);
                if (!resultSet.next()){
                    break;
                }
            }

            String id = resultSet.getString(ID);
            String content = resultSet.getString(CONTENT);

            if (content != null) {
                // clean text
                content = html2text(content);

                // detect language
                detector = DetectorFactory.create();
                detector.append(content);
                String lang;
                try {
                    lang = detector.detect();
                } catch (LangDetectException le){
                    continue;
                }

                if (!lang.equals(language)) {
                    skipped += 1;
                    if (skipped % 1000 == 0){
                        System.out.println(String.format("Skipped %d blog posts...", skipped));
                    }
                    continue;
                }

                // extract features
                List<Float> features = featureExtractor.getFeatures(content);

                // write features
                write(databaseAdapter, features, id, dataSetId, language);
                count++;
                if (count % 100 == 0){
                    System.out.println(String.format("Feature for %d blog posts calculated...", count));
                }
            }
        }
        System.out.println("Done.");

        // Close database connection
        databaseAdapter.closeConnection();
        System.out.println("Finished.");
    }

    private static ResultSet getResultSet(int dataSetId, DatabaseAdapter databaseAdapter, int limit, int offset) {
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
        String statement = String.format(SELECT_STATEMENT, idColumn, contentColumn, tableName, limit, offset);

        return databaseAdapter.executeQuery(statement);
    }

    private static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    private static void write(DatabaseAdapter databaseAdapter, List<Float> features, String documentId, int dataSetId, String language) {
        AbstractTableDefinition featureTableDefinition = databaseAdapter.getWriteTable(SchemaConfig.getFeatureTableName());

        featureTableDefinition.setRecordValuesToNull(); // this sets all values to NULL, so no value is left unattended
        featureTableDefinition.setValue(SchemaConfig.DATA_SET, dataSetId);
        featureTableDefinition.setValue(SchemaConfig.DOCUMENT_ID, documentId);
        featureTableDefinition.setValue(SchemaConfig.LANGUAGE, language);
        for (int i = 0; i < features.size(); i++) {
            featureTableDefinition.setFeatureValue(i, features.get(i));
        }
        featureTableDefinition.writeRecord();
    }

}
