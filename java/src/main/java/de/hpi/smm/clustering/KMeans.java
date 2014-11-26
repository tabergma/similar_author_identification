package de.hpi.smm.clustering;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.Kluster;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KMeans {

    private final static String OUTPUT_PATH = "output/";
    private final static String INPUT_PATH = "testdata/";
    private final static String FEATURE_INPUT_PATH = INPUT_PATH + "points";
    private final static String CLUSTER_INPUT_PATH = INPUT_PATH + "clusters";

    private final static int k = 6;
    private final static Configuration conf = new Configuration();
    private final static Path input = new Path(FEATURE_INPUT_PATH);
    private final static Path clusterIn = new Path(CLUSTER_INPUT_PATH);
    private final static Path output = new Path(OUTPUT_PATH);
    private final static double convergenceDelta = 0.001;
    private final static int maxIterations = 10;
    private final static boolean runClustering = true;
    private final static double clusterClassificationThreshold = 0;
    private final static boolean runSequential = false;


    public void run(List<List<Float>> documentFeatures) throws Exception {
        // set configuration
        //conf.addResource("$HADOOP_HOME/etc/hadoop/core-site.xml");
        //conf.addResource("$HADOOP_HOME/etc/hadoop/hdfs-site.xml");

        // check if all directories exists
        createDirectories();

        FileSystem fs = FileSystem.get(conf);

        // convert list of document features to list of vectors
        List<Vector> vectors = getPoints(documentFeatures);
        writePointsToFile(vectors, new Path(FEATURE_INPUT_PATH + "/file1"), fs);

        // write initial clusters
        writeInitialClusters(vectors, new Path(CLUSTER_INPUT_PATH + "/part-00000"), fs);

        // create output path
        HadoopUtil.delete(conf, output);

        // run k means
        KMeansDriver.run(
                conf,
                input,
                clusterIn,
                output,
                convergenceDelta,
                maxIterations,
                runClustering,
                clusterClassificationThreshold,
                runSequential
        );
    }

    private List<Vector> getPoints(List<List<Float>> documents) {
        List<Vector> points = new ArrayList<Vector>();

        for (List<Float> doc: documents) {
            Vector vec = new RandomAccessSparseVector(doc.size());

            double[] arr = new double[doc.size()];
            for (int i = 0; i < doc.size(); i++)
                arr[i] = doc.get(i);

            vec.assign(arr);
            points.add(vec);
        }

        return points;
    }

    private void createDirectories() throws IOException {
        File testData = new File(INPUT_PATH);
        if (!testData.exists()) {
            testData.mkdir();
        }

        testData = new File(FEATURE_INPUT_PATH);
        if (!testData.exists()) {
            testData.mkdir();
        }
    }

    private void cleanUp() throws IOException {
        FileUtils.deleteDirectory(new File(INPUT_PATH));
        FileUtils.deleteDirectory(new File(OUTPUT_PATH));
    }


    public void writePointsToFile(List<Vector> points, Path path, FileSystem fs) throws IOException {
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, path,
                IntWritable.class, VectorWritable.class);
        int recNum = 0;
        VectorWritable vec = new VectorWritable();
        for (Vector point : points) {
            vec.set(point);
            writer.append(new IntWritable(recNum++), vec);
        }
        writer.close();
    }

    public void writeInitialClusters(List<Vector> vectors, Path path, FileSystem fs) throws IOException {
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, path, Text.class, Kluster.class);
        for (int i = 0; i < k; i++) {
            Vector vec = vectors.get(i);
            Kluster cluster = new Kluster(vec, i, new EuclideanDistanceMeasure());
            writer.append(new Text(cluster.getIdentifier()), cluster);
        }
        writer.close();
    }

}
