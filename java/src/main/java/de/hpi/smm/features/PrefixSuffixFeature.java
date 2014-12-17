package de.hpi.smm.features;


import de.hpi.smm.helper.MutableInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrefixSuffixFeature extends AbstractTokenFeature {

    private static List<String> prefixSuffix;
    private Float[] features;
    private Map<String, MutableInt> prefixFrequencies;
    private Map<String, MutableInt> suffixFrequencies;

    public PrefixSuffixFeature(float weight) {
        super(weight);
        this.prefixFrequencies = new HashMap<String, MutableInt>(getNumberOfFeatures());
        this.suffixFrequencies = new HashMap<String, MutableInt>(getNumberOfFeatures());
        for (String letter : getPrefixSuffix()){
            prefixFrequencies.put(letter, new MutableInt());
            suffixFrequencies.put(letter, new MutableInt());
        }
        this.features = new Float[getNumberOfFeatures()*2];
    }

    @Override
    public void feedToken(String token, String tag) {
        token = token.toUpperCase();

        int length = token.length();
        if (length > 2) {
            // Prefix
            String prefix = token.substring(0, 2);
            MutableInt prefixCount = prefixFrequencies.get(prefix);
            if (prefixCount != null){
            	prefixCount.increment();
            }

            // Suffix
            String suffix = token.substring(length - 2, length);
            MutableInt suffixCount = suffixFrequencies.get(suffix);
            if (suffixCount != null){
                suffixCount.increment();
            }
        }
    }

    @Override
    public Float[] getFeatures() {
        for(int i = 0; i < features.length / 2; i++) {
            int prefixCount = prefixFrequencies.get(getPrefixSuffix().get(i)).get();
            if (prefixCount != 0)
                features[i] = (float) 1 - 1.f / prefixCount;
            else
                features[i] = 0.f;
        }
        for(int i = features.length / 2; i < features.length; i++) {
            int suffixCount = suffixFrequencies.get(getPrefixSuffix().get(i)).get();
            if (suffixCount != 0)
                features[i] = (float) 1 - 1.f / suffixCount;
            else
                features[i] = 0.f;
        }
        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return getPrefixSuffix().size();
    }

    @Override
    public String getName() {
        return "PrefixSuffixFeature";
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
