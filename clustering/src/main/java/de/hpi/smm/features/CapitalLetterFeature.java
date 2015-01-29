package de.hpi.smm.features;

public class CapitalLetterFeature extends AbstractTokenFeature {

    private int capitalLetterCount = 0;
    private int letterCount = 0;

    public CapitalLetterFeature(float weight) {
        super(weight);
    }

    @Override
    public void feedToken(String token, String tag) {
        for (char c : token.toCharArray()) {
            this.letterCount += 1;
            if(Character.isUpperCase(c)){
                capitalLetterCount += 1;
            }
        }
    }

    @Override
    public Float[] getFeatures() {
        Float[] features = new Float[1];

        // Number of capitalized letters
        if (capitalLetterCount != 0)
            features[0] = (float) capitalLetterCount / letterCount;
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
        return "CapitalLetterFeature";
    }

    @Override
    public String getMaxName() {
        return "with the most capital letters.";
    }

    @Override
    public String getMinName() {
        return "with the fewest capital letters.";
    }

    @Override
    public String getHighName() {
        return "with many capital letters.";
    }

    @Override
    public String getLowName() {
        return "with few capital letters.";
    }

    @Override
    public String getMeaningfulName() {
        return "Number of capital letters.";
    }

    @Override
    public String getVeryHighName(){
    	return "with very many capital lettes";
    }

    @Override
    public String getVeryLowName(){
    	return "with very few capital letters";
    }

    @Override
    public String getAverageName(){
    	return "with an average amount of capital letters";
    }
}