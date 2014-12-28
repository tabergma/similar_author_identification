package de.hpi.smm;


import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import de.hpi.smm.cluster_determination.Cluster;
import de.hpi.smm.cluster_determination.ClusterDetermination;
import de.hpi.smm.features.FeatureExtractor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage:\njava main.java ( -c \"<text>\" | -f <filename> )");
        }

        String content = "";

        switch (args[0]) {
            case "-c":
                content = args[1];
                break;
            case "-f":
                content = readFile(args[1]);
                if (content.isEmpty()) return;
                break;
            default:
                System.out.println("Usage:\njava main.java ( -c \"<text>\" | -f <filename> )");
                return;
        }

        // determine language of content
        DetectorFactory.loadProfile(Config.PROFILES_DIR);
        Detector detector = DetectorFactory.create();
        detector.append(content);
        String lang = detector.detect();

        // extract features for content
        FeatureExtractor featureExtractor = new FeatureExtractor();
        List<Float> features = featureExtractor.getFeatures(content, lang);

        // calculate distance to each cluster and select the nearest one
        ClusterDetermination clusterDetermination = new ClusterDetermination();
        Cluster cluster = clusterDetermination.determineCluster(features);

        System.out.println("The given text belongs to cluster " + cluster.getNumber() + ": " + cluster.getName() + ".");
    }

    private static String readFile(String filename) {
        String content = "";
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();

            while (line != null) {
                content += line;
                content += "\n";
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Could not read file " + filename);
        }

        return content;
    }

}
