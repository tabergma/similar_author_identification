package de.hpi.smm.features;


public class WordLengthFeature extends AbstractFeature {

    float wordCount = 0.f;
    float wordLength = 0.f;

    @Override
    public void feedToken(String token, String tag) {
        this.wordCount += 1;
        this.wordLength += token.length();
    }

    @Override
    public Float[] getFeatures() {
        return new Float[]{ this.wordLength / this.wordCount };
    }

    @Override
    public int getNumberOfFeatures() {
        return 1;
    }
}
