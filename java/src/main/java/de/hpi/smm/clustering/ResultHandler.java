package de.hpi.smm.clustering;

import org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable;
import org.apache.mahout.math.Vector;

public interface ResultHandler {

    public void handleBlogPost(WeightedPropertyVectorWritable value, int clusterId, int documentId);

    public void handleCluster(Vector centerVector, int id, String name);

}
