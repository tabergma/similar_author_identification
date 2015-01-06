package de.hpi.smm;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Config {

    // cluster information
    public final static String CLUSTER_FILE = "../res/cluster.csv";

    // POS Tagger
    public final static String MODEL_DIR = "../res/stanford-postagger-full-2014-08-27/models/";
    public final static String ENGLISH_MODEL = "wsj-0-18-bidirectional-distsim.tagger";
    public final static String GERMAN_MODEL = "german-fast.tagger";
    //public final static String FRENCH_MODEL = "french.tagger";
    //public final static String CHINESE_MODEL = "chinese-distsim.tagger";
    //public final static String SPANISH_MODEL = "spanish-distsim.tagger";
    private final static MaxentTagger englishTagger = new MaxentTagger(MODEL_DIR + ENGLISH_MODEL);
    private final static MaxentTagger germanTagger = new MaxentTagger(MODEL_DIR + GERMAN_MODEL);
    //private final static MaxentTagger frenchTagger = new MaxentTagger(MODEL_DIR + FRENCH_MODEL);
    //private final static MaxentTagger chineseTagger = new MaxentTagger(MODEL_DIR + CHINESE_MODEL);
    //private final static MaxentTagger spanishTagger = new MaxentTagger(MODEL_DIR + SPANISH_MODEL);

    // Features
    public final static String FUNCTION_WORD_FILE = "../res/FunctionWords_de.txt";
    public final static String ABBREVIATIONS_FILE = "../res/Abbreviations_de.txt";

    // Language detector
    public final static String PROFILES_DIR = "resources/langdetect-03-03-2014/profiles/";

    // Data sets
    public static final String LOCAL_SETS_PATH = "../localDataSets/";
    public static final String LOCAL_GERMAN_SET_PATH = LOCAL_SETS_PATH + "german/";
    public static final String LOCAL_SPINNER_SET_PATH = LOCAL_SETS_PATH + "spinner/";
    public static final String LOCAL_SMM_SET_PATH = LOCAL_SETS_PATH + "smm/";

    public static final Map<String, MaxentTagger> lang2tagger;
    static {
        Map<String, MaxentTagger> aMap = new HashMap<String, MaxentTagger>();
        aMap.put("en", englishTagger);
        aMap.put("de", germanTagger);
        //aMap.put("fr", frenchTagger);
        //aMap.put("zh-cn", chineseTagger);
        //aMap.put("es", spanishTagger);

        lang2tagger = Collections.unmodifiableMap(aMap);
    }
}
