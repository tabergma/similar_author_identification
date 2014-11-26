package de.hpi.smm.clustering;

import de.hpi.smm.Config;
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

    private static final String INPUT_PATH = Config.INPUT_PATH;
    private static final String OUTPUT_PATH = Config.OUTPUT_PATH;
    private static final String FEATURE_INPUT_PATH = Config.FEATURE_INPUT_PATH;
    private static final String CLUSTER_INPUT_PATH = Config.CLUSTER_INPUT_PATH;

    public void run(List<List<Float>> documentFeatures) throws Exception {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        // set configuration
        //conf.addResource("$HADOOP_HOME/etc/hadoop/core-site.xml");
        //conf.addResource("$HADOOP_HOME/etc/hadoop/hdfs-site.xml");

        // check if all directories exists
        createDirectories();


        // convert list of document features to list of vectors
        List<Vector> vectors = getPoints(documentFeatures);
        writePointsToFile(vectors, new Path(FEATURE_INPUT_PATH + "/file1"), fs, conf);

        // write initial clusters
        writeInitialClusters(vectors, new Path(CLUSTER_INPUT_PATH + "/part-00000"), fs, conf);

        // create output path
        Path output = new Path(Config.OUTPUT_PATH);
        HadoopUtil.delete(conf, output);

        // run k means
        KMeansDriver.run(
                conf,
                new Path(Config.FEATURE_INPUT_PATH),
                new Path(Config.CLUSTER_INPUT_PATH),
                output,
                Config.CONVERGENCE_DELTA,
                Config.MAX_ITERATIONS,
                Config.RUN_CLUSTERING,
                Config.CLUSTER_CLASSIFICATION_THRESHOLD,
                Config.RUN_SEQUENTIAL
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

    public void cleanUp() throws IOException {
        FileUtils.deleteDirectory(new File(INPUT_PATH));
        FileUtils.deleteDirectory(new File(OUTPUT_PATH));
    }


    public void writePointsToFile(List<Vector> points, Path path, FileSystem fs, Configuration conf) throws IOException {
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

    public void writeInitialClusters(List<Vector> vectors, Path path, FileSystem fs, Configuration conf) throws IOException {
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, path, Text.class, Kluster.class);
        for (int i = 0; i < Config.K; i++) {
            Vector vec = vectors.get(i);
            Kluster cluster = new Kluster(vec, i, new EuclideanDistanceMeasure());
            writer.append(new Text(cluster.getIdentifier()), cluster);
        }
        writer.close();
    }

}
