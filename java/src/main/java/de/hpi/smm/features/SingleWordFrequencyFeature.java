package de.hpi.smm.features;

import de.hpi.smm.helper.MutableInt;

import java.util.HashMap;
import java.util.Map;


public class SingleWordFrequencyFeature extends AbstractTokenFeature {

    private Map<String, MutableInt> frequencies = new HashMap<String, MutableInt>();

    public SingleWordFrequencyFeature(float weight) {
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
    }

    @Override
    public Float[] getFeatures() {
        Float[] features = new Float[1];

        int singleOccurringWordCount = 0;
        for (MutableInt count : frequencies.values()) {
            if (count.get() == 1){
                singleOccurringWordCount++;
            }
        }

        // percentage of words that occur only once
        features[0] = (float) singleOccurringWordCount / frequencies.size();

        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return 1;
    }

    @Override
    public String getName() {
        return "SingleWordFrequencyFeature";
    }

    @Override
    public String getMaxName() {
        return "the highest number of words that occur only once.";
    }

    @Override
    public String getMinName() {
        return "the lowest number of words that occur only once.";
    }

    @Override
    public String getHighName() {
        return "a lot words that occur only once.";
    }

    @Override
    public String getLowName() {
        return "only a few words that occur only once.";
    }

    @Override
    public String getMeaningfulName() {
        return "Number of words that occur only once.";
    }

}
