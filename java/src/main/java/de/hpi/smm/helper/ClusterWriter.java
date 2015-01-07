package de.hpi.smm.helper;


import de.hpi.smm.Config;
import de.hpi.smm.clustering.ClusterCentroid;
import de.hpi.smm.drawing.Cluster;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class ClusterWriter {

    public static void writeClusterFiles(Map<Integer, List<Integer>> cluster2documents, List<String> documentTexts) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter htmlFile = new PrintWriter(Config.HTML_CLUSTER_FILE, "UTF-8");
        htmlFile.write(ClusterWriter.getStyle());

        for (Map.Entry<Integer, List<Integer>> c2d : cluster2documents.entrySet()) {
            // create directory for cluster
            String clusterPath = Config.RESULT_CLUSTER_PATH + c2d.getKey() + "/";
            File dir = new File(clusterPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // write all document texts to cluster dir
            for (Integer docId : c2d.getValue()) {
                PrintWriter writer = new PrintWriter(clusterPath + docId + ".txt", "UTF-8");
                writer.write(documentTexts.get(docId));
                writer.close();
                htmlFile.write(String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>%n", c2d.getKey(), docId, documentTexts.get(docId)));
            }
        }
        htmlFile.write("</table>\n");
    }

    public static String getStyle() {
        return " <meta charset=\"utf-8\"/><style type=\"text/css\">\n" +
                "td {\n" +
                "    white-space: nowrap;\n" +
                "    overflow: hidden;\n" +
                "}\n" +
                "</style>\n" +
                "<table>\n";
    }

    public static void writeClusterCenterFiles(List<ClusterCentroid> centers) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter centerWriter = new PrintWriter(Config.CLUSTER_CENTER_OUTPUT, "UTF-8");
        for (ClusterCentroid cc : centers){
            centerWriter.print(cc.getId());
            centerWriter.print(",");
            centerWriter.print(cc.getName());
            for (Double value : cc.getValues()) {
                centerWriter.print(",");
                centerWriter.print(value);
            }
            centerWriter.println();
        }
    }
}
