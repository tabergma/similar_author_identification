package de.hpi.smm.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeatureExtractor {

    public List<Float> getFeatures(String text) {
        // Initialize features
        List<AbstractFeature> featureList = new ArrayList<AbstractFeature>();
        addAllFeatures(featureList);

        // Tokenize text
        String[] tokens = text.split("\\s");
        for (String token: tokens) {
            for (AbstractFeature feature : featureList){
                feature.feedToken(token);
            }
        }

        // Add features to feature list
        ArrayList<Float> featureValues = new ArrayList<Float>();
        for (AbstractFeature feature : featureList){
            featureValues.addAll(Arrays.asList(feature.getFeatures()));
        }

        return featureValues;
    }

    public static void addAllFeatures(List<AbstractFeature> featureList){
        featureList.add(new WordLengthFeature());
        featureList.add(new LetterFrequencyFeature());
        featureList.add(new FunctionWordFeature());
        featureList.add(new CapitalLetterFrequencyFeature());
        featureList.add(new WordFrequencyFeature());
    }
}
