package de.hpi.smm.features;

public class BlankLineFeature extends AbstractTextFeature {

    Float[] features = new Float[1];

    public BlankLineFeature(float weight) {
        super(weight);
    }

    @Override
    public void feedText(String text) {
        // TODO: ADAPT FOR HANA DATA SET
        int blankLinesCount = 0;

        if (text.startsWith("\\n"))
            blankLinesCount++;

        if (text.contains("\\n\\n")) {
            String[] paragraphs = text.split("\\\\n\\\\n");
            blankLinesCount += paragraphs.length - 1;
        }

        // Number of blank lines
        if (blankLinesCount != 0)
            features[0] =  1 - (1f / (float) blankLinesCount);
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
        return "BlankLineFeature";
    }

    @Override
    public String getMaxName() {
        return "the most blank lines.";
    }

    @Override
    public String getMinName() {
        return "the fewest blank lines.";
    }

    @Override
    public String getHighName() {
        return "with many blank lines.";
    }
    
    public String getVeryHighName(){
    	return "with very many blank lines.";
    }
    
    public String getVeryLowName(){
    	return "with very few blank lines.";
    }
    
    public String getAverageName(){
    	return "with an average amount of blank lines.";
    }

    @Override
    public String getLowName() {
        return "with only a few blank lines.";
    }

    @Override
    public String getMeaningfulName() {
        return "Number of blank lines.";
    }

}
