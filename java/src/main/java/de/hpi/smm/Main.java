package de.hpi.smm;


import de.hpi.smm.clustering.KMeans;
import de.hpi.smm.features.FeatureExtractor;
import de.hpi.smm.helper.DatabaseAdapter;
import de.hpi.smm.helper.FileWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Fetching data...");
        ResultSet rs = getTestSet();

        FeatureExtractor featureExtractor = new FeatureExtractor();
        FileWriter fileWriter = new FileWriter("features.txt");

        System.out.println("Extracting features...");
        try {
            while (rs.next()){
                String content = rs.getString("POSTCONTENT");
                if (content != null) {
                    List<Float> features = featureExtractor.getFeatures(content);
                    fileWriter.writeFeaturesForDocument(features);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        fileWriter.close();

        System.out.println("Performing K-Means...");
        KMeans kMeans = new KMeans();
        //System.out.println(kMeans.run(readFeatureFile()));
    }

    private static ResultSet getTestSet() {
        String statement = "SELECT POSTCONTENT FROM SYSTEM.WEBPAGE LIMIT 100000";
        DatabaseAdapter dbAdapter = DatabaseAdapter.getSmaHanaAdapter();
        return dbAdapter.executeQuery(statement);
    }

    private static List<List<Float>> readFeatureFile() throws IOException {
        List<List<Float>> list = new ArrayList<List<Float>>();

        BufferedReader br = new BufferedReader(new FileReader("../output/features.txt"));
        try {
            String line = br.readLine();

            while (line != null) {
                List<Float> l = new ArrayList<Float>();

                String[] fs = line.split(" ");
                for (String f : fs)
                    l.add(Float.parseFloat(f));

                list.add(l);
                line = br.readLine();
            }
        } finally {
            br.close();
        }

        return list;
    }
}
