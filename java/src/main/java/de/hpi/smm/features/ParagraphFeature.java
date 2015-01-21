package de.hpi.smm.features;

public class ParagraphFeature extends AbstractTextFeature {

    Float[] features = new Float[1];

    public ParagraphFeature(float weight) {
        super(weight);
    }

    @Override
    public void feedText(String text) {
        // TODO: ADAPT FOR HANA DATA SET
        int paragraphLength = 0;
        int paragraphCount = 0;

        if (text.contains("\\n\\n")) {
            String[] paragraphs = text.split("\\\\n\\\\n");
            paragraphCount = paragraphs.length;

            for (String paragraph : paragraphs) {
                paragraphLength += paragraph.length();
            }

        }

        // Average blank lines
        if (paragraphLength != 0)
            features[0] =  (1 - (1 / ((float) paragraphLength / (float) paragraphCount)));
        else
            features[0] = 0f;
    }

    @Override
    public Float[] getFeatures() {
        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return 0;
    }

    @Override
    public String getName() {
        return "ParagraphFeature";
    }

    @Override
    public String getMaxName() {
        return "with the longest paragraphs.";
    }

    @Override
    public String getMinName() {
        return "with the smallest paragraphs.";
    }

    @Override
    public String getHighName() {
        return "with long paragraphs.";
    }

    @Override
    public String getLowName() {
        return "with small paragraphs.";
    }
    
    public String getVeryHighName(){
    	return "with very long paragraphs.";
    }
    
    public String getVeryLowName(){
    	return "with very short paragraphs.";
    }
    
    public String getAverageName(){
    	return "with average paragraph length.";
    }

    @Override
    public String getMeaningfulName() {
        return "Length of paragraphs.";
    }
}
