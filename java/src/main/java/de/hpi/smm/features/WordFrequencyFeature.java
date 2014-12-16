package de.hpi.smm.features;

import de.hpi.smm.helper.MutableInt;

import java.util.HashMap;
import java.util.Map;


public class WordFrequencyFeature extends AbstractTokenFeature {

    private Map<String, MutableInt> frequencies = new HashMap<String, MutableInt>();
    private int wordCount = 0;

    public WordFrequencyFeature(float weight) {
        super(weight);
    }

    @Override
    public void feedToken(String token, String tag) {
        token = token.toLowerCase();
        MutableInt count = frequencies.get(token);
        if (count == null) {
            frequencies.put(token, new MutableInt());
        } else {
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

        // percentage of words that occur only once
        features[0] = (float) singleOccurringWordCount / frequencies.size();

        // average number of occurrences per word
        features[1] = (float) wordCount / frequencies.size();

        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return 2;
    }

    @Override
    public String getName() {
        return "WordFrequencyFeature";
    }

}
