package de.hpi.smm.features;


public class WordLengthFeature extends AbstractTokenFeature {

    float wordCount = 0.f;
    float wordLength = 0.f;

    public WordLengthFeature(float weight) {
        super(weight);
    }

    @Override
    public void feedToken(String token, String tag) {
        this.wordCount += 1;
        this.wordLength += token.length();
    }

    @Override
    public Float[] getFeatures() {
        return new Float[]{ 1 - (1.f / (this.wordLength / this.wordCount)) };
    }

    @Override
    public int getNumberOfFeatures() {
        return 1;
    }

    @Override
    public String getName() {
        return "WordLengthFeature";
    }

    @Override
    public String getMaxName() {
        return "with the longest words.";
    }

    @Override
    public String getMinName() {
        return "with the shortest words.";
    }

    @Override
    public String getHighName() {
        return "with long words.";
    }

    @Override
    public String getLowName() {
        return "with short words.";
    }

    @Override
    public String getVeryHighName(){
    	return "with very long words.";
    }

    @Override
    public String getVeryLowName(){
    	return "with very short words.";
    }

    @Override
    public String getAverageName(){
    	return "with an average word length.";
    }

    @Override
    public String getMeaningfulName() {
        return "Length of words.";
    }

}
