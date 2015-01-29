package de.hpi.smm.clustering;

import org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable;
import org.apache.mahout.math.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CacheResultHandler implements ResultHandler {

    public Map<Integer, List<Integer>> cluster2document = new HashMap<Integer, List<Integer>>();
    private List<BlogPost> blogPost = new ArrayList<>();
    private List<ClusterCentroid> clusters = new ArrayList<ClusterCentroid>();

    @Override
    public void handleBlogPost(WeightedPropertyVectorWritable value, int clusterId, int documentId) {
        this.blogPost.add(BlogPost.createFromVector(clusterId, documentId, value));
        addDocumentToCluster(clusterId, documentId);
    }

    @Override
    public void handleCluster(Vector centerVector, int id, String name) {
        ClusterCentroid cluster = ClusterCentroid.createFromVector(id, name, centerVector);

        int nrOfDocuments = 0;
        if (cluster2document.containsKey(id)) {
            nrOfDocuments = cluster2document.get(id).size();
        }
        cluster.setNrOfDocuments(nrOfDocuments);

        clusters.add(cluster);
    }

    private void addDocumentToCluster(int clusterId, int documentId) {
        if (!cluster2document.containsKey(clusterId)) {
            ArrayList<Integer> documents = new ArrayList<Integer>();
            documents.add(documentId);
            cluster2document.put(clusterId, documents);
        } else {
            List<Integer> documents = cluster2document.get(clusterId);
            documents.add(documentId);
            cluster2document.put(clusterId, documents);
        }
    }

    public Map<Integer, List<Integer>> getCluster2document() {
        return cluster2document;
    }

    public List<BlogPost> getBlogPosts() {
        return blogPost;
    }

    public List<ClusterCentroid> getClusters() {
        return clusters;
    }
}
