package de.hpi.smm.helper;

import de.hpi.smm.Config;
import de.hpi.smm.cluster_determination.BlogPost;
import de.hpi.smm.cluster_determination.Cluster;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FileReader {

    public static List<Cluster> readClusterFile() throws IOException {
        BufferedReader br = new BufferedReader(new java.io.FileReader(Config.CLUSTER_CENTER_OUTPUT));
        List<Cluster> clusters = new ArrayList<>();

        String line = "";
        while((line = br.readLine()) != null){
            String[] l = line.split(",");

            Cluster cluster = new Cluster();
            cluster.setNumber(Integer.parseInt(l[0]));
            cluster.setName(l[1]);

            String[] labelsStr = l[2].split(";");
            List<String> labels = new ArrayList<>();
            Collections.addAll(labels, labelsStr);
            cluster.setLabels(labels);

            Float[] points = new Float[l.length - 3];
            for (int i = 3; i < l.length; i++) {
                points[i - 3] = Float.valueOf(l[i]);
            }
            cluster.setPoint(points);

            clusters.add(cluster);
        }
        br.close();

        return clusters;
    }

    public static List<BlogPost> readBlogPostFile() throws IOException {
        BufferedReader br = new BufferedReader(new java.io.FileReader(Config.BLOG_POST_OUTPUT));
        List<BlogPost> dataEntries = new ArrayList<>();

        String line = "";
        while((line = br.readLine()) != null){
            String[] l = line.split(",");

            BlogPost blogPost = new BlogPost();
            blogPost.setNumber(Integer.parseInt(l[0]));
            blogPost.setDocumentId(Integer.parseInt(l[1]));

            Float[] points = new Float[l.length - 2];
            for (int i = 2; i < l.length; i++) {
                points[i - 2] = Float.valueOf(l[i]);
            }
            blogPost.setPoint(points);

            dataEntries.add(blogPost);
        }
        br.close();

        return dataEntries;
    }

    public static String readFile(String filename) {
        String content = "";
        BufferedReader br = null;

        try {
            br = new BufferedReader(new java.io.FileReader(filename));
            String line = br.readLine();

            while (line != null) {
                content += line;
                content += "\n";
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Could not read file " + filename);
        }

        return content;
    }
}
