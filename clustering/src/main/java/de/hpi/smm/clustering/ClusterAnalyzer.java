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


public class ClusterAnalyzer {

    private String clusterFile;
    private String clusterCenterFile;

    public ClusterAnalyzer(String clusterFile, String clusterCenterFile) {
        this.clusterFile = clusterFile;
        this.clusterCenterFile = clusterCenterFile;
    }

    public void analyzeMahout(ResultHandler resultHandler) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        // Handle blog post
        System.out.print("Read cluster - blog post file ... ");
        SequenceFile.Reader blogPostReader = new SequenceFile.Reader(fs, new Path(this.clusterFile), conf);
        IntWritable key = new IntWritable();
        WeightedPropertyVectorWritable value = new WeightedPropertyVectorWritable();
        while (blogPostReader.next(key, value)) {
            Integer documentId = Integer.parseInt(((NamedVector) value.getVector()).getName());
            int clusterId = key.get();
            resultHandler.handleBlogPost(value, clusterId, documentId);
        }
        blogPostReader.close();
        System.out.println("Done.");

        // Handle cluster centroids
        System.out.print("Read cluster centroids file ... ");
        SequenceFile.Reader clusterReader = new SequenceFile.Reader(fs, new Path(getLastClusterFile()), conf);
        IntWritable key1 = new IntWritable();
        ClusterWritable value1 = new ClusterWritable();
        while (clusterReader.next(key1, value1)) {
            int clusterId = key1.get();
            String name = "cluster" + key1.toString();
            Vector v = value1.getValue().getCenter();
            resultHandler.handleCluster(v, clusterId, name);
        }
        clusterReader.close();
        System.out.println("Done.");
    }

    private String getLastClusterFile() {
        for (int i = Config.MAX_ITERATIONS; i > 0 ; i--) {
            String path = String.format(this.clusterCenterFile, i);
            if (new File(path).isFile()){
                return path;
            }
        }
        throw new RuntimeException("FATAL ERROR: Cluster center file not found!");
    }

}
