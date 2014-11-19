package de.hpi.smm;


import de.hpi.smm.FeatureExtraction.FeatureExtractor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        String filePath = "test.txt";

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine();

        FeatureExtractor featureExtractor = new FeatureExtractor();
        System.out.println(featureExtractor.getFeatures(line));
    }
}
