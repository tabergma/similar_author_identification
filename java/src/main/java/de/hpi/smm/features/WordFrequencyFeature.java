package de.hpi.smm.features;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WordFrequencyFeature {

	private String functionWordLocation = "../resource/FunctionWords.txt";
	
    private Map<String, Float> functionWordMap = new HashMap<String, Float>();
    private float wordCount = 0.f;


    public WordFrequencyFeature() {

    }

    public void evaluateWordFrequency(String text) {
        text = text.toLowerCase();
        if (functionWordMap.containsKey(text)) {
        	functionWordMap.put(text, functionWordMap.get(text) + 1.f);
        }
        else {
        	functionWordMap.put(text, 1.f);
        }
        wordCount ++;
    }

    public List<Float> getWordFrequency() {
        List<Float> wordFrequencyList = new ArrayList<Float>();
        
        //percentage of words that occur only once
        float singleOccurringWordCount = 0.f;
        for (float count : functionWordMap.values()) {
        	if (count == 1.f){
        		singleOccurringWordCount ++;
        	}
        }
        wordFrequencyList.add(singleOccurringWordCount / functionWordMap.size());
        
        //average number of occurrences per word
        wordFrequencyList.add(wordCount / functionWordMap.size());
        

        return wordFrequencyList;
    }
}
