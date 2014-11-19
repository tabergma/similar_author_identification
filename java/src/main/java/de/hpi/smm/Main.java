package de.hpi.smm;


import de.hpi.smm.features.FeatureExtractor;
import de.hpi.smm.helper.FileWriter;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        DatabaseAdapter dbAdapter = DatabaseAdapter.getSmaHanaAdapter();
        return dbAdapter.executeQuery(statement);
    }
}
