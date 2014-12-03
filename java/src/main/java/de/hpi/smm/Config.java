package de.hpi.smm;

public class Config {

    // Directories for K-Means
    public final static String OUTPUT_PATH = "output/";
    public final static String INPUT_PATH = "testdata/";
    public final static String FEATURE_INPUT_PATH = INPUT_PATH + "points";
    public final static String CLUSTER_INPUT_PATH = INPUT_PATH + "clusters";
    public final static String CLUSTER_FILE = OUTPUT_PATH + "clusteredPoints/part-m-00000";

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
}
