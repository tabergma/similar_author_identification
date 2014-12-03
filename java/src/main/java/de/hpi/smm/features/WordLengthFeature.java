package de.hpi.smm.features;


public class WordLengthFeature extends AbstractFeature {

    float wordCount = 0.f;
    float wordLength = 0.f;

    @Override
    public void feedToken(String token) {
        this.wordCount += 1;
        this.wordLength += token.length();
    }

    @Override
    public float[] getFeatures() {
        float[] feature = { this.wordLength / this.wordCount };
        return feature;
    }

    @Override
    public int getNumberOfFeatures() {
        return 1;
    }
}
