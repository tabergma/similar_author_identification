package de.hpi.smm;

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
    public final static int K = 10;
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

    // Database config
    public final static String IP = "141.89.225.134";
    public final static String INSTANCE = "03";
    public final static String USER = "SMA1415";
    public final static String PASSWORD = "Popcorn54";

    // POS Tagger
    public final static String MODEL_DIR = "../resource/stanford-postagger-full-2014-08-27/models/";

    // Language detector
    public final static String PROFILES_DIR = "../resource/langdetect-03-03-2014/profiles/";

    public static final Map<String, String> lang2model;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("en", "wsj-0-18-bidirectional-distsim.tagger");
        aMap.put("de", "german-fast.tagger");
        aMap.put("fr", "french.tagger");
        aMap.put("zh-cn", "chinese-distsim.tagger");
        aMap.put("es", "spanish-distsim.tagger");

        lang2model = Collections.unmodifiableMap(aMap);
    }
}
