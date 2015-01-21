package de.hpi.smm.features;

import de.hpi.smm.helper.MutableInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class  PunctuationFrequencyFeature extends AbstractTokenFeature {

    private Float[] features;
    private int letterCount = 0;

    private Map<Character, MutableInt> frequencies;

    private static List<Character> letters;


    public PunctuationFrequencyFeature(float weight) {
        super(weight);
        this.frequencies = new HashMap<Character, MutableInt>(getNumberOfFeatures());
        for (Character letter : getLetters()){
            frequencies.put(letter, new MutableInt());
        }
        this.features = new Float[getNumberOfFeatures()];
        this.letterCount = 0;
    }

    @Override
    public void feedToken(String token, String tag) {
        token = token.toUpperCase();
        for (char c : token.toCharArray()) {
            this.letterCount++;
            MutableInt count = frequencies.get(c);
            if (count != null){
                count.increment();
            }
        }
    }

    @Override
    public Float[] getFeatures() {
        for(int i = 0; i < features.length; i++) {
            features[i] = (float) frequencies.get(getLetters().get(i)).get() / this.letterCount;
        }
        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return getLetters().size();
    }

    @Override
    public String getName() {
        return "CharacterFrequencyFeature";
    }

    @Override
    public String getMaxName() {
        return "containing the most punctuation characters.";
    }

    @Override
    public String getMinName() {
        return "containing the least punctuation characters.";
    }

    @Override
    public String getHighName() {
        return "containing many punctuation characters.";
    }

    @Override
    public String getLowName() {
        return "containing few punctuation characters";
    }

    @Override
    public String getVeryHighName(){
    	return "containing very many punctuation characters.";
    }

    @Override
    public String getVeryLowName(){
    	return "containing very few punctuation characters.";
    }

    @Override
    public String getAverageName(){
    	return "containing an average amount of punctuation characters.";
    }

    @Override
    public String getMeaningfulName() {
        return "Frequency of certain characters.";
    }

    public static List<Character> getLetters() {
        if (letters == null){
            letters = new ArrayList<Character>();
//            letters.add('A');
//            letters.add('B');
//            letters.add('C');
//            letters.add('D');
//            letters.add('E');
//            letters.add('F');
//            letters.add('G');
//            letters.add('H');
//            letters.add('I');
//            letters.add('J');
//            letters.add('K');
//            letters.add('L');
//            letters.add('M');
//            letters.add('N');
//            letters.add('O');
//            letters.add('P');
//            letters.add('Q');
//            letters.add('R');
//            letters.add('S');
//            letters.add('T');
//            letters.add('U');
//            letters.add('V');
//            letters.add('W');
//            letters.add('X');
//            letters.add('Y');
//            letters.add('Z');
//            letters.add('0');
//            letters.add('1');
//            letters.add('2');
//            letters.add('3');
//            letters.add('4');
//            letters.add('5');
//            letters.add('6');
//            letters.add('7');
//            letters.add('8');
//            letters.add('9');
            letters.add(',');
            letters.add('.');
            letters.add(';');
            letters.add(':');
            letters.add('-');
//            letters.add('_');
//            letters.add('#');
//            letters.add('+');
//            letters.add('*');
            letters.add('?');
            letters.add('!');
//            letters.add('"');
//            letters.add('�');
//            letters.add('$');
//            letters.add('%');
            letters.add('&');
//            letters.add('/');
//            letters.add('\\');
//            letters.add('=');
            letters.add('(');
            letters.add(')');
            letters.add('\'');
//            letters.add('{');
//            letters.add('}');
//            letters.add('[');
//            letters.add(']');
//            letters.add('<');
//            letters.add('>');
//            letters.add('|');
//            letters.add('`');
//            letters.add('�');
//            letters.add('^');
//            letters.add('�');
//            letters.add('@');
        }
        return letters;
    }
}
