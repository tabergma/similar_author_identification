package de.hpi.smm.features;

import org.apache.commons.lang3.StringUtils;

public class UpperCaseFeature extends AbstractTokenFeature {

    private int upperCaseWordCount = 0;
    private int wordCount = 0;

    public UpperCaseFeature(float weigth) {
        super(weigth);
    }

    @Override
    public void feedToken(String token, String tag) {
        wordCount++;
        if (StringUtils.isAllUpperCase(token)) {
            upperCaseWordCount++;
        }
    }

    @Override
    public Float[] getFeatures() {
        Float[] features = new Float[1];

        // Number of upper case words
        if (upperCaseWordCount != 0)
            features[0] = (float) upperCaseWordCount / wordCount;
        else
            features[0] = 0f;

        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return 1;
    }

    @Override
    public String getName() {
        return "UpperCaseFeature";
    }

    @Override
    public String getMaxName() {
        return "with the most upper case words.";
    }

    @Override
    public String getMinName() {
        return "with the least upper case words.";
    }

    @Override
    public String getHighName() {
        return "with many upper case words.";
    }

    @Override
    public String getLowName() {
        return "with few upper case words.";
    }
    
    public String getVeryHighName(){
    	return "with very many upper case words.";
    }
    
    public String getVeryLowName(){
    	return "with very few upper case words.";
    }
    
    public String getAverageName(){
    	return "with an average amount of upper case words.";
    }

    @Override
    public String getMeaningfulName() {
        return "Number of upper case words.";
    }

}
