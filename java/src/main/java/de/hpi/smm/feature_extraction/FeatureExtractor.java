package de.hpi.smm.feature_extraction;

import java.util.ArrayList;
import java.util.List;

public class FeatureExtractor {

    Boolean wordLengthFeature = true;

    public List<Float> getFeatures(String text) {
        ArrayList<Float> features = new ArrayList<Float>();
        String[] tokens = text.split("\\s");

        Float wordCount = 0.f;
        Float wordLength = 0.f;

        for (String token: tokens) {
            wordCount += 1;
            wordLength += token.length();
        }

        if (wordLengthFeature)
            features.add(wordLength / wordCount);

        return features;
    }

}
