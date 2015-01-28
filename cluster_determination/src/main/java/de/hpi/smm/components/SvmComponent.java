package de.hpi.smm.components;

import de.hpi.smm.io.SvmFeatureWriter;
import de.hpi.smm.libsvm.svm_train;

import java.io.IOException;

public class SvmComponent {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Wrong number of arguments!");
            System.out.println("To start the program execute");
            System.out.println("  java -jar <jar-name> <featureFile> <modelFile>");
        }

        String featureFile = args[0];
        String modelFile = args[1];

        run(featureFile, modelFile);
    }

    /**
     * Gets the features from the database,
     * writes them in a special format into a file and
     * trains the SVM model given this file.
     * The resulting model is written to disk.
     */
    public static void run(String featureFile, String modelFile) throws IOException {
        SvmFeatureWriter writer = new SvmFeatureWriter(featureFile);

        // read features and write them to a file


        // create model and write it to disk
        String[] createModel = {"-q", "-t", "2", "-s", "0", "-c", "100", "-b", "1", featureFile, modelFile};
        svm_train.main(createModel);
    }

}
