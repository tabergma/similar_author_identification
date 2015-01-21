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
import de.hpi.smm.evaluation.cluster_determination.TenFoldCrossValidation;
import de.hpi.smm.features.FeatureExtractor;
import de.hpi.smm.helper.ClusterWriter;
import de.hpi.smm.helper.FeatureWriter;
import de.hpi.smm.evaluation.cluster_determination.SvmFeatureWriter;
import de.hpi.smm.libsvm.svm_train;
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

/*
    TODO
    - Feature evaluation: Find best combination of features
    - Implement new project which implements the interface for clustering a new blog post
    - Rewrite some features? -> Boolean Feature, other ranges, ...
    - Weight features?
    - Naming clusters
    - Look into blog posts by hand: How can we improve cluster assigning for this blog?
    - Configuration of K-Means
    - search for feature selection algorithmns?
 */

public class Main {

    public static void main(String[] args) throws Exception {
        int limit = 20000;
        int minLength = 50;

//        Util.switchErrorPrint(false);

        System.out.println("Fetching data...");
//        AbstractDataSet testSet1 = DataSetSelector.getDataSet(DataSetSelector.SMM_SET, minLength, limit);
        AbstractDataSet testSet2 = DataSetSelector.getDataSet(DataSetSelector.SPINNER_SET, minLength, limit);
//        AbstractDataSet testSet3 = DataSetSelector.getDataSet(DataSetSelector.GERMAN_SET, minLength, -1);

        AbstractDataSet testSet = testSet2;

        // print out the test content
//        printSet(testSet);

        // extract the features from the set
//        extractFeatures(testSet);

        // cluster the set based on the last time the festures were extracted
        clusterSet(testSet);

        // evaluate all features - TAKES A LONG TIME!
//        evaluateFeatures(testSet);

        // train a model for the SVM
//        svmCluster(testSet);
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


        System.out.println("Extracting features...");g
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

//        System.out.println("10-fold cross validation...");
//        TenFoldCrossValidation tenFoldCrossValidation = new TenFoldCrossValidation(analyzer.getBlogPosts());
//        System.out.println("Result for k-nearest neighbor: " + tenFoldCrossValidation.validateKNearestNeighbor());
//        SvmFeatureWriter svmFeatureWriter = new SvmFeatureWriter();
//        svmFeatureWriter.writeFeaturesForAllBlogposts(analyzer.getBlogPosts());
//        runSvmTenFoldCrossValidation();

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

//        System.out.println("Draw image...");
//        Map<Integer, List<Integer>> cluster2documents = analyzer.getCluster2document();
//        List<Point> twoFeatures = new ArrayList<Point>();
//        for (Map.Entry<Integer, List<Integer>> c2d : cluster2documents.entrySet()) {
//            for (Integer docId : c2d.getValue()) {
//                twoFeatures.add(new Point(docId, c2d.getKey() + 1));
//            }
//        }
//        Drawing.drawInWindow(twoFeatures);

        System.out.println("Writing cluster files...");
//        ClusterWriter.writeClusterFiles(analyzer.getCluster2document(), testSet.getDocumentTexts());
        ClusterWriter.writeClusterCenterFiles(resultList, clusterCentroids);
        ClusterWriter.writeBlogPosts(analyzer.getBlogPosts());
    }

    private static void runSvmTenFoldCrossValidation() throws IOException {
        // 10-fold cross validation
        String[] validation = {"-q", "-t", "2", "-s", "0", "-c", "100", "-v", "10", Config.SVM_FEATURE_FILE};
        svm_train.main(validation);
    }

    private static void svmCluster(AbstractDataSet testSet) throws IOException {
        // on command line
        // java svm_train -s 0 -t 0 -b 1 -q ../features.svm ../features.svm.model
        // java svm_predict -b 1 ../features.svm ../features.svm.model ../output

        // 10-fold cross validation
//        String[] validation = {"-q", "-t", "2", "-s", "0", "-c", "100", "-v", "10", Config.SVM_FEATURE_FILE};
//        svm_train.main(validation);

        // create model
        String[] createModel = {"-q", "-t", "2", "-s", "0", "-c", "100", "-b", "1", Config.SVM_FEATURE_FILE, Config.SVM_MODEL_FILE};
        svm_train.main(createModel);
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
