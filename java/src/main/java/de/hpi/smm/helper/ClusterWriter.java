package de.hpi.smm.helper;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class ClusterWriter {

    public static String PATH = "../output/clusters/";

    public static void writeClusterFiles(Map<Integer, List<Integer>> cluster2documents, List<String> documentTexts) throws FileNotFoundException, UnsupportedEncodingException {
        for (Map.Entry<Integer, List<Integer>> c2d : cluster2documents.entrySet()) {
            // create directory for cluster
            String clusterPath = PATH + c2d.getKey() + "/";
            File dir = new File(clusterPath);
            if (!dir.exists()) {
                dir.mkdir();
            }

            // write all document texts to cluster dir
            for (Integer docId : c2d.getValue()) {
                PrintWriter writer = new PrintWriter(clusterPath + docId + ".txt", "UTF-8");
                writer.write(documentTexts.get(docId));
                writer.close();
            }
        }
    }

}
