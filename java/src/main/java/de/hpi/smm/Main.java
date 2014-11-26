package de.hpi.smm;


import de.hpi.smm.clustering.ClusterAnalyzer;
import de.hpi.smm.clustering.KMeans;
import de.hpi.smm.drawing.Drawing;
import de.hpi.smm.drawing.Point;
import de.hpi.smm.features.FeatureExtractor;
import de.hpi.smm.helper.ClusterWriter;
import de.hpi.smm.helper.DatabaseAdapter;
import de.hpi.smm.helper.FeatureWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

public class Main {
    public static final int minLength = 50;

    public static void main(String[] args) throws Exception {
        System.out.println("Fetching data...");
        ResultSet rs = getTestSet();

        FeatureExtractor featureExtractor = new FeatureExtractor();
        FeatureWriter featureWriter = new FeatureWriter("features.txt");

        System.out.println("Extracting features...");
        List<String> documentTexts = new ArrayList<String>();
        try {
            while (rs.next()){
                String content = rs.getString("POSTCONTENT");
                if (content != null && content.length() > minLength) {
                    List<Float> features = featureExtractor.getFeatures(content);
                    featureWriter.writeFeaturesForDocument(features);
                    documentTexts.add(content);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        featureWriter.close();

        System.out.println("Performing K-Means...");
        KMeans kMeans = new KMeans();
        kMeans.run(readFeatureFile());

        System.out.println("Analyze clusters...");
        ClusterAnalyzer analyzer = new ClusterAnalyzer();
        analyzer.analyze();

        System.out.println("Draw image...");
        List<Map<Integer, Double>> points = analyzer.getPoints();
        List<Point> twoFeatures = new ArrayList<Point>();
        for (Map<Integer, Double> mapping : points){
            System.out.println(mapping);
            twoFeatures.add(new Point(mapping.get(0), mapping.get(351)));
        }
        Drawing.drawInWindow(twoFeatures);

        System.out.println("Writing cluster files...");
        ClusterWriter.writeClusterFiles(analyzer.getCluster2document(), documentTexts);
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
