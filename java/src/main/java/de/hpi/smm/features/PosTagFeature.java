package de.hpi.smm.features;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PosTagFeature extends AbstractFeature {

    Map<String, Integer> tagCount = new HashMap<String, Integer>();
    int wordCount = 0;

    public PosTagFeature(List<String> t) {
        for (String tag : t) {
            tagCount.put(tag, 0);
        }
    }

    @Override
    public void feedToken(String token, String tag) {
        tagCount.put(tag, tagCount.get(tag) + 1);
        wordCount++;
    }

    @Override
    public Float[] getFeatures() {
        Float[] features = new Float[tagCount.size()];
        int i = 0;
        for (Integer count : tagCount.values()) {
            if (count != 0) {
                features[i] = (float) count / wordCount;
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
}
