package de.hpi.smm.components;

import de.hpi.smm.Config;
import de.hpi.smm.cluster_determination.BlogPost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SvmComponent {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Wrong number of arguments!");
            System.out.println("To start the program execute");
            System.out.println("  java -jar <jar-name> <modelFile>");
            return;
        }

        String modelFile = args[0];

        run(modelFile);
    }

    /**
     * Gets the features from the database,
     * writes them in a special format into a file and
     * trains the SVM model given this file.
     * The resulting model is written to disk.
     *
     * @param modelFile the result model file
     */
    public static void run(String modelFile) throws IOException {
        // read features and cluster id of all blog posts
        List<BlogPost> blogPosts = new ArrayList<>();

        // create model and write it to disk
        String[] createModel = {"-q", "-t", "2", "-s", "0", "-c", "100", "-b", "1", Config.SVM_FEATURE_FILE, Config.SVM_MODEL_FILE};
//        svm_train.main(createModel, blogPosts);
    }

}
