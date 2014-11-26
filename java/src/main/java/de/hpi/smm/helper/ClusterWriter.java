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
        PrintWriter allFiles = new PrintWriter(PATH + "allFiles.html", "UTF-8");
        allFiles.write(ClusterWriter.getStyle());
        allFiles.write("<table>\n");
        for (Map.Entry<Integer, List<Integer>> c2d : cluster2documents.entrySet()) {
            // create directory for cluster
            String clusterPath = PATH + c2d.getKey() + "/";
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
        return "<style type=\"text/css\">\n" +
                "td {\n" +
                "    white-space: nowrap;\n" +
                "    overflow: hidden;\n" +
                "}\n" +
                "</style>\n";
    }
}
