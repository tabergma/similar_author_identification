package de.hpi.smm.features;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import de.hpi.smm.helper.MutableInt;
import de.hpi.smm.helper.Util;


public class FunctionWordFeature extends AbstractFeature {

	private static String functionWordLocation = "../resource/FunctionWords.txt";
    private Map<String, MutableInt> frequencies = new HashMap<String, MutableInt>();
    private static List<String> functionWords;

    private float[] features;

    private int wordCount;

    public FunctionWordFeature () {
        this.frequencies = new HashMap<String, MutableInt>(getFunctionWords().size());
        for (String functionWord : getFunctionWords()){
            frequencies.put(functionWord, new MutableInt());
        }
        this.features = new float[getNumberOfFeatures()];
        this.wordCount = 0;
    }

    @Override
    public void feedToken(String token) {
        String lowerToken = token.toLowerCase();

        MutableInt count = frequencies.get(lowerToken);
        if (count != null){
            count.increment();
        }
        wordCount++;
    }

    @Override
    public float[] getFeatures() {
        for(int i = 0; i < features.length; i++) {
            features[i] = frequencies.get(getFunctionWords().get(i)).get() / this.wordCount;
        }
        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return getFunctionWords().size();
    }

    public static List<String> getFunctionWords() {
        if (functionWords == null){
            functionWords = new ArrayList<String>();

            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(functionWordLocation));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    functionWords.add(line);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return functionWords;
    }

}
