package de.hpi.smm.features;


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

    @Override
    public String getName() {
        return "PostLengthFeature";
    }

    @Override
    public String getMaxName() {
        return "with the longest posts.";
    }

    @Override
    public String getMinName() {
        return "with the shortest posts.";
    }

    @Override
    public String getHighName() {
        return "with long posts.";
    }

    @Override
    public String getLowName() {
        return "with short posts.";
    }

    @Override
    public String getMeaningfulName() {
        return "Length of blog post.";
    }
}
