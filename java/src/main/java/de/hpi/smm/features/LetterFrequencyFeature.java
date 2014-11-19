package de.hpi.smm.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LetterFrequencyFeature {

    private Map<Character, Float> letterFrequencyMap = new HashMap<Character, Float>();
    private Float letterCount = 0.f;

    public LetterFrequencyFeature() {
        letterFrequencyMap.put('A', 0.f);
        letterFrequencyMap.put('B', 0.f);
        letterFrequencyMap.put('C', 0.f);
        letterFrequencyMap.put('D', 0.f);
        letterFrequencyMap.put('E', 0.f);
        letterFrequencyMap.put('F', 0.f);
        letterFrequencyMap.put('G', 0.f);
        letterFrequencyMap.put('H', 0.f);
        letterFrequencyMap.put('I', 0.f);
        letterFrequencyMap.put('J', 0.f);
        letterFrequencyMap.put('K', 0.f);
        letterFrequencyMap.put('L', 0.f);
        letterFrequencyMap.put('M', 0.f);
        letterFrequencyMap.put('N', 0.f);
        letterFrequencyMap.put('O', 0.f);
        letterFrequencyMap.put('P', 0.f);
        letterFrequencyMap.put('Q', 0.f);
        letterFrequencyMap.put('R', 0.f);
        letterFrequencyMap.put('S', 0.f);
        letterFrequencyMap.put('T', 0.f);
        letterFrequencyMap.put('U', 0.f);
        letterFrequencyMap.put('V', 0.f);
        letterFrequencyMap.put('W', 0.f);
        letterFrequencyMap.put('X', 0.f);
        letterFrequencyMap.put('Y', 0.f);
        letterFrequencyMap.put('Z', 0.f);
    }

    public void evaluateLetterFrequency(String text) {
        text = text.toUpperCase();
        for (char c : text.toCharArray())
            if (this.letterFrequencyMap.containsKey(c))
                this.letterFrequencyMap.put(c, this.letterFrequencyMap.get(c) + 1);
    }

    public List<Float> getLetterFrequency() {
        List<Float> letterFrequencyList = new ArrayList<Float>(this.letterFrequencyMap.values());

        for(int i = 0; i < letterFrequencyList.size(); i++) {
            letterFrequencyList.add(i, letterFrequencyList.get(i) / this.letterCount);
        }

        return letterFrequencyList;
    }
}
