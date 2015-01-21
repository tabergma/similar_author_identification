package de.hpi.smm.features;

import java.util.ArrayList;
import java.util.List;


public class  NumberFrequencyFeature extends AbstractTokenFeature {

    private Float[] features;
    private int letterCount = 0;

    private int numberCount = 0;

    private static List<Character> numberCharacters;


    public NumberFrequencyFeature(float weight) {
        super(weight);
        getNumberCharacters();
        
        this.features = new Float[getNumberOfFeatures()];
        this.letterCount = 0;
    }

    @Override
    public void feedToken(String token, String tag) {
        for (char c : token.toCharArray()) {
            this.letterCount++;
            if (numberCharacters.contains(c)){
                numberCount++;
            }
        }
    }

    @Override
    public Float[] getFeatures() {
        features[0] = (float) numberCount / (float) letterCount;
        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return 1;
    }

    @Override
    public String getName() {
        return "CharacterFrequencyFeature";
    }

    @Override
    public String getMaxName() {
        return "with the most number characters.";
    }

    @Override
    public String getMinName() {
        return "with the least number characters.";
    }

    @Override
    public String getHighName() {
        return "with many number characters.";
    }

    @Override
    public String getLowName() {
        return "with few number characters.";
    }
    
    public String getVeryHighName(){
    	return "with very many number characters.";
    }
    
    public String getVeryLowName(){
    	return "with very few number characters.";
    }
    
    public String getAverageName(){
    	return "with an average amount of number characters.";
    }

    @Override
    public String getMeaningfulName() {
        return "Frequency of certain characters.";
    }

    public static List<Character> getNumberCharacters() {
        if (numberCharacters == null){
            numberCharacters = new ArrayList<Character>();
            numberCharacters.add('0');
            numberCharacters.add('1');
            numberCharacters.add('2');
            numberCharacters.add('3');
            numberCharacters.add('4');
            numberCharacters.add('5');
            numberCharacters.add('6');
            numberCharacters.add('7');
            numberCharacters.add('8');
            numberCharacters.add('9');
        }
        return numberCharacters;
    }
}
