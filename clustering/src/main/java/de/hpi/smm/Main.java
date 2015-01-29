package de.hpi.smm;


import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import de.hpi.smm.clustering.*;
import de.hpi.smm.database.*;
import de.hpi.smm.evaluation.EvaluationResult;
import de.hpi.smm.evaluation.Evaluator;
import de.hpi.smm.evaluation.FeatureEvaluator;
import de.hpi.smm.features.FeatureExtractor;
import de.hpi.smm.io.ClusterWriter;
import de.hpi.smm.io.FeatureWriter;
import de.hpi.smm.io.SvmFeatureWriter;
import de.hpi.smm.sets.AbstractDataSet;
import de.hpi.smm.sets.Author;
import de.hpi.smm.sets.DataSetSelector;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) throws Exception {
        int limit = 20000;
        int minLength = 50;

        testDatabaseAdapter();

//        Util.switchErrorPrint(false);

//        System.out.println("Fetching data...");
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

        System.out.println("Finished.");
    }

    private static void testDatabaseAdapter() {
        // ========= writing =========

//        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
//        databaseAdapter.setSchema(SchemaConfig.getWholeSchema());
//
//        AbstractTableDefinition featureTableDefinition = databaseAdapter.getWriteTable(SchemaConfig.getFeatureTableName());
//        featureTableDefinition.setRecordValuesToNull(); // this sets all values to NULL, so no value is left unattended
//        featureTableDefinition.setValue(SchemaConfig.DATA_SET, 1);
//        featureTableDefinition.setValue(SchemaConfig.DOCUMENT_ID, 1);
//        featureTableDefinition.setFeatureValue(0, 0.0);
//        featureTableDefinition.setFeatureValue(1, 0.0);
//        featureTableDefinition.writeRecord();
//
//        databaseAdapter.closeConnection();

        // ========== reading ========

//        databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
//        databaseAdapter.setSchema(SchemaConfig.getWholeSchema());
//
//        featureTableDefinition = databaseAdapter.getReadTable(SchemaConfig.getDocumentClusterMappingTableName());
//        while(featureTableDefinition.next()) {
//            System.out.println(featureTableDefinition.getInt(SchemaConfig.DATA_SET));
//            System.out.println(featureTableDefinition.getInt(SchemaConfig.DOCUMENT_ID));
//            System.out.println(featureTableDefinition.getInt(SchemaConfig.CLUSTER_ID));
//        }
//
//        databaseAdapter.closeConnection();

        // ========== join + reading ========

        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        Schema schema = SchemaConfig.getWholeSchema(0);
        databaseAdapter.setSchema(schema);

        AbstractTableDefinition featureTableDefinition = schema.getTableDefinition(SchemaConfig.getFeatureTableName());
        AbstractTableDefinition documentClusterMapping = schema.getTableDefinition(SchemaConfig.getDocumentClusterMappingTableName());

        AbstractTableDefinition joinedTableDefinition = new JoinedTableDefinition(featureTableDefinition, SchemaConfig.DOCUMENT_ID, documentClusterMapping, SchemaConfig.DOCUMENT_ID);

        joinedTableDefinition = databaseAdapter.getReadTable(joinedTableDefinition);
        while(joinedTableDefinition.next()) {
            int v2 = joinedTableDefinition.getInt(SchemaConfig.DOCUMENT_ID);
            String s = joinedTableDefinition.getString(SchemaConfig.LABELS);
            double v3 = joinedTableDefinition.getFeatureValue(0);
        }

        databaseAdapter.closeConnection();
    }

    private static void evaluateFeatures(AbstractDataSet testSet) throws Exception {
        System.out.println("Evaluation of features...");
        FeatureEvaluator.run(FeatureExtractor.getIndexToFeatureMap(), testSet, FeatureWriter.readFeatureFile());
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
        kMeans.run(FeatureWriter.readFeatureFile());

        System.out.println("Analyze clusters...");
        ClusterAnalyzer analyzer = new ClusterAnalyzer(Config.CLUSTER_FILE, Config.CLUSTER_CENTER_FILE);
        CacheResultHandler resultHandler = new CacheResultHandler();
        analyzer.analyzeMahout(resultHandler);


        System.out.println("Clean up...");
        kMeans.cleanUp();

        System.out.println("Labeling clusters...");
        ClusterLabeling labeling = new ClusterLabeling(resultHandler.getClusters(), FeatureExtractor.getIndexToFeatureMap());
        List<ClusterCentroid> clusterCentroids = labeling.labelClusters();

        System.out.println("Calculate precision...");
        Evaluator evaluator = new Evaluator();
        List<EvaluationResult> resultList = evaluator.evaluate(testSet, resultHandler.getCluster2document());
        for (EvaluationResult result : resultList){
            System.out.println(result.toString());
        }

        System.out.println("Writing cluster files...");
//        ClusterWriter.writeClusterFiles(analyzer.getCluster2document(), testSet.getDocumentTexts());
        ClusterWriter.writeClusterCenterFiles(resultList, clusterCentroids);
        ClusterWriter.writeBlogPosts(resultHandler.getBlogPosts());

        System.out.println("Writing files for SVM training...");
        SvmFeatureWriter svmFeatureWriter = new SvmFeatureWriter(Config.SVM_FEATURE_FILE);
        svmFeatureWriter.writeFeaturesForAllBlogPosts(resultHandler.getBlogPosts());

//        System.out.println("Draw image...");
//        Map<Integer, List<Integer>> cluster2documents = analyzer.getCluster2document();
//        List<Point> twoFeatures = new ArrayList<Point>();
//        for (Map.Entry<Integer, List<Integer>> c2d : cluster2documents.entrySet()) {
//            for (Integer docId : c2d.getValue()) {
//                twoFeatures.add(new Point(docId, c2d.getKey() + 1));
//            }
//        }
//        Drawing.drawInWindow(twoFeatures);
    }

    private static void printSet(AbstractDataSet testSet) {
        while (testSet.next()) {
            System.out.println(testSet.getText());
        }
    }



}
