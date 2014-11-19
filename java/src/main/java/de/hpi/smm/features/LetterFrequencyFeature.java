package de.hpi.smm.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LetterFrequencyFeature {

    Map<Character, Float> letterFrequency = new HashMap<Character, Float>();

    public LetterFrequencyFeature() {
        letterFrequency.put('A', 0.f);
        letterFrequency.put('B', 0.f);
        letterFrequency.put('C', 0.f);
        letterFrequency.put('D', 0.f);
        letterFrequency.put('E', 0.f);
        letterFrequency.put('F', 0.f);
        letterFrequency.put('G', 0.f);
        letterFrequency.put('H', 0.f);
        letterFrequency.put('I', 0.f);
        letterFrequency.put('J', 0.f);
        letterFrequency.put('K', 0.f);
        letterFrequency.put('L', 0.f);
        letterFrequency.put('M', 0.f);
        letterFrequency.put('N', 0.f);
        letterFrequency.put('O', 0.f);
        letterFrequency.put('P', 0.f);
        letterFrequency.put('Q', 0.f);
        letterFrequency.put('R', 0.f);
        letterFrequency.put('S', 0.f);
        letterFrequency.put('T', 0.f);
        letterFrequency.put('U', 0.f);
        letterFrequency.put('V', 0.f);
        letterFrequency.put('W', 0.f);
        letterFrequency.put('X', 0.f);
        letterFrequency.put('Y', 0.f);
        letterFrequency.put('Z', 0.f);
    }

    public void evaluateLetterFrequency(String text) {
        text = text.toUpperCase();
        for (char c : text.toCharArray())
            if (letterFrequency.containsKey(c))
                letterFrequency.put(c, letterFrequency.get(c) + 1);
    }

    public List<Float> getLetterFrequency() {
        return new ArrayList<Float>(letterFrequency.values());
    }
}
