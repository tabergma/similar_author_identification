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
        return "with the least diverse vocabulary.";
    }

    @Override
    public String getMinName() {
        return "with the most diverse vocabulary.";
    }

    @Override
    public String getHighName() {
        return "with a small vocabulary diversity.";
    }

    @Override
    public String getLowName() {
        return "with a very diverse vocabulary.";
    }

    @Override
    public String getMeaningfulName() {
        return "Frequency of words.";
    }

}
