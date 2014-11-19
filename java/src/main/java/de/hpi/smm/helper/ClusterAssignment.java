package de.hpi.smm.helper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.mahout.clustering.classify.WeightedVectorWritable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClusterAssignment {

    public String CLUSTER_DIR = "";

    public Map<String, String> getClusterAssignment() throws IOException {
        Map<String, String> clusterMap = new HashMap<String, String>();

        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        File clusterFolder = new File(CLUSTER_DIR);
        File[] clusterFiles = clusterFolder.listFiles();

        for(File f: clusterFiles) {
            if(!f.getName().startsWith("part-m"))
                continue;

            SequenceFile.Reader reader = new SequenceFile.Reader(fs, new Path(f.getAbsolutePath()), conf);
            IntWritable key = new IntWritable();
            WeightedVectorWritable value = new WeightedVectorWritable();

            while (reader.next(key, value)) {
                clusterMap.put(key.toString(), value.toString());
            }
            reader.close();
        }

        return clusterMap;

    }

}
