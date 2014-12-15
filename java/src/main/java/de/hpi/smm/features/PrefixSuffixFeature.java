package de.hpi.smm.features;


import de.hpi.smm.helper.MutableInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrefixSuffixFeature extends AbstractTokenFeature {

    private static List<String> prefixSuffix;
    private Float[] features;
    private Map<String, MutableInt> frequencies;

    public PrefixSuffixFeature(float weight) {
        super(weight);
        this.frequencies = new HashMap<String, MutableInt>(getNumberOfFeatures());
        for (String letter : getPrefixSuffix()){
            frequencies.put(letter, new MutableInt());
        }
        this.features = new Float[getNumberOfFeatures()];
    }

    @Override
    public void feedToken(String token, String tag) {
        token = token.toUpperCase();

        int length = token.length();
        if (length > 2) {
            // Prefix
            String prefix = token.substring(0, 2);
            MutableInt count = frequencies.get(prefix);
            if (count != null){
                count.increment();
            }

            // Suffix
            String suffix = token.substring(length - 2, length);
            count = frequencies.get(suffix);
            if (count != null){
                count.increment();
            }
        }
    }

    @Override
    public Float[] getFeatures() {
        for(int i = 0; i < features.length; i++) {
            int count = frequencies.get(getPrefixSuffix().get(i)).get();
            if (count != 0)
                features[i] = (float) 1 - 1.f / count;
            else
                features[i] = 0.f;
        }
        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return getPrefixSuffix().size();
    }

    public static List<String> getPrefixSuffix() {
        if (prefixSuffix == null) {
            // List of all characters
            List<String> letters = new ArrayList<String>();
            letters.add("A");
            letters.add("B");
            letters.add("C");
            letters.add("D");
            letters.add("E");
            letters.add("F");
            letters.add("G");
            letters.add("H");
            letters.add("I");
            letters.add("J");
            letters.add("K");
            letters.add("L");
            letters.add("M");
            letters.add("N");
            letters.add("O");
            letters.add("P");
            letters.add("Q");
            letters.add("R");
            letters.add("S");
            letters.add("T");
            letters.add("U");
            letters.add("V");
            letters.add("W");
            letters.add("X");
            letters.add("Y");
            letters.add("Z");
            // Build all combinations of letter of size 2
            prefixSuffix = new ArrayList<String>();
            for (String a : letters) {
                for (String b : letters) {
                    if (!a.equals(b))
                        prefixSuffix.add(a + b);
                }
            }
        }

        return prefixSuffix;
    }
}
