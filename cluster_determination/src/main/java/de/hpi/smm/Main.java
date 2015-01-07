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
            return new ModelAndView(map, "index.mustache");
        }, new MustacheTemplateEngine());

        post("/", (rq, rs) -> {
            try {
                String blogPost = rq.queryMap().get("blog-post").value();
                Cluster assignedCluster = run(blogPost);

                map.put("cluster", assignedCluster.getNumber());
                map.put("cluster-name", assignedCluster.getName());
                map.put("blog-post", blogPost);
                map.put("result", true);

            } catch (LangDetectException | IOException e) {
                e.printStackTrace();
                rs.redirect("/error");
            }

            return new ModelAndView(map, "index.mustache");
        }, new MustacheTemplateEngine());

        get("/error", (rq, rs) -> new ModelAndView(map, "error.mustache"), new MustacheTemplateEngine());
    }

    private static Cluster run(String content) throws LangDetectException, IOException {
        // determine language of content
        DetectorFactory.clear();
        DetectorFactory.loadProfile(Config.PROFILES_DIR);
        Detector detector = DetectorFactory.create();
        detector.append(content);
        String lang = detector.detect();

        // extract features for content
        FeatureExtractor featureExtractor = new FeatureExtractor();
        List<Float> features = featureExtractor.getFeatures(content, lang);

//        features.clear();
//        features.add(2.5f);
//        features.add(2.5f);
//        features.add(2.5f);

        // calculate distance to each cluster and select the nearest one
        ClusterDetermination clusterDetermination = new ClusterDetermination();
        return clusterDetermination.determineCluster(features);
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
