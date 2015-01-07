package de.hpi.smm.evaluation;


import de.hpi.smm.Config;
import de.hpi.smm.clustering.ClusterAnalyzer;
import de.hpi.smm.clustering.KMeans;
import de.hpi.smm.features.Feature;
import de.hpi.smm.features.FeatureExtractor;
import de.hpi.smm.sets.AbstractDataSet;
import de.hpi.smm.sets.Author;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureEvaluator {

    private static Map<Integer, Feature> index2Feature = new HashMap<Integer, Feature>();

    private static PrintWriter evaluationFMeasureWriter;
    private static PrintWriter evaluationRecallWriter;
    private static PrintWriter evaluationPrecisionWriter;
    private static PrintWriter evaluationClusterWriter;

    private static double sumFMeasure;
    private static double sumPrecision;
    private static double sumRecall;

    public static void run(FeatureExtractor featureExtractor, AbstractDataSet testSet, List<List<Float>> features) throws Exception {
        // build index - feature map
        index2Feature = featureExtractor.getIndex2FeatureMap();

        // Build all combinations of features
        List<Integer> l = new ArrayList<Integer>();
        l.addAll(index2Feature.keySet());

        Combination combination = new Combination();

        double numberOfCombinations = Math.pow(2, index2Feature.size()) - 1; // minus empty set

        KMeans kMeans = new KMeans();
        ClusterAnalyzer analyzer = new ClusterAnalyzer();
        Evaluator evaluator = new Evaluator();

        evaluationFMeasureWriter =  new PrintWriter(Config.EVALUATION_FEATURE_F_MEASURE_FILE, "UTF-8");
        evaluationRecallWriter =  new PrintWriter(Config.EVALUATION_FEATURE_RECALL_FILE, "UTF-8");
        evaluationPrecisionWriter =  new PrintWriter(Config.EVALUATION_FEATURE_PRECISION_FILE, "UTF-8");
        evaluationClusterWriter =  new PrintWriter(Config.EVALUATION_FEATURE_CLUSTER_FILE, "UTF-8");
        writeHeaders(testSet);

        for (int i = 1; i <= numberOfCombinations; i++) {
            System.out.println("Testing combination " + i + " of " + numberOfCombinations + "...");
            List<Integer> currentCombination = combination.getNext();

            // get the feature combination
            List<List<Float>> featureCombination = getFeatureCombination(features, currentCombination);

            // run k-Means
            kMeans.run(featureCombination);

            // analyze cluster
            analyzer.analyze();

            // calculate F-Measure, Precision, Recall
            List<EvaluationResult> resultList = evaluator.evaluate(testSet, analyzer.getCluster2document());

            // write results to file
            writeResult(currentCombination, resultList);

            // clean up
            kMeans.cleanUp();
        }

        evaluationFMeasureWriter.close();
        evaluationPrecisionWriter.close();
        evaluationRecallWriter.close();
        evaluationClusterWriter.close();
    }

    private static List<List<Float>> getFeatureCombination(List<List<Float>> features, List<Integer> combination) {
        List<List<Float>> featureCombination = new ArrayList<List<Float>>();

        for (List<Float> feature : features) {
            List<Float> f = new ArrayList<Float>();
            for (Integer featureIndex : combination) {
                int start = index2Feature.get(featureIndex).start;
                int end = index2Feature.get(featureIndex).end;

                f.addAll(feature.subList(start, end));
            }
            featureCombination.add(f);
        }

        return featureCombination;
    }

    private static void writeHeaders(AbstractDataSet testSet) {
        for (int i = 0; i < index2Feature.size(); i++) {
            evaluationFMeasureWriter.write(index2Feature.get(i).name + ",");
            evaluationPrecisionWriter.write(index2Feature.get(i).name + ",");
            evaluationRecallWriter.write(index2Feature.get(i).name + ",");
            evaluationClusterWriter.write(index2Feature.get(i).name + ",");
        }
        for (Author author : testSet.getAuthors()) {
            String name = author.toString();
            evaluationFMeasureWriter.write(name + "_f-measure,");
            evaluationPrecisionWriter.write(name + "_precision,");
            evaluationRecallWriter.write(name + "_recall,");
            evaluationClusterWriter.write(name + "_cluster,");
        }

        evaluationFMeasureWriter.write("f-measure-avg\n");
        evaluationPrecisionWriter.write("precision-avg\n");
        evaluationRecallWriter.write("recall-avg\n");
        evaluationClusterWriter.write("\n");
    }

    private static void writeResult(List<Integer> combination, List<EvaluationResult> resultList) {
        // write feature combination
        for (int i = 0; i < index2Feature.size(); i++) {
            if (combination.contains(i)) {
                evaluationFMeasureWriter.write("1,");
                evaluationPrecisionWriter.write("1,");
                evaluationRecallWriter.write("1,");
                evaluationClusterWriter.write("1,");
            } else {
                evaluationFMeasureWriter.write("0,");
                evaluationPrecisionWriter.write("0,");
                evaluationRecallWriter.write("0,");
                evaluationClusterWriter.write("0,");
            }
        }

        sumFMeasure = 0.0;
        sumPrecision = 0.0;
        sumRecall = 0.0;

        for (EvaluationResult result : resultList) {
            sumFMeasure += result.getFMeasure();
            sumPrecision += result.getPrecision();
            sumRecall += result.getRecall();
            // write precision, recall, f-measure and cluster
            evaluationFMeasureWriter.write(String.valueOf(result.getFMeasure()) + ",");
            evaluationPrecisionWriter.write(String.valueOf(result.getPrecision()) + ",");
            evaluationRecallWriter.write(String.valueOf(result.getRecall()) + ",");
            evaluationClusterWriter.write(String.valueOf(result.getCluster()) + ",");
        }
        // write avg values
        evaluationFMeasureWriter.write(String.valueOf(sumFMeasure / resultList.size()));
        evaluationPrecisionWriter.write(String.valueOf(sumPrecision / resultList.size()));
        evaluationRecallWriter.write(String.valueOf(sumRecall / resultList.size()));
        // new line
        evaluationFMeasureWriter.write("\n");
        evaluationPrecisionWriter.write("\n");
        evaluationRecallWriter.write("\n");
        evaluationClusterWriter.write("\n");
    }
}

class Combination {
    private int current = 0b1;

    public List<Integer> getNext() {
        List<Integer> l = new ArrayList<>();
        // get the positions of 1 bits
        String number = Integer.toBinaryString(current);
        int max = number.length() - 1;
        for (int i = max; i >= 0; i--){
            char c = number.charAt(i);
            if (c == '1')
                l.add(max - i);
        }
        // increment the current value
        current += 0b1;
        // return the current combination
        return l;
    }
}
