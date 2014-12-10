package de.hpi.smm;


import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import de.hpi.smm.clustering.ClusterAnalyzer;
import de.hpi.smm.clustering.KMeans;
import de.hpi.smm.drawing.Drawing;
import de.hpi.smm.drawing.Point;
import de.hpi.smm.evaluation.Evaluator;
import de.hpi.smm.features.FeatureExtractor;
import de.hpi.smm.helper.*;
import de.hpi.smm.sets.DataSetSelector;
import de.hpi.smm.sets.TestSet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static final int minLength = 50;

    public static void main(String[] args) throws Exception {
        System.out.println("Fetching data...");

        TestSet testSet = DataSetSelector.getDataSet(Config.SELECTED_SET);

        FeatureExtractor featureExtractor = new FeatureExtractor();
        FeatureWriter featureWriter = new FeatureWriter();

        // Create language detector
        DetectorFactory.loadProfile(Config.PROFILES_DIR);
        Detector detector = DetectorFactory.create();

        System.out.println("Extracting features...");
        List<String> documentTexts = new ArrayList<String>();
        while (testSet.next()){
            String content = testSet.getText();
            if (content != null && content.length() > minLength) {
                // detect language
                detector.append(content);
                String lang = detector.detect();

                // extract features
                List<Float> features = featureExtractor.getFeatures(content, lang);

                // write features
                featureWriter.writeFeaturesForDocument(features);
                documentTexts.add(content);
            }
        }

        featureWriter.close();

        System.out.println("Performing K-Means...");
        KMeans kMeans = new KMeans();
        kMeans.run(readFeatureFile());

        System.out.println("Analyze clusters...");
        ClusterAnalyzer analyzer = new ClusterAnalyzer();
        analyzer.analyze();

        System.out.println("Calculate precision...");
        Evaluator evaluator = new Evaluator();
        evaluator.evaluate(testSet.getDocumentToAuthorMapping(), analyzer.getCluster2document());

        System.out.println("Draw image...");
        Map<Integer, List<Integer>> cluster2documents = analyzer.getCluster2document();
        List<Point> twoFeatures = new ArrayList<Point>();
        for (Map.Entry<Integer, List<Integer>> c2d : cluster2documents.entrySet()) {
            for (Integer docId : c2d.getValue()) {
                twoFeatures.add(new Point(docId, c2d.getKey() + 1));
            }
        }
        Drawing.drawInWindow(twoFeatures);

        System.out.println("Writing cluster files...");
        ClusterWriter.writeClusterFiles(analyzer.getCluster2document(), documentTexts);

        System.out.println("Clean up...");
        kMeans.cleanUp();
    }

    private static List<List<Float>> readFeatureFile() throws IOException {
        List<List<Float>> list = new ArrayList<List<Float>>();

        BufferedReader br = new BufferedReader(new FileReader(Config.FEATURE_FILE));
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
