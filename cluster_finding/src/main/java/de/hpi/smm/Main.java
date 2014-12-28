package de.hpi.smm;


import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import de.hpi.smm.clustering.ClusterAnalyzer;
import de.hpi.smm.clustering.KMeans;
import de.hpi.smm.drawing.Drawing;
import de.hpi.smm.drawing.Point;
import de.hpi.smm.evaluation.EvaluationResult;
import de.hpi.smm.evaluation.Evaluator;
import de.hpi.smm.evaluation.FeatureEvaluator;
import de.hpi.smm.features.FeatureExtractor;
import de.hpi.smm.helper.ClusterWriter;
import de.hpi.smm.helper.FeatureWriter;
import de.hpi.smm.helper.Util;
import de.hpi.smm.sets.AbstractDataSet;
import de.hpi.smm.sets.DataSetSelector;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
    TODO
    - Feature evaluation: Find best combination of features
    - Implement new project which implements the interface for clustering a new blog post
    - Rewrite some features? -> Boolean Feature, other ranges, ...
    - Weight features?
    - Naming clusters
    - Look into blog posts by hand: How can we improve cluster assigning for this blog?
    - Configuration of K-Means
 */

public class Main {
    public static void main(String[] args) throws Exception {
        int limit = 1000;
        int minLength = 50;

        Util.switchErrorPrint(false);

        AbstractDataSet testSet1 = DataSetSelector.getDataSet(DataSetSelector.SMM_SET, minLength, limit);
        AbstractDataSet testSet2 = DataSetSelector.getDataSet(DataSetSelector.SPINNER_SET, minLength, limit);
        AbstractDataSet testSet3 = DataSetSelector.getDataSet(DataSetSelector.GERMAN_SET, minLength, -1);

//        printSet(testSet3);
        clusterSet(testSet3);
    }

    private static void printSet(AbstractDataSet testSet) {
        while (testSet.next()){
            System.out.println(testSet.getText());
        }
    }

    private static void clusterSet(AbstractDataSet testSet) throws Exception {
        System.out.println("Fetching data...");

        FeatureExtractor featureExtractor = new FeatureExtractor();
        FeatureWriter featureWriter = new FeatureWriter();

        // Create language detector
        DetectorFactory.loadProfile(Config.PROFILES_DIR);
        Detector detector = DetectorFactory.create();

        System.out.println("Extracting features...");
        int i = 0;
        List<String> documentTexts = new ArrayList<String>();
        List<Float> features;
        while (testSet.next()){
            String content = testSet.getText();
            if (content != null) {
                // detect language
                detector.append(content);
                String lang = detector.detect();

                // extract features
                features = featureExtractor.getFeatures(content, lang);

                // write features
                featureWriter.writeFeaturesForDocument(features);
                documentTexts.add(content);
            }
            if (++i % 100 == 0){
                System.out.println(String.format("Features from %d documents extracted...",i));
            }
        }

        featureWriter.close();

        if (Config.EVALUATE_FEATURES) {
            System.out.println("Evaluation of features...");
            FeatureEvaluator.run(featureExtractor, testSet, readFeatureFile());
            return;
        }

        System.out.println("Performing K-Means...");
        KMeans kMeans = new KMeans();
        kMeans.run(readFeatureFile());

        System.out.println("Analyze clusters...");
        ClusterAnalyzer analyzer = new ClusterAnalyzer();
        analyzer.analyze();

        System.out.println("Calculate precision...");
        Evaluator evaluator = new Evaluator();
        List<EvaluationResult> resultList = evaluator.evaluate(testSet, analyzer.getCluster2document());
        for (EvaluationResult result : resultList){
            System.out.println(result.toString());
        }

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