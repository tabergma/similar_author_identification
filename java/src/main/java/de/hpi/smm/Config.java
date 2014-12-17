package de.hpi.smm;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Config {

    // Directories for K-Means
    public final static String KMEANS_OUPUT = "output/";
    public final static String KMEANS_DATA = "testdata/";
    public final static String FEATURE_INPUT_PATH = KMEANS_DATA + "points";
    public final static String CLUSTER_INPUT_PATH = KMEANS_DATA + "clusters";
    public final static String CLUSTER_FILE = KMEANS_OUPUT + "clusteredPoints/part-m-00000";

    // Parameters for K-Means
    public final static int K = 6;
    public final static double CONVERGENCE_DELTA = 0.001;
    public final static int MAX_ITERATIONS = 10;
    public final static boolean RUN_CLUSTERING = true;
    public final static double CLUSTER_CLASSIFICATION_THRESHOLD = 0;
    public final static boolean RUN_SEQUENTIAL = false;

    // Our output path
    public final static String RESULT_PATH = "result/";
    public final static String RESULT_CLUSTER_PATH = RESULT_PATH + "clusters/";
    public final static String FEATURE_FILE = RESULT_PATH + "features.txt";
    public final static String FEATURE_SEPERATOR= " ";
    public final static String HTML_CLUSTER_FILE = RESULT_PATH + "all_files.html";

    // Evaluating features
    public final static Boolean EVALUATE_FEATURES = false;
    public final static String EVALUATION_FEATURE_F_MEASURE_FILE = RESULT_PATH + "evaluation_features_f_measure.csv";
    public final static String EVALUATION_FEATURE_PRECISION_FILE = RESULT_PATH + "evaluation_features_precision.csv";
    public final static String EVALUATION_FEATURE_RECALL_FILE = RESULT_PATH + "evaluation_features_recall.csv";
    public final static String EVALUATION_FEATURE_CLUSTER_FILE = RESULT_PATH + "evaluation_features_cluster.csv";

    // Database config
    public final static String IP = "141.89.225.134";
    public final static String INSTANCE = "03";
    public final static String USER = "SMA1415";
    public final static String PASSWORD = "Popcorn54";

    // POS Tagger
    public final static String MODEL_DIR = "../resource/stanford-postagger-full-2014-08-27/models/";
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
    public final static String FUNCTION_WORD_FILE = "../resource/FunctionWords_de.txt";
    public final static String ABBREVIATIONS_FILE = "../resource/Abbreviations_de.txt";

    // Language detector
    public final static String PROFILES_DIR = "../resource/langdetect-03-03-2014/profiles/";

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
