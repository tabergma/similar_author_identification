package de.hpi.smm.features;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FunctionWordFeature {

	private String functionWordLocation = "../resource/FunctionWords.txt";
	
    private Map<String, Float> functionWordMap = new HashMap<String, Float>();
    private int wordCount = 0;


    public FunctionWordFeature() {
//        BufferedReader br = null;
//        try {
//        	br = new BufferedReader(new FileReader(functionWordLocation));
//            StringBuilder sb = new StringBuilder();
//            String line = br.readLine();
//
//            while (line != null) {
//            	functionWordMap.put(line, 0.f);
//                line = br.readLine();
//            }
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//        	try {
//        		br.close();
//        	}
//        	catch (IOException e) {
//        		e.printStackTrace();
//        	}
//        }
    	functionWordMap.put("a", 0.f);
    	functionWordMap.put("able", 0.f);
    	functionWordMap.put("aboard", 0.f);
    	functionWordMap.put("about", 0.f);
    	functionWordMap.put("above", 0.f);
    	functionWordMap.put("absent", 0.f);
    	functionWordMap.put("according", 0.f);
    	functionWordMap.put("accordingly", 0.f);
    	functionWordMap.put("across", 0.f);
    	functionWordMap.put("after", 0.f);
    	functionWordMap.put("against", 0.f);
    	functionWordMap.put("ahead", 0.f);
    	functionWordMap.put("albeit", 0.f);
    	functionWordMap.put("all", 0.f);
    	functionWordMap.put("along", 0.f);
    	functionWordMap.put("alongside", 0.f);
    	functionWordMap.put("although", 0.f);
    	functionWordMap.put("am", 0.f);
    	functionWordMap.put("amid", 0.f);
    	functionWordMap.put("amidst", 0.f);
    	functionWordMap.put("among", 0.f);
    	functionWordMap.put("amongst", 0.f);
    	functionWordMap.put("amount", 0.f);
    	functionWordMap.put("an", 0.f);
    	functionWordMap.put("and", 0.f);
    	functionWordMap.put("another", 0.f);
    	functionWordMap.put("anti", 0.f);
    	functionWordMap.put("any", 0.f);
    	functionWordMap.put("anybody", 0.f);
    	functionWordMap.put("anyone", 0.f);
    	functionWordMap.put("anything", 0.f);
    	functionWordMap.put("are", 0.f);
    	functionWordMap.put("around", 0.f);
    	functionWordMap.put("as", 0.f);
    	functionWordMap.put("aside", 0.f);
    	functionWordMap.put("astraddle", 0.f);
    	functionWordMap.put("astride", 0.f);
    	functionWordMap.put("at", 0.f);
    	functionWordMap.put("away", 0.f);
    	functionWordMap.put("bar", 0.f);
    	functionWordMap.put("barring", 0.f);
    	functionWordMap.put("be", 0.f);
    	functionWordMap.put("because", 0.f);
    	functionWordMap.put("been", 0.f);
    	functionWordMap.put("before", 0.f);
    	functionWordMap.put("behind", 0.f);
    	functionWordMap.put("being", 0.f);
    	functionWordMap.put("below", 0.f);
    	functionWordMap.put("beneath", 0.f);
    	functionWordMap.put("beside", 0.f);
    	functionWordMap.put("besides", 0.f);
    	functionWordMap.put("better", 0.f);
    	functionWordMap.put("between", 0.f);
    	functionWordMap.put("beyond", 0.f);
    	functionWordMap.put("bit", 0.f);
    	functionWordMap.put("both", 0.f);
    	functionWordMap.put("but", 0.f);
    	functionWordMap.put("by", 0.f);
    	functionWordMap.put("can", 0.f);
    	functionWordMap.put("certain", 0.f);
    	functionWordMap.put("circa", 0.f);
    	functionWordMap.put("close", 0.f);
    	functionWordMap.put("concerning", 0.f);
    	functionWordMap.put("consequently", 0.f);
    	functionWordMap.put("considering", 0.f);
    	functionWordMap.put("could", 0.f);
    	functionWordMap.put("couple", 0.f);
    	functionWordMap.put("dare", 0.f);
    	functionWordMap.put("deal", 0.f);
    	functionWordMap.put("despite", 0.f);
    	functionWordMap.put("down", 0.f);
    	functionWordMap.put("due", 0.f);
    	functionWordMap.put("during", 0.f);
    	functionWordMap.put("each", 0.f);
    	functionWordMap.put("eight", 0.f);
    	functionWordMap.put("eighth", 0.f);
    	functionWordMap.put("either", 0.f);
    	functionWordMap.put("enough", 0.f);
    	functionWordMap.put("every", 0.f);
    	functionWordMap.put("everybody", 0.f);
    	functionWordMap.put("everyone", 0.f);
    	functionWordMap.put("everything", 0.f);
    	functionWordMap.put("except", 0.f);
    	functionWordMap.put("excepting", 0.f);
    	functionWordMap.put("excluding", 0.f);
    	functionWordMap.put("failing", 0.f);
    	functionWordMap.put("few", 0.f);
    	functionWordMap.put("fewer", 0.f);
    	functionWordMap.put("fifth", 0.f);
    	functionWordMap.put("first", 0.f);
    	functionWordMap.put("five", 0.f);
    	functionWordMap.put("following", 0.f);
    	functionWordMap.put("for", 0.f);
    	functionWordMap.put("four", 0.f);
    	functionWordMap.put("fourth", 0.f);
    	functionWordMap.put("from", 0.f);
    	functionWordMap.put("front", 0.f);
    	functionWordMap.put("given", 0.f);
    	functionWordMap.put("good", 0.f);
    	functionWordMap.put("great", 0.f);
    	functionWordMap.put("had", 0.f);
    	functionWordMap.put("half", 0.f);
    	functionWordMap.put("have", 0.f);
    	functionWordMap.put("he", 0.f);
    	functionWordMap.put("heaps", 0.f);
    	functionWordMap.put("hence", 0.f);
    	functionWordMap.put("her", 0.f);
    	functionWordMap.put("hers", 0.f);
    	functionWordMap.put("herself", 0.f);
    	functionWordMap.put("him", 0.f);
    	functionWordMap.put("himself", 0.f);
    	functionWordMap.put("his", 0.f);
    	functionWordMap.put("however", 0.f);
    	functionWordMap.put("i", 0.f);
    	functionWordMap.put("if", 0.f);
    	functionWordMap.put("in", 0.f);
    	functionWordMap.put("including", 0.f);
    	functionWordMap.put("inside", 0.f);
    	functionWordMap.put("instead", 0.f);
    	functionWordMap.put("into", 0.f);
    	functionWordMap.put("is", 0.f);
    	functionWordMap.put("it", 0.f);
    	functionWordMap.put("its", 0.f);
    	functionWordMap.put("itself", 0.f);
    	functionWordMap.put("keeping", 0.f);
    	functionWordMap.put("lack", 0.f);
    	functionWordMap.put("less", 0.f);
    	functionWordMap.put("like", 0.f);
    	functionWordMap.put("little", 0.f);
    	functionWordMap.put("loads", 0.f);
    	functionWordMap.put("lots", 0.f);
    	functionWordMap.put("majority", 0.f);
    	functionWordMap.put("many", 0.f);
    	functionWordMap.put("masses", 0.f);
    	functionWordMap.put("may", 0.f);
    	functionWordMap.put("me", 0.f);
    	functionWordMap.put("might", 0.f);
    	functionWordMap.put("mine", 0.f);
    	functionWordMap.put("minority", 0.f);
    	functionWordMap.put("minus", 0.f);
    	functionWordMap.put("more", 0.f);
    	functionWordMap.put("most", 0.f);
    	functionWordMap.put("much", 0.f);
    	functionWordMap.put("must", 0.f);
    	functionWordMap.put("my", 0.f);
    	functionWordMap.put("myself", 0.f);
    	functionWordMap.put("near", 0.f);
    	functionWordMap.put("need", 0.f);
    	functionWordMap.put("neither", 0.f);
    	functionWordMap.put("nevertheless", 0.f);
    	functionWordMap.put("next", 0.f);
    	functionWordMap.put("nine", 0.f);
    	functionWordMap.put("ninth", 0.f);
    	functionWordMap.put("no", 0.f);
    	functionWordMap.put("nobody", 0.f);
    	functionWordMap.put("none", 0.f);
    	functionWordMap.put("nor", 0.f);
    	functionWordMap.put("nothing", 0.f);
    	functionWordMap.put("notwithstanding", 0.f);
    	functionWordMap.put("number", 0.f);
    	functionWordMap.put("numbers", 0.f);
    	functionWordMap.put("of", 0.f);
    	functionWordMap.put("off", 0.f);
    	functionWordMap.put("on", 0.f);
    	functionWordMap.put("once", 0.f);
    	functionWordMap.put("one", 0.f);
    	functionWordMap.put("onto", 0.f);
    	functionWordMap.put("opposite", 0.f);
    	functionWordMap.put("or", 0.f);
    	functionWordMap.put("other", 0.f);
    	functionWordMap.put("ought", 0.f);
    	functionWordMap.put("our", 0.f);
    	functionWordMap.put("ours", 0.f);
    	functionWordMap.put("ourselves", 0.f);
    	functionWordMap.put("out", 0.f);
    	functionWordMap.put("outside", 0.f);
    	functionWordMap.put("over", 0.f);
    	functionWordMap.put("part", 0.f);
    	functionWordMap.put("past", 0.f);
    	functionWordMap.put("pending", 0.f);
    	functionWordMap.put("per", 0.f);
    	functionWordMap.put("pertaining", 0.f);
    	functionWordMap.put("place", 0.f);
    	functionWordMap.put("plenty", 0.f);
    	functionWordMap.put("plethora", 0.f);
    	functionWordMap.put("plus", 0.f);
    	functionWordMap.put("quantities", 0.f);
    	functionWordMap.put("quantity", 0.f);
    	functionWordMap.put("quarter", 0.f);
    	functionWordMap.put("regarding", 0.f);
    	functionWordMap.put("remainder", 0.f);
    	functionWordMap.put("respecting", 0.f);
    	functionWordMap.put("rest", 0.f);
    	functionWordMap.put("round", 0.f);
    	functionWordMap.put("save", 0.f);
    	functionWordMap.put("saving", 0.f);
    	functionWordMap.put("second", 0.f);
    	functionWordMap.put("seven", 0.f);
    	functionWordMap.put("seventh", 0.f);
    	functionWordMap.put("several", 0.f);
    	functionWordMap.put("shall", 0.f);
    	functionWordMap.put("she", 0.f);
    	functionWordMap.put("should", 0.f);
    	functionWordMap.put("similar", 0.f);
    	functionWordMap.put("since", 0.f);
    	functionWordMap.put("six", 0.f);
    	functionWordMap.put("sixth", 0.f);
    	functionWordMap.put("so", 0.f);
    	functionWordMap.put("some", 0.f);
    	functionWordMap.put("somebody", 0.f);
    	functionWordMap.put("someone", 0.f);
    	functionWordMap.put("something", 0.f);
    	functionWordMap.put("spite", 0.f);
    	functionWordMap.put("such", 0.f);
    	functionWordMap.put("ten", 0.f);
    	functionWordMap.put("tenth", 0.f);
    	functionWordMap.put("than", 0.f);
    	functionWordMap.put("thanks", 0.f);
    	functionWordMap.put("that", 0.f);
    	functionWordMap.put("the", 0.f);
    	functionWordMap.put("their", 0.f);
    	functionWordMap.put("theirs", 0.f);
    	functionWordMap.put("them", 0.f);
    	functionWordMap.put("themselves", 0.f);
    	functionWordMap.put("then", 0.f);
    	functionWordMap.put("thence", 0.f);
    	functionWordMap.put("therefore", 0.f);
    	functionWordMap.put("these", 0.f);
    	functionWordMap.put("they", 0.f);
    	functionWordMap.put("third", 0.f);
    	functionWordMap.put("this", 0.f);
    	functionWordMap.put("those", 0.f);
    	functionWordMap.put("though", 0.f);
    	functionWordMap.put("three", 0.f);
    	functionWordMap.put("through", 0.f);
    	functionWordMap.put("throughout", 0.f);
    	functionWordMap.put("thru", 0.f);
    	functionWordMap.put("thus", 0.f);
    	functionWordMap.put("till", 0.f);
    	functionWordMap.put("time", 0.f);
    	functionWordMap.put("to", 0.f);
    	functionWordMap.put("tons", 0.f);
    	functionWordMap.put("top", 0.f);
    	functionWordMap.put("toward", 0.f);
    	functionWordMap.put("towards", 0.f);
    	functionWordMap.put("two", 0.f);
    	functionWordMap.put("under", 0.f);
    	functionWordMap.put("underneath", 0.f);
    	functionWordMap.put("unless", 0.f);
    	functionWordMap.put("unlike", 0.f);
    	functionWordMap.put("until", 0.f);
    	functionWordMap.put("unto", 0.f);
    	functionWordMap.put("up", 0.f);
    	functionWordMap.put("upon", 0.f);
    	functionWordMap.put("us", 0.f);
    	functionWordMap.put("used", 0.f);
    	functionWordMap.put("various", 0.f);
    	functionWordMap.put("versus", 0.f);
    	functionWordMap.put("via", 0.f);
    	functionWordMap.put("view", 0.f);
    	functionWordMap.put("wanting", 0.f);
    	functionWordMap.put("was", 0.f);
    	functionWordMap.put("we", 0.f);
    	functionWordMap.put("were", 0.f);
    	functionWordMap.put("what", 0.f);
    	functionWordMap.put("whatever", 0.f);
    	functionWordMap.put("when", 0.f);
    	functionWordMap.put("whenever", 0.f);
    	functionWordMap.put("where", 0.f);
    	functionWordMap.put("whereas", 0.f);
    	functionWordMap.put("wherever", 0.f);
    	functionWordMap.put("whether", 0.f);
    	functionWordMap.put("which", 0.f);
    	functionWordMap.put("whichever", 0.f);
    	functionWordMap.put("while", 0.f);
    	functionWordMap.put("whilst", 0.f);
    	functionWordMap.put("who", 0.f);
    	functionWordMap.put("whoever", 0.f);
    	functionWordMap.put("whole", 0.f);
    	functionWordMap.put("whom", 0.f);
    }

    public void evaluateFunctionWord(String text) {
        text = text.toLowerCase();
        if (functionWordMap.containsKey(text)) {
        	functionWordMap.put(text, functionWordMap.get(text) + 1.f);
        }
        wordCount ++;
    }

    public List<Float> getFunctionWordFrequency() {
        List<Float> functionWordFrequencyList = new ArrayList<Float>(this.functionWordMap.values());

        for(int i = 0; i < functionWordFrequencyList.size(); i++) {
        	functionWordFrequencyList.set(i, functionWordFrequencyList.get(i) / this.wordCount);
        }

        return functionWordFrequencyList;
    }
}
