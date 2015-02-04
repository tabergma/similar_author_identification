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
    private final static boolean useAbbreviations = true;
    private String functionWordFile;
    private String abbreviationFile;

    public FunctionWordFeature(float weight, String functionWordFile, String abbreviationFile) {
        super(weight);
        this.functionWordFile = functionWordFile;
        this.abbreviationFile = abbreviationFile;

        this.frequencies = new HashMap<>(getNumberOfFeatures());
        for (String functionWord : getFunctionWords(functionWordFile, abbreviationFile)){
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
            features[i] = (float) frequencies.get(getFunctionWords(this.functionWordFile, this.abbreviationFile).get(i)).get() / this.wordCount;
        }
        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return getFunctionWords(this.functionWordFile, this.abbreviationFile).size();
    }

    @Override
    public String getName() {
        return "FunctionWordFeature";
    }

    @Override
    public String getMaxName() {
        return "with the most known function words.";
    }

    @Override
    public String getMinName() {
        return "with the fewest known function words.";
    }

    @Override
    public String getHighName() {
        return "with many known function words.";
    }

    @Override
    public String getLowName() {
        return "with few known function words.";
    }

    @Override
    public String getMeaningfulName() {
        return "Number of function words.";
    }

    @Override
    public String getVeryHighName(){
    	return "with very many known function words.";
    }

    @Override
    public String getVeryLowName(){
    	return "with very few known function words.";
    }

    @Override
    public String getAverageName(){
    	return "with an average amount of known function words.";
    }

    public static List<String> getFunctionWords(String functionWordFile, String abbreviationFile) {
        if (functionWords == null){
            functionWords = new ArrayList<String>();

            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(functionWordFile));
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
            
            if (useAbbreviations){
                BufferedReader br2 = null;
                try {
                    br2 = new BufferedReader(new FileReader(Config.ABBREVIATIONS_FILE));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = br2.readLine()) != null) {
                        functionWords.add(line);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        br2.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return functionWords;
    }

}
