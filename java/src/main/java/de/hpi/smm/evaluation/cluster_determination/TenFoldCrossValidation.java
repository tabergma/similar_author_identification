package de.hpi.smm.evaluation.cluster_determination;


import de.hpi.smm.clustering.BlogPost;

import java.util.ArrayList;
import java.util.List;

public class TenFoldCrossValidation {

    private List<BlogPost> blogPosts;
    private List<BlogPost> testData;
    private List<BlogPost> trainingData;
    private KNearestNeighbor kNearestNeighbor = new KNearestNeighbor();

    public TenFoldCrossValidation(List<BlogPost> blogPosts) {
        this.blogPosts = blogPosts;
    }

    public Double validateKNearestNeighbor() {
        Double sum = 0.0;

        for (int i = 0; i < 10; i++) {
            getData(i);
            sum += getAccuracyForKNearestNeighbor(trainingData, testData, 6);
        }

        return sum/ 10;
    }

    private Double getAccuracyForKNearestNeighbor(List<BlogPost> documents, List<BlogPost> points, int k) {
        Double correct = 0.0;
        Double incorrect = 0.0;

        for (BlogPost point: points) {
            int cluster = kNearestNeighbor.getNearestCluster(documents, point.getPoint(), k);
            if (cluster == point.getClusterNumber()) correct++;
            else incorrect++;
        }

        return correct / (correct + incorrect);
    }

    private void getData(int k) {
        this.testData = new ArrayList<>();
        this.trainingData = new ArrayList<>();

        for (BlogPost blogPost: blogPosts) {
            if (blogPost.getDocumentId() % 10 == k) {
                this.testData.add(blogPost);
            } else {
                this.trainingData.add(blogPost);
            }
        }
    }
}
