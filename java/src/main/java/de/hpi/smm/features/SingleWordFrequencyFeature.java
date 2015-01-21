package de.hpi.smm.features;

import de.hpi.smm.helper.MutableInt;

import java.util.HashMap;
import java.util.Map;


public class SingleWordFrequencyFeature extends AbstractTokenFeature {

    private Map<String, MutableInt> frequencies = new HashMap<String, MutableInt>();

    public SingleWordFrequencyFeature(float weight) {
        super(weight);
    }

    @Override
    public void feedToken(String token, String tag) {
        token = token.toLowerCase();
        MutableInt count = frequencies.get(token);
        if (count == null) {
            frequencies.put(token, new MutableInt());
        } else {
            count.increment();
        }
    }

    @Override
    public Float[] getFeatures() {
        Float[] features = new Float[1];

        int singleOccurringWordCount = 0;
        for (MutableInt count : frequencies.values()) {
            if (count.get() == 1){
                singleOccurringWordCount++;
            }
        }

        // percentage of words that occur only once
        features[0] = (float) singleOccurringWordCount / frequencies.size();

        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return 1;
    }

    @Override
    public String getName() {
        return "SingleWordFrequencyFeature";
    }

    @Override
    public String getMaxName() {
        return "containing the most words that occur only once.";
    }

    @Override
    public String getMinName() {
        return "containing the fewest words that occur only once.";
    }

    @Override
    public String getHighName() {
        return "containing many words that occur only once.";
    }

    @Override
    public String getLowName() {
        return "containing few words that occur only once.";
    }
    
    public String getVeryHighName(){
    	return "containing very many words that occur only once.";
    }
    
    public String getVeryLowName(){
    	return "containing very few words that occur only once.";
    }
    
    public String getAverageName(){
    	return "containing an average amount of words that occur only once.";
    }

    @Override
    public String getMeaningfulName() {
        return "Number of words that occur only once.";
    }

}
