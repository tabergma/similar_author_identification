package de.hpi.smm.features;


import java.util.ArrayList;
import java.util.List;

public class EmoticonFeature extends AbstractTokenFeature {

    private Float[] features = new Float[] {0f};
    private static List<String> emoticons = getEmoticons();

    public EmoticonFeature(float weight) {
        super(weight);
    }

    @Override
    public void feedToken(String token, String tag) {
        if (emoticons.contains(token)) {
            features[0] = 1f;
        }
    }

    @Override
    public Float[] getFeatures() {
        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return getEmoticons().size();
    }

    @Override
    public String getName() {
        return "EmoticonFeature";
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
