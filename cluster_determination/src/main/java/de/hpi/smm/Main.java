package de.hpi.smm;


import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import de.hpi.smm.cluster_determination.Cluster;
import de.hpi.smm.cluster_determination.ClusterDetermination;
import de.hpi.smm.features.FeatureExtractor;
import de.hpi.smm.mustache.MustacheTemplateEngine;
import spark.ModelAndView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

public class Main {

    public static void main(String[] args) {
        Map map = new HashMap();

        get("/", (rq, rs) -> {
            map.put("result", false);
            return new ModelAndView(map, "main.mustache");
        }, new MustacheTemplateEngine());

        post("/", (rq, rs) -> {
            String blogPost = rq.queryMap().get("blog-post").value();
            String assignedCluster = "";
            try {
                assignedCluster = run(rq.queryMap().get("blog-post").value());
            } catch (LangDetectException | IOException e) {
                e.printStackTrace();
            }
            map.put("cluster", assignedCluster);
            map.put("blog-post", blogPost);
            map.put("result", true);

            return new ModelAndView(map, "main.mustache");
        }, new MustacheTemplateEngine());

    }

    private static String run(String content) throws LangDetectException, IOException {
        // determine language of content
        DetectorFactory.clear();
        DetectorFactory.loadProfile(Config.PROFILES_DIR);
        Detector detector = DetectorFactory.create();
        detector.append(content);
        String lang = detector.detect();

        // extract features for content
        FeatureExtractor featureExtractor = new FeatureExtractor();
        List<Float> features = featureExtractor.getFeatures(content, lang);

        // calculate distance to each cluster and select the nearest one
        ClusterDetermination clusterDetermination = new ClusterDetermination();
        Cluster cluster = clusterDetermination.determineCluster(features);

        return "The given text belongs to cluster " + cluster.getNumber() + ": " + cluster.getName() + ".";
    }

    private static String readFile(String filename) {
        String content = "";
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(filename));
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
