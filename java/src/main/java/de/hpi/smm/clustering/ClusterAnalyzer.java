package de.hpi.smm.clustering;

import de.hpi.smm.Config;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable;
import org.apache.mahout.clustering.iterator.ClusterWritable;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClusterAnalyzer {

    private Map<Integer, List<Integer>> cluster2document = new HashMap<Integer, List<Integer>>();
    private List<BlogPost> blogPost = new ArrayList<>();
    private List<ClusterCentroid> centers = new ArrayList<ClusterCentroid>();

    public void analyzeMahout() throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        SequenceFile.Reader reader = new SequenceFile.Reader(fs,
                new Path(Config.CLUSTER_FILE), conf);

        IntWritable key = new IntWritable();
        WeightedPropertyVectorWritable value = new WeightedPropertyVectorWritable();
        while (reader.next(key, value)) {
            Integer documentId = Integer.parseInt(((NamedVector) value.getVector()).getName());
            int clusterId = key.get();

            addBlogPost(value, clusterId, documentId);
            addDocumentToCluster(clusterId, documentId);
        }

        reader.close();

        SequenceFile.Reader reader1 = new SequenceFile.Reader(fs, new Path(getLastClusterFile()), conf);
        IntWritable key1 = new IntWritable();
        ClusterWritable value1 = new ClusterWritable();
        while (reader1.next(key1, value1)) {
            centers.add(ClusterCentroid.createFromVector(key1.get(), "cluster" + key1.toString(), value1.getValue().getCenter()));
        }
        reader1.close();
    }

    private String getLastClusterFile() {
        for (int i = Config.MAX_ITERATIONS; i > 0 ; i--) {
            String path = String.format(Config.CLUSTER_CENTER_FILE, i);
            if (new File(path).isFile()){
                return path;
            }
        }
        throw new RuntimeException("FATAL ERROR: Cluster center file not found!");
    }

    private void addBlogPost(WeightedPropertyVectorWritable value, int clusterId, int documentId) {
        int featureNr = value.getVector().size();
        Vector vector = value.getVector();
        Double[] point = new Double[featureNr];

        for (int i = 0; i < featureNr; i++) {
            point[i] = vector.get(i);
        }

        this.blogPost.add(new BlogPost(clusterId, documentId, point));
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

    public List<ClusterCentroid> getCenters() {
        return centers;
    }

}
