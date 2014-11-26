package de.hpi.smm.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CapitalLetterFrequencyFeature {

    private Float capitalLetterCount = 0.f;
    private Float letterCount = 0.f;

    public CapitalLetterFrequencyFeature() {
    }

    public void evaluateCapitalLetterFrequency(String text) {
    	
        for (char c : text.toCharArray()) {
            this.letterCount += 1.f;
            if(Character.isUpperCase(c)){
        		capitalLetterCount += 1.f;
            }
        }
    }

    public Float getCapitalLetterFrequency() {
    	Float capitalLetterFrequency = capitalLetterCount / letterCount;
        return capitalLetterFrequency;
    }
}
