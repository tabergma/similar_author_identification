package de.hpi.smm.features;

import java.util.ArrayList;
import java.util.List;

import de.hpi.smm.features.CapitalLetterFrequencyFeature;

public class FeatureExtractor {

    public List<Float> getFeatures(String text) {
        // Initialize features
        WordLengthFeature wordLengthFeature = new WordLengthFeature();
        LetterFrequencyFeature letterFrequencyFeature = new LetterFrequencyFeature();
        FunctionWordFeature functionWordFeature = new FunctionWordFeature();
        CapitalLetterFrequencyFeature capitalLetterFrequencyFeature = new CapitalLetterFrequencyFeature();

        // Tokenize text
        String[] tokens = text.split("\\s");
        for (String token: tokens) {
            wordLengthFeature.addWordCount(1);
            wordLengthFeature.addWordLength(token.length());

            letterFrequencyFeature.evaluateLetterFrequency(token);
            functionWordFeature.evaluateFunctionWord(token);
            capitalLetterFrequencyFeature.evaluateCapitalLetterFrequency(token);
        }

        // Add features to feature list
        ArrayList<Float> features = new ArrayList<Float>();
        features.add(wordLengthFeature.getAvgWordLength());
        features.addAll(letterFrequencyFeature.getLetterFrequency());
        features.addAll(functionWordFeature.getFunctionWordFrequency());
        features.add(capitalLetterFrequencyFeature.getCapitalLetterFrequency());

        return features;
    }
}
