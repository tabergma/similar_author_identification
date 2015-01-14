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
        Float[] features = new Float[1];

        // average number of occurrences per word
        features[0] = (float) frequencies.size() / (float) wordCount;

        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return 1;
    }

    @Override
    public String getName() {
        return "WordFrequencyFeature";
    }

    @Override
    public String getMaxName() {
        return "the highest average number of occurrences per word.";
    }

    @Override
    public String getMinName() {
        return "the lowest average number of occurrences per word.";
    }

    @Override
    public String getHighName() {
        return "a high average number of occurrences per word.";
    }

    @Override
    public String getLowName() {
        return "a low average number of occurrences per word.";
    }

    @Override
    public String getMeaningfulName() {
        return "Frequency of words.";
    }

}
