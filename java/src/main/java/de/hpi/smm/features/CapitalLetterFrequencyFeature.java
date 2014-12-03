package de.hpi.smm.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CapitalLetterFrequencyFeature extends AbstractFeature {

    private int capitalLetterCount = 0;
    private int letterCount = 0;

    public CapitalLetterFrequencyFeature() {
    }

    @Override
    public void feedToken(String token) {
        for (char c : token.toCharArray()) {
            this.letterCount += 1;
            if(Character.isUpperCase(c)){
                capitalLetterCount += 1;
            }
        }
    }

    @Override
    public Float[] getFeatures() {
        Float[] capitalLetterFrequency = {(float) capitalLetterCount / letterCount};
        return capitalLetterFrequency;
    }

    @Override
    public int getNumberOfFeatures() {
        return 1;
    }
}
