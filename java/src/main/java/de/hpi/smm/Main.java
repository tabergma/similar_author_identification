package de.hpi.smm;


import de.hpi.smm.feature_extraction.FeatureExtractor;
import de.hpi.smm.helper.FileWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        ResultSet rs = getTestSet();

        FeatureExtractor featureExtractor = new FeatureExtractor();

        ArrayList<List<Float>> list = new ArrayList<List<Float>>();

        try {
            while (rs.next()){
                String content = rs.getString("POSTCONTENT");
                if (content != null) {
                    list.add(featureExtractor.getFeatures(content));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        FileWriter.writeFeaturesToFile(list, "test");
    }

    private static ResultSet getTestSet() {
        String statement = "SELECT POSTCONTENT FROM SYSTEM.WEBPAGE LIMIT 100000";
        DatabaseAdapter dbAdapter = DatabaseAdapter.getSmaAdapter();
        return dbAdapter.executeQuery(statement);
    }
}
