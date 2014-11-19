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

        String statement = "SELECT POSTCONTENT FROM SYSTEM.WEBPAGE";
        DatabaseAdapter dbAdapter = DatabaseAdapter.getSmaAdapter();
        ResultSet rs = dbAdapter.executeQuery(statement);
        try {
            if (rs.next()){
                System.out.println(rs.getString("POSTCONTENT"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        String filePath = "test.txt";

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine();

        FeatureExtractor featureExtractor = new FeatureExtractor();

        ArrayList<List<Float>> list = new ArrayList<List<Float>>();
        list.add(featureExtractor.getFeatures(line));

        FileWriter.writeFeaturesToFile(list, "word");
    }
}
