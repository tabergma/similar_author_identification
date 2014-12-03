package de.hpi.smm.features;


import de.hpi.smm.helper.MutableInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmoticonFeature extends AbstractFeature {

    private Float[] features;
    private int emoticonsCount = 0;
    private Map<String, MutableInt> frequencies;
    private static List<String> emoticons;

    public EmoticonFeature(float weight) {
        super(weight);
        
        this.frequencies = new HashMap<String, MutableInt>(getNumberOfFeatures());
        for (String emoticon : getEmoticons()){
            frequencies.put(emoticon, new MutableInt());
        }
        this.features = new Float[getNumberOfFeatures()];
    }

    @Override
    public void feedToken(String token, String tag) {
        MutableInt count = frequencies.get(token);
        if (count != null){
            count.increment();
            emoticonsCount++;
        }
    }

    @Override
    public Float[] getFeatures() {
        for(int i = 0; i < features.length; i++) {
            int count = frequencies.get(getEmoticons().get(i)).get();
            if (count != 0)
                features[i] = (float) count / this.emoticonsCount;
            else
                features[i] = 0f;
        }
        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return getEmoticons().size();
    }

    public static List<String> getEmoticons() {
        if (emoticons == null){
            emoticons = new ArrayList<String>();
            emoticons.add(":-)");
            emoticons.add(";-)");
        }
        return emoticons;
    }
}
