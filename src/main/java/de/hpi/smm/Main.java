package de.hpi.smm;


import de.hpi.smm.cluster_determination.Cluster;
import de.hpi.smm.cluster_determination.ClusterDetermination;
import de.hpi.smm.evaluation.TenFoldCrossValidation;
import de.hpi.smm.helper.FileReader;
import de.hpi.smm.mustache.MustacheTemplateEngine;
import spark.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static spark.Spark.get;
import static spark.Spark.post;

public class Main {

    public static void main(String[] args) throws IOException {
        Map<String, Object> map = new HashMap<String, Object>();

        ClusterDetermination clusterDetermination = new ClusterDetermination(
                de.hpi.smm.helper.FileReader.readClusterFile(), de.hpi.smm.helper.FileReader.readBlogPostFile());

        get("/", (rq, rs) -> {
            map.put("result", false);
            map.put("k-nearest", true);
            map.put("euclidean", false);
            map.put("k-value", 6);
            map.put("svm", false);
            return new ModelAndView(map, "index.mustache");
        }, new MustacheTemplateEngine());

        post("/", (rq, rs) -> {
            try {
                String blogPost = rq.queryMap().get("blog-post").value();
                String method = rq.queryMap().get("method").value();
                String k = rq.queryMap().get("k-value").value();
                Cluster assignedCluster = clusterDetermination.run(blogPost, method, k);

                List<String> labels = assignedCluster.getLabels();
                labels = labels.stream().filter(x -> !x.contains("POS"))
                        .collect(Collectors.toList());

                switch (method) {
                    case "k-nearest":
                        map.put("k-nearest", true);
                        map.put("euclidean", false);
                        map.put("svm", false);
                        break;
                    case "euclidean":
                        map.put("k-nearest", false);
                        map.put("euclidean", true);
                        map.put("svm", false);
                        break;
                    case "svm":
                        map.put("k-nearest", false);
                        map.put("euclidean", false);
                        map.put("svm", true);
                        break;
                }

                map.put("k-value", k);
                map.put("cluster-number", assignedCluster.getNumber() + 1);
                map.put("cluster-name", assignedCluster.getName());
                map.put("cluster-labels", labels);
                map.put("blog-post", blogPost);
                map.put("result", true);

            } catch (Exception e) {
                e.printStackTrace();
                rs.redirect("/error");
            }

            return new ModelAndView(map, "index.mustache");
        }, new MustacheTemplateEngine());

        get("/clusters", (rq, rs) -> {
            try {
                List<Cluster> clusters = FileReader.readClusterFile();
                for (Cluster cluster : clusters) {
                    cluster.setLabels(
                            cluster.getLabels().stream()
                                    .filter(x -> !x.contains("POS"))
                                    .collect(Collectors.toList()));
                    cluster.setNumber(cluster.getNumber() + 1);
                }
                map.put("all-clusters", clusters);
            } catch (IOException e) {
                map.put("all-clusters", new ArrayList<>());
                e.printStackTrace();
            }
            return new ModelAndView(map, "cluster.mustache");
        }, new MustacheTemplateEngine());

        get("/evaluation", (rq, rs) -> {
            try {
                TenFoldCrossValidation tenFoldCrossValidation = new TenFoldCrossValidation();
                Double accuracyKNearest = tenFoldCrossValidation.runForKNearestNeighbor();
                Double accuracySvm = tenFoldCrossValidation.runForSVM();

                map.put("accuracyKNearest", accuracyKNearest);
                map.put("accuracySvm", accuracySvm);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new ModelAndView(map, "evaluate.mustache");
        }, new MustacheTemplateEngine());

        get("/error", (rq, rs) -> new ModelAndView(map, "error.mustache"), new MustacheTemplateEngine());
    }

}
