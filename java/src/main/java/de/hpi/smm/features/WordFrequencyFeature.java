package de.hpi.smm.features;

import de.hpi.smm.helper.MutableInt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WordFrequencyFeature extends AbstractFeature {

	private String functionWordLocation = "../resource/FunctionWords.txt";

    private Map<String, MutableInt> frequencies = new HashMap<String, MutableInt>();

    private int wordCount = 0;


    public WordFrequencyFeature() {
    }

    @Override
    public void feedToken(String token) {
        token = token.toLowerCase();
        MutableInt count = frequencies.get(token);
        if (count == null) {
            frequencies.put(token, new MutableInt());
        }
        else {
            count.increment();
        }
        wordCount++;
    }

    @Override
    public Float[] getFeatures() {
        Float[] features = new Float[2];

        int singleOccurringWordCount = 0;
        for (MutableInt count : frequencies.values()) {
            if (count.get() == 1){
                singleOccurringWordCount++;
            }
        }

        //percentage of words that occur only once
        features[0] = (float) singleOccurringWordCount / frequencies.size();

        //average number of occurrences per word
        features[1] = (float) wordCount / frequencies.size();

        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return 2;
    }
}
