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

}
