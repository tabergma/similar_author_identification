package de.hpi.smm;


import de.hpi.smm.features.FeatureExtractor;
import de.hpi.smm.helper.DatabaseAdapter;
import de.hpi.smm.helper.FileWriter;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException {

        ResultSet rs = getTestSet();

        FeatureExtractor featureExtractor = new FeatureExtractor();
        FileWriter fileWriter = new FileWriter("features.txt");

        try {
            while (rs.next()){
                String content = rs.getString("POSTCONTENT");
                if (content != null) {
                    fileWriter.writeFeaturesForDocument(featureExtractor.getFeatures(content));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        fileWriter.close();
    }

    private static ResultSet getTestSet() {
        String statement = "SELECT POSTCONTENT FROM SYSTEM.WEBPAGE LIMIT 100000";
        DatabaseAdapter dbAdapter = DatabaseAdapter.getSmaAdapter();
        return dbAdapter.executeQuery(statement);
    }
}
