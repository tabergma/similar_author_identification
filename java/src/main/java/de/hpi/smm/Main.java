package de.hpi.smm;


import de.hpi.smm.feature_extraction.FeatureExtractor;
import de.hpi.smm.helper.FileWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        String filePath = "test.txt";

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine();

        FeatureExtractor featureExtractor = new FeatureExtractor();

        ArrayList<List<Float>> list = new ArrayList<List<Float>>();
        list.add(featureExtractor.getFeatures(line));

        FileWriter.writeFeaturesToFile(list, "word");
    }
}
