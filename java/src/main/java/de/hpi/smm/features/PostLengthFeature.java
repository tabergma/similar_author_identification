package de.hpi.smm.features;


import java.util.List;

public class PostLengthFeature extends AbstractTokenFeature {

    int count = 0;

    public PostLengthFeature(float weight) {
        super(weight);
    }

    @Override
    public void feedToken(String token, String tag) {
        count += token.length();
    }

    @Override
    public Float[] getFeatures() {
        return new Float[] {1 - (1f / count)};
    }

    @Override
    public int getNumberOfFeatures() {
        return 1;
    }
}
