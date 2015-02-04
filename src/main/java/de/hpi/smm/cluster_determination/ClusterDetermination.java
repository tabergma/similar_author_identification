package de.hpi.smm.cluster_determination;


import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import de.hpi.smm.Config;
import de.hpi.smm.features.FeatureExtractor;
import de.hpi.smm.libsvm.svm_train;

import java.io.IOException;
import java.util.List;


public class ClusterDetermination {

    private List<Cluster> clusters;
    private List<BlogPost> blogPosts;
    private String svmModelFile;


    public ClusterDetermination(List<Cluster> clusters, List<BlogPost> blogPosts, String svmModelFile) throws IOException {
        this.clusters = clusters;
        this.blogPosts = blogPosts;
        this.svmModelFile = svmModelFile;
    }

    public Cluster run(String content, String method, String k, String expectedLanguage) throws Exception {
        // determine language of text
        DetectorFactory.clear();
        DetectorFactory.loadProfile(Config.PROFILES_DIR);
        Detector detector = DetectorFactory.create();
        detector.append(content);
        String lang = detector.detect();

        if (!lang.equals(expectedLanguage))
            throw new RuntimeException("The language of the blog post does not match the expected language " + expectedLanguage);

        // extract features for content
        FeatureExtractor featureExtractor = new FeatureExtractor(lang);
        List<Float> featureList = featureExtractor.getFeatures(content);
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
                index = Svm.getNearestCluster(featureList, this.svmModelFile);
        }

        return clusters.get(index);
    }

    public void createSvmModel() throws IOException {
        String[] args = {"-q", "-t", "2", "-s", "0", "-c", "100", "-b", "1", this.svmModelFile};
        svm_train svmTrain = new svm_train();
        svmTrain.train(args, blogPosts);
    }

}
