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

    // TODO check file access!!!
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Wrong number of arguments!");
            System.out.println("-----------------------------------------------");
            System.out.println("To start the program execute");
            System.out.println("  java -jar feature_component.jar <data-set-id>");
            System.out.println("-----------------------------------------------");
            System.out.println("data-set-id:");
            System.out.println("  1 -> smm data");
            System.out.println("  2 -> springer data");
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

        // Load table
//        AbstractTableDefinition table = getTable(databaseAdapter, dataSetId);
//        System.out.println(table.formatCompleteReadStatement());
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

        ResultSet resultSet = databaseAdapter.executeQuery(statement);

        int i = 0;
        // read content and extract features
        while(resultSet.next()) {
            String id = resultSet.getString(SchemaConfig.SMM_ID);
            String content = null;
            content = resultSet.getString(SchemaConfig.SMM_CONTENT);
//            if (dataSetId == 1) {
//                content = resultSet.getString(SchemaConfig.SMM_CONTENT);
//            } else if (dataSetId == 2){
//                content = resultSet.getString(SchemaConfig.SPINN3R_CONTENT);
//            }

            if (content != null) {
                content = html2text(content);
                i++;
                // detect language
                detector.append(content);
                String lang = detector.detect();

                // extract features
                List<Float> features = featureExtractor.getFeatures(content, lang);

                // write features
                write(databaseAdapter, features, id, dataSetId);
            }
            if (i == 10){
                break;
            }
        }

        // Close database connection
        databaseAdapter.closeConnection();
    }

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    private static AbstractTableDefinition getTable(DatabaseAdapter databaseAdapter, int dataSetId) {
        Schema schema = SchemaConfig.getWholeSchema(dataSetId);
        databaseAdapter.setSchema(schema);

        AbstractTableDefinition featureTableDefinition = schema.getTableDefinition(SchemaConfig.getFeatureTableName());

        JoinedTableDefinition joinedTableDefinition = null;
        if (dataSetId == 1) { // smm
            AbstractTableDefinition documentTableDefinition = schema.getTableDefinition(SchemaConfig.getSmmTableName());
            joinedTableDefinition = new JoinedTableDefinition(documentTableDefinition, SchemaConfig.SMM_ID, featureTableDefinition, SchemaConfig.DOCUMENT_ID);
            joinedTableDefinition.addWhereClause(String.format("%s = null", SchemaConfig.DOCUMENT_ID));
        } else if (dataSetId == 2) { // spinn3r
            AbstractTableDefinition documentTableDefinition = schema.getTableDefinition(SchemaConfig.getSpinn3rTableName());
            joinedTableDefinition = new JoinedTableDefinition(documentTableDefinition, SchemaConfig.SPINN3R_ID, featureTableDefinition, SchemaConfig.DOCUMENT_ID);
            joinedTableDefinition.addWhereClause(String.format("%s = null", SchemaConfig.DOCUMENT_ID));
        }
        joinedTableDefinition.setJoinType(" FULL OUTER ");
        return databaseAdapter.getReadTable(joinedTableDefinition);
    }

    private static void write(DatabaseAdapter databaseAdapter, List<Float> features, String documentId, int dataSetId) {
        AbstractTableDefinition featureTableDefinition = databaseAdapter.getWriteTable(SchemaConfig.getFeatureTableName());

        featureTableDefinition.setRecordValuesToNull(); // this sets all values to NULL, so no value is left unattended
        featureTableDefinition.setValue(SchemaConfig.DATA_SET, dataSetId);
        featureTableDefinition.setValue(SchemaConfig.DOCUMENT_ID, documentId);
        for (int i = 0; i < features.size(); i++) {
            featureTableDefinition.setFeatureValue(i, features.get(i));
        }
        featureTableDefinition.writeRecord();
    }

}
