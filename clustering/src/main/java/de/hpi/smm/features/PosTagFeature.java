package de.hpi.smm.features;


import de.hpi.smm.helper.MutableInt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PosTagFeature extends AbstractTokenFeature {

    Map<String, MutableInt> tagCount = new HashMap<String, MutableInt>();
    int wordCount = 0;

    public PosTagFeature(float weight, List<String> t) {
        super(weight);
        for (String tag : t) {
            tagCount.put(tag, new MutableInt());
        }
    }

    @Override
    public void feedToken(String token, String tag) {
        MutableInt count = tagCount.get(tag);
        if (count != null) {
            count.increment();
            wordCount++;
        }
    }

    @Override
    public Float[] getFeatures() {
        Float[] features = new Float[tagCount.size()];
        int i = 0;
        for (MutableInt count : tagCount.values()) {
            if (count.get() != 0) {
                features[i] = (float) count.get() / wordCount;
            } else {
                features[i] = 0f;
            }
            i++;
        }
        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return tagCount.size();
    }

    @Override
    public String getName() {
        return "PosTagFeature";
    }

    @Override
    public String getMaxName() {
        return "with the highest frequency of POS tags.";
    }

    @Override
    public String getMinName() {
        return "with the lowest frequency of POS tags.";
    }

    @Override
    public String getHighName() {
        return "with a high frequency of POS tags.";
    }

    @Override
    public String getLowName() {
        return "with a low frequency of POS tags.";
    }

    @Override
    public String getVeryHighName(){
    	return "with a very high frequency of POS tags.";
    }

    @Override
    public String getVeryLowName(){
    	return "with a very low frequency of POS tags.";
    }

    @Override
    public String getAverageName(){
    	return "with an average frequency of POS tags.";
    }

    @Override
    public String getMeaningfulName() {
        return "Frequency of POS tags.";
    }
}