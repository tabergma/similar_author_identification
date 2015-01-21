package de.hpi.smm.features;


public class SentenceLengthFeature extends AbstractTextFeature {

    Float[] features = new Float[1];

    public SentenceLengthFeature(float weight) {
        super(weight);
    }

    @Override
    public void feedText(String text) {
        int sentenceLength = 0;
        int sentenceCount = 0;

        String[] sentences = text.split("\\p{Punct}\\s[A-Z]");
        sentenceCount = sentences.length;

        for (String sentence : sentences) {
            sentenceLength += sentence.length() + 2;
        }

        // Average blank lines
        if (sentenceLength != 0)
            features[0] =  1 - 1 / (float) sentenceLength / (float) sentenceCount;
        else
            features[0] = 0f;
    }

    @Override
    public Float[] getFeatures() {
        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return 1;
    }

    @Override
    public String getName() {
        return "SentenceLengthFeature";
    }

    @Override
    public String getMaxName() {
        return "with the longest sentences.";
    }

    @Override
    public String getMinName() {
        return "with the shortest sentences.";
    }

    @Override
    public String getHighName() {
        return "with long sentences.";
    }

    @Override
    public String getLowName() {
        return "with short sentences.";
    }

    public String getVeryHighName(){
    	return "with very long sentences.";
    }
    
    public String getVeryLowName(){
    	return "with very short sentences.";
    }
    
    public String getAverageName(){
    	return "with average sentence length.";
    }
    
    @Override
    public String getMeaningfulName() {
        return "Length of sentences.";
    }
}
