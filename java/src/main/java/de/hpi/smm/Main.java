package de.hpi.smm;


import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import de.hpi.smm.clustering.ClusterAnalyzer;
import de.hpi.smm.clustering.ClusterCentroid;
import de.hpi.smm.clustering.ClusterLabeling;
import de.hpi.smm.clustering.KMeans;
import de.hpi.smm.evaluation.EvaluationResult;
import de.hpi.smm.evaluation.Evaluator;
import de.hpi.smm.evaluation.FeatureEvaluator;
import de.hpi.smm.evaluation.cluster_determination.SvmFeatureWriter;
import de.hpi.smm.evaluation.cluster_determination.TenFoldCrossValidation;
import de.hpi.smm.features.FeatureExtractor;
import de.hpi.smm.helper.ClusterWriter;
import de.hpi.smm.helper.FeatureWriter;
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

        // cluster the set based on the last time the festures were extracted
        clusterSet(testSet);

        // evaluate all features - TAKES A LONG TIME!
//        evaluateFeatures(testSet);

        // train a model for the SVM
        svmCluster(testSet);
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

//        System.out.println("10-fold cross validation...");
//        TenFoldCrossValidation tenFoldCrossValidation = new TenFoldCrossValidation(analyzer.getBlogPosts());
//        System.out.println("Result for k-nearest neighbor: " + tenFoldCrossValidation.validateKNearestNeighbor());
//        runSvmTenFoldCrossValidation();
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

        System.out.println("Training a model for the SVM...");
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
