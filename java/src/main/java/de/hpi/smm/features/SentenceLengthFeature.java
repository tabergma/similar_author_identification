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
        return "the longest sentences.";
    }

    @Override
    public String getMinName() {
        return "the shortest sentences.";
    }

    @Override
    public String getHighName() {
        return "long sentences.";
    }

    @Override
    public String getLowName() {
        return "short sentences.";
    }

    @Override
    public String getMeaningfulName() {
        return "Length of sentences.";
    }
}
