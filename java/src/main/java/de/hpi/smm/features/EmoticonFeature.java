package de.hpi.smm.features;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmoticonFeature extends AbstractTokenFeature {

    private Float[] features;
    private static List<String> emoticons;
    private Map<String, Integer> occurences;

    public EmoticonFeature(float weight) {
        super(weight);
        emoticons = getEmoticons();

        this.occurences = new HashMap<String, Integer>(getNumberOfFeatures());
        for (String emoticon : emoticons){
            this.occurences.put(emoticon, 0);
        }
        this.features = new Float[getNumberOfFeatures()];
    }

    @Override
    public void feedToken(String token, String tag) {
        if (emoticons.contains(token))
            this.occurences.put(token, 1);
    }

    @Override
    public Float[] getFeatures() {
        for(int i = 0; i < features.length; i++) {
            features[i] = (float) this.occurences.get(emoticons.get(i));
        }
        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return emoticons.size();
    }

    @Override
    public String getName() {
        return "EmoticonFeature";
    }

    @Override
    public String getMaxName() {
        return "with emoticons.";
    }

    @Override
    public String getMinName() {
        return "without emoticons.";
    }

    @Override
    public String getHighName() {
        return "with emoticons.";
    }

    @Override
    public String getLowName() {
        return "without emoticons.";
    }

    @Override
    public String getMeaningfulName() {
        return "Usage of emoticons.";
    }

    public static List<String> getEmoticons() {
        if (emoticons == null){
            emoticons = new ArrayList<String>();
            emoticons.add(":-)");
            emoticons.add(";-)");
            emoticons.add(";)");
            emoticons.add(":)");
            emoticons.add(":*");
            emoticons.add("<3");
            emoticons.add(":D");
            emoticons.add("^^");
            emoticons.add(":/");
        }
        return emoticons;
    }
}
