package de.hpi.smm.features;


public class PostLengthFeature extends AbstractFeature {

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
        return new Float[] {1f / count};
    }

    @Override
    public int getNumberOfFeatures() {
        return 1;
    }
}
