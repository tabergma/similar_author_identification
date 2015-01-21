package de.hpi.smm.cluster_determination;


import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import de.hpi.smm.Config;
import de.hpi.smm.features.FeatureExtractor;

import java.io.IOException;
import java.util.List;


public class ClusterDetermination {

    List<Cluster> clusters = de.hpi.smm.helper.FileReader.readClusterFile();
    List<DataEntry> dataEntries = de.hpi.smm.helper.FileReader.readBlogPostFile();

    public ClusterDetermination() throws IOException {}

    public Cluster run(String content, String method, String k) throws Exception {
        // determine language of text
        DetectorFactory.clear();
        DetectorFactory.loadProfile(Config.PROFILES_DIR);
        Detector detector = DetectorFactory.create();
        detector.append(content);
        String lang = detector.detect();

        // extract features for content
        FeatureExtractor featureExtractor = new FeatureExtractor();
        List<Float> features = featureExtractor.getFeatures(content, lang);

        Float[] blogPostPoint = features.toArray(new Float[features.size()]);
        int index = -1;

        switch (method) {
            case "k-nearest":
                index = KNearestNeighbour.getNearestCluster(dataEntries, blogPostPoint, Integer.valueOf(k));
                break;
            case "euclidean":
                index = EuclideanDistance.getNearestCluster(clusters, blogPostPoint);
                break;
            case "svm":
                return Svm.getNearestCluster(features);
        }

        return clusters.get(index);
    }

}
