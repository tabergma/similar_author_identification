package de.hpi.smm.evaluation;

import de.hpi.smm.Config;
import de.hpi.smm.cluster_determination.BlogPost;
import de.hpi.smm.cluster_determination.KNearestNeighbour;
import de.hpi.smm.libsvm.svm_train;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TenFoldCrossValidation {

    private List<BlogPost> blogPosts;
    private List<BlogPost> testData;
    private List<BlogPost> trainingData;

    public TenFoldCrossValidation() throws IOException {
        this.blogPosts = de.hpi.smm.helper.FileReader.readBlogPostFile();
    }

    public Double runForKNearestNeighbor() {
        Double sum = 0.0;

        for (int i = 0; i < 10; i++) {
            getData(i);
            sum += getAccuracyForKNearestNeighbor(trainingData, testData, 6);
        }

        sum = sum / 10;
        return Math.round(sum * 10000.0) / 100.0;
    }

    public Double runForSVM() throws IOException {
        String[] args = {"-q", "-t", "2", "-s", "0", "-c", "100", "-v", "10", Config.SVM_FEATURE_FILE};
        svm_train svm = new svm_train();
        double result = svm.crossValidation(args, blogPosts);
        return Math.round(result * 100.0) / 100.0;
    }

    private Double getAccuracyForKNearestNeighbor(List<BlogPost> documents, List<BlogPost> points, int k) {
        Double correct = 0.0;
        Double incorrect = 0.0;

        for (BlogPost point: points) {
            int cluster = KNearestNeighbour.getNearestCluster(documents, point.getPoint(), k);
            if (cluster == point.getNumber()) correct++;
            else incorrect++;
        }

        return correct / (correct + incorrect);
    }

    private void getData(int k) {
        this.testData = new ArrayList<>();
        this.trainingData = new ArrayList<>();

        for (BlogPost blogPost: blogPosts) {
            if (Integer.parseInt(blogPost.getDocumentId()) % 10 == k) {
                this.testData.add(blogPost);
            } else {
                this.trainingData.add(blogPost);
            }
        }
    }
}
