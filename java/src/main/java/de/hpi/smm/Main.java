package de.hpi.smm;


import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import de.hpi.smm.clustering.ClusterAnalyzer;
import de.hpi.smm.clustering.ClusterCentroid;
import de.hpi.smm.clustering.ClusterLabeling;
import de.hpi.smm.clustering.KMeans;
import de.hpi.smm.drawing.Drawing;
import de.hpi.smm.drawing.Point;
import de.hpi.smm.evaluation.EvaluationResult;
import de.hpi.smm.evaluation.Evaluator;
import de.hpi.smm.evaluation.FeatureEvaluator;
import de.hpi.smm.helper.SvmFeatureWriter;
import de.hpi.smm.features.FeatureExtractor;
import de.hpi.smm.helper.ClusterWriter;
import de.hpi.smm.helper.FeatureWriter;
import de.hpi.smm.sets.AbstractDataSet;
import de.hpi.smm.sets.Author;
import de.hpi.smm.sets.DataSetSelector;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Main {

    public static void main(String[] args) throws Exception {
        int limit = 20000;
        int minLength = 50;

//        Util.switchErrorPrint(false);

        System.out.println("Fetching data...");
//        AbstractDataSet testSet = DataSetSelector.getDataSet(DataSetSelector.SMM_SET, minLength, limit);
//        AbstractDataSet testSet = DataSetSelector.getDataSet(DataSetSelector.SPINNER_SET, minLength, limit);
        AbstractDataSet testSet = DataSetSelector.getDataSet(DataSetSelector.GERMAN_SET, minLength, -1);

        // print out the test content
//        printSet(testSet);

        // extract the features from the set
        extractFeatures(testSet);

        // cluster the set based on the last time the features were extracted
        clusterSet(testSet);

        // evaluate all features - TAKES A LONG TIME!
//        evaluateFeatures(testSet);
    }

    private static void evaluateFeatures(AbstractDataSet testSet) throws Exception {
        System.out.println("Evaluation of features...");
        FeatureEvaluator.run(FeatureExtractor.getIndexToFeatureMap(), testSet, readFeatureFile());
        return;
    }

    private static void extractFeatures(AbstractDataSet testSet) throws Exception {
        FeatureExtractor featureExtractor = new FeatureExtractor();
        FeatureWriter featureWriter = new FeatureWriter();

        // Create language detector
        DetectorFactory.loadProfile(Config.PROFILES_DIR);
        Detector detector = DetectorFactory.create();

        System.out.println("Extracting features...");
        int i = 0;
        List<String> documentTexts = new ArrayList<String>();
        List<Float> features;
        while (testSet.next()) {
            String content = testSet.getText();
            if (content != null) {
                // detect language
                detector.append(content);
                String lang = detector.detect();

                // extract features
                features = featureExtractor.getFeatures(content, lang);

                // write features
                featureWriter.writeFeaturesForDocument(features, testSet.getAuthor().getId());
                documentTexts.add(content);
            }
            if (++i % 100 == 0) {
                System.out.println(String.format("Features from %d documents extracted...", i));
            }
        }
        testSet.saveTexts(documentTexts);

        featureWriter.close();
    }

    private static void clusterSet(AbstractDataSet testSet) throws Exception {
        List<Author> authors = testSet.getAuthors();
        FileUtils.writeStringToFile(new File(Config.AUTHOR_FILE), org.apache.commons.lang.StringUtils.join(authors, Config.FEATURE_SEPARATOR));

        System.out.println("Performing K-Means...");
        KMeans kMeans = new KMeans();
        kMeans.run(readFeatureFile());

        System.out.println("Analyze clusters...");
        ClusterAnalyzer analyzer = new ClusterAnalyzer();
        analyzer.analyzeMahout();

        System.out.println("Clean up...");
        kMeans.cleanUp();

        System.out.println("Labeling clusters...");
        ClusterLabeling labeling = new ClusterLabeling(analyzer.getCenters(), analyzer.getCluster2document(), FeatureExtractor.getIndexToFeatureMap());
        List<ClusterCentroid> clusterCentroids = labeling.labelClusters();

        System.out.println("Calculate precision...");
        Evaluator evaluator = new Evaluator();
        List<EvaluationResult> resultList = evaluator.evaluate(testSet, analyzer.getCluster2document());
        for (EvaluationResult result : resultList){
            System.out.println(result.toString());
        }

        System.out.println("Writing cluster files...");
//        ClusterWriter.writeClusterFiles(analyzer.getCluster2document(), testSet.getDocumentTexts());
        ClusterWriter.writeClusterCenterFiles(resultList, clusterCentroids);
        ClusterWriter.writeBlogPosts(analyzer.getBlogPosts());

        System.out.println("Writing files for SVM training...");
        SvmFeatureWriter svmFeatureWriter = new SvmFeatureWriter();
        svmFeatureWriter.writeFeaturesForAllBlogposts(analyzer.getBlogPosts());

        System.out.println("Draw image...");
        Map<Integer, List<Integer>> cluster2documents = analyzer.getCluster2document();
        List<Point> twoFeatures = new ArrayList<Point>();
        for (Map.Entry<Integer, List<Integer>> c2d : cluster2documents.entrySet()) {
            for (Integer docId : c2d.getValue()) {
                twoFeatures.add(new Point(docId, c2d.getKey() + 1));
            }
        }
        Drawing.drawInWindow(twoFeatures);
    }

    private static void printSet(AbstractDataSet testSet) {
        while (testSet.next()) {
            System.out.println(testSet.getText());
        }
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
