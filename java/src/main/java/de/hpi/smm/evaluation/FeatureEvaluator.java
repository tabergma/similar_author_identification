package de.hpi.smm.evaluation;


import de.hpi.smm.Config;
import de.hpi.smm.clustering.ClusterAnalyzer;
import de.hpi.smm.clustering.KMeans;
import de.hpi.smm.features.AbstractTextFeature;
import de.hpi.smm.features.AbstractTokenFeature;
import de.hpi.smm.features.FeatureExtractor;
import de.hpi.smm.sets.AbstractDataSet;
import de.hpi.smm.sets.Author;

import java.io.PrintWriter;
import java.util.*;

public class FeatureEvaluator {

    private static Map<Integer, Feature> index2Feature = new HashMap<Integer, Feature>();
    private static PrintWriter evaluationWriter;

    public static void run(FeatureExtractor featureExtractor, AbstractDataSet testSet, List<List<Float>> features) throws Exception {
        // build index - feature map
        buildIndex2FeatureMap(featureExtractor);

        // Build all combinations of features
        List<Integer> l = new ArrayList<Integer>();
        l.addAll(index2Feature.keySet());

        Combination combination = new Combination();

        double numberOfCombinations = Math.pow(2, index2Feature.size()) - 1; // minus empty set

        KMeans kMeans = new KMeans();
        ClusterAnalyzer analyzer = new ClusterAnalyzer();
        Evaluator evaluator = new Evaluator();
        evaluationWriter =  new PrintWriter(Config.EVALUATION_FEATURE_FILE, "UTF-8");
        writeHeader(testSet);

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

        evaluationWriter.close();
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

    private static void buildIndex2FeatureMap(FeatureExtractor featureExtractor) {
        int offset = 0;
        int index = 0;

        for (AbstractTokenFeature tokenFeature : featureExtractor.getTokenFeatureList()) {
            addFeature(tokenFeature.getName(),
                    offset,
                    offset + tokenFeature.getNumberOfFeatures(),
                    index
            );
            // Increment index and offset
            index += 1;
            offset += tokenFeature.getNumberOfFeatures();
        }
        for (AbstractTextFeature textFeature : featureExtractor.getTextFeatureList()) {
            addFeature(textFeature.getName(),
                    offset,
                    offset + textFeature.getNumberOfFeatures(),
                    index
            );
            // Increment index and offset
            index += 1;
            offset += textFeature.getNumberOfFeatures();
        }
    }

    private static void addFeature(String name, int start, int end, int index) {
        // Create feature
        Feature feature = new Feature();
        feature.name = name;
        feature.start = start;
        feature.end = end;
        // Add feature
        index2Feature.put(index, feature);
    }

    private static void writeHeader(AbstractDataSet testSet) {
        for (int i = 0; i < index2Feature.size(); i++) {
            evaluationWriter.write(index2Feature.get(i).name);
            evaluationWriter.write(",");
        }
        for (Author author : testSet.getAuthors()) {
            String name = author.toString();
            evaluationWriter.write(name + "_cluster,");
            evaluationWriter.write(name + "_f-measure,");
            evaluationWriter.write(name + "_precision,");
            evaluationWriter.write(name + "_recall,");
        }
        evaluationWriter.write("\n");
    }

    private static void writeResult(List<Integer> combination, List<EvaluationResult> resultList) {
        for (int i = 0; i < index2Feature.size(); i++) {
            if (combination.contains(i)) {
                evaluationWriter.write("1");
            } else {
                evaluationWriter.write("0");
            }
            evaluationWriter.write(",");
        }
        for (EvaluationResult result : resultList) {
            evaluationWriter.write(String.valueOf(result.getCluster()));
            evaluationWriter.write(",");
            evaluationWriter.write(String.valueOf(result.getFMeasure()));
            evaluationWriter.write(",");
            evaluationWriter.write(String.valueOf(result.getPrecision()));
            evaluationWriter.write(",");
            evaluationWriter.write(String.valueOf(result.getRecall()));
            evaluationWriter.write(",");
        }
        evaluationWriter.write("\n");
    }
}

class Feature {
    public String name;
    public Integer start;
    public Integer end;
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

    public void reset() {
        current = 0b1;
    }

    public String getBinaryString() {
        return Integer.toBinaryString(current);
    }

}
