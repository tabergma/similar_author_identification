package de.hpi.smm.cluster_determination;


import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import de.hpi.smm.Config;
import de.hpi.smm.features.FeatureExtractor;
import de.hpi.smm.libsvm.svm_train;

import java.io.IOException;
import java.util.List;


public class ClusterDetermination {

    List<Cluster> clusters = de.hpi.smm.helper.FileReader.readClusterFile();
    List<BlogPost> blogPosts = de.hpi.smm.helper.FileReader.readBlogPostFile();

    public ClusterDetermination() throws IOException {
        createSvmModel();
    }

    public Cluster run(String content, String method, String k) throws Exception {
        // determine language of text
        DetectorFactory.clear();
        DetectorFactory.loadProfile(Config.PROFILES_DIR);
        Detector detector = DetectorFactory.create();
        detector.append(content);
        String lang = detector.detect();

        // extract features for content
        FeatureExtractor featureExtractor = new FeatureExtractor();
        List<Float> featureList = featureExtractor.getFeatures(content, lang);
        Float[] featureArray = featureList.toArray(new Float[featureList.size()]);

        int index = -1;
        switch (method) {
            case "k-nearest":
                index = KNearestNeighbour.getNearestCluster(blogPosts, featureArray, Integer.valueOf(k));
                break;
            case "euclidean":
                index = EuclideanDistance.getNearestCluster(clusters, featureArray);
                break;
            case "svm":
                index = Svm.getNearestCluster(featureList);
        }

        return clusters.get(index);
    }

    private void createSvmModel() throws IOException {
        String[] createModel = {"-q", "-t", "2", "-s", "0", "-c", "100", "-b", "1", Config.SVM_FEATURE_FILE, Config.SVM_MODEL_FILE};
        svm_train.main(createModel);
    }

}
