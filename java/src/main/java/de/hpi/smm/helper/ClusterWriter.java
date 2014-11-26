package de.hpi.smm.helper;


import de.hpi.smm.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class ClusterWriter {

    public static void writeClusterFiles(Map<Integer, List<Integer>> cluster2documents, List<String> documentTexts) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter allFiles = new PrintWriter(Config.CLUSTER_FILE + "allFiles.html", "UTF-8");
        allFiles.write(ClusterWriter.getStyle());
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
                allFiles.write(String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>%n", c2d.getKey(), docId, documentTexts.get(docId)));
            }
        }
        allFiles.write("</table>\n");
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
}