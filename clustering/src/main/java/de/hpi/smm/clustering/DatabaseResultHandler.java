package de.hpi.smm.clustering;

import org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable;
import org.apache.mahout.math.Vector;


public class DatabaseResultHandler implements ResultHandler {

    @Override
    public void handleBlogPost(WeightedPropertyVectorWritable value, int clusterId, int documentId) {
        BlogPost blogPost = BlogPost.createFromVector(clusterId, documentId, value);
        // TODO
    }

    @Override
    public void handleCluster(Vector centerVector, int id, String name) {
        ClusterCentroid cluster = ClusterCentroid.createFromVector(id, name, centerVector);

        // TODO
    }
}
