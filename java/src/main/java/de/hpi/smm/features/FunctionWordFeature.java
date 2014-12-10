package de.hpi.smm.features;

import de.hpi.smm.Config;
import de.hpi.smm.helper.MutableInt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FunctionWordFeature extends AbstractTokenFeature {

    private Map<String, MutableInt> frequencies;
    private static List<String> functionWords;
    private Float[] features;
    private int wordCount;

    public FunctionWordFeature(float weight) {
        super(weight);
        this.frequencies = new HashMap<String, MutableInt>(getNumberOfFeatures());
        for (String functionWord : getFunctionWords()){
            frequencies.put(functionWord, new MutableInt());
        }
        this.features = new Float[getNumberOfFeatures()];
        this.wordCount = 0;
    }

    @Override
    public void feedToken(String token, String tag) {
        String lowerToken = token.toLowerCase();

        MutableInt count = frequencies.get(lowerToken);
        if (count != null){
            count.increment();
        }
        wordCount++;
    }

    @Override
    public Float[] getFeatures() {
        for(int i = 0; i < features.length; i++) {
            features[i] = (float) frequencies.get(getFunctionWords().get(i)).get() / this.wordCount;
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
                br = new BufferedReader(new FileReader(Config.FUNCTION_WORD_FILE));
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
