package de.hpi.smm;


import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import de.hpi.smm.cluster_determination.Cluster;
import de.hpi.smm.cluster_determination.ClusterDetermination;
import de.hpi.smm.features.FeatureExtractor;
import de.hpi.smm.libsvm.svm_predict;
import de.hpi.smm.mustache.MustacheTemplateEngine;
import org.apache.commons.io.FileUtils;
import spark.ModelAndView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
                map.put("cluster-labels", assignedCluster.getLabels());
                map.put("blog-post", blogPost);
                map.put("result", true);

            } catch (Exception e) {
                e.printStackTrace();
                rs.redirect("/error");
            }

            return new ModelAndView(map, "index.mustache");
        }, new MustacheTemplateEngine());

        get("/error", (rq, rs) -> new ModelAndView(map, "error.mustache"), new MustacheTemplateEngine());
    }

    private static Cluster run(String content) throws Exception {
        // determine language of content
        DetectorFactory.clear();
        DetectorFactory.loadProfile(Config.PROFILES_DIR);
        Detector detector = DetectorFactory.create();
        detector.append(content);
        String lang = detector.detect();

        // extract features for content
        FeatureExtractor featureExtractor = new FeatureExtractor();
        List<Float> features = featureExtractor.getFeatures(content, lang);

        if (Config.USE_SVM_TO_CLUSTER){
            return svmPredict(features);
        } else {
            // calculate distance to each cluster and select the nearest one
            ClusterDetermination clusterDetermination = new ClusterDetermination();
            return clusterDetermination.determineCluster(features);
        }
    }

    private static Cluster svmPredict(List<Float> features) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(-1);
        int i = 1;
        for (Float f : features){
            if (f != 0.0){
                stringBuilder.append(Config.SVM_SEPARATOR);
                stringBuilder.append(i);
                stringBuilder.append(Config.KEY_VALUE_SEPARATOR);
                stringBuilder.append(f);
            }
            i++;
        }
        stringBuilder.append("\n");
        FileUtils.writeStringToFile(new File(Config.TEMP_FILE), stringBuilder.toString());

        String[] authorNames = FileUtils.readFileToString(new File(Config.AUTHOR_FILE)).split(Config.FEATURE_SEPARATOR);

        String[] predict = {"-q", "-b", "1", Config.TEMP_FILE, Config.SVM_MODEL_FILE, Config.SVM_OUTPUT_FILE};
        svm_predict.main(predict);
        String result = FileUtils.readFileToString(new File(Config.SVM_OUTPUT_FILE));
        String authorIdString = result.split("\n")[1].split(" ")[0];
        Integer authorId = Math.round(Float.parseFloat(authorIdString));
        Cluster c = new Cluster();
        c.setName(authorId.toString());
        c.setNumber(authorId);
        List<String> authorName = new ArrayList<String>();
        authorName.add(authorNames[authorId]);
        c.setLabels(authorName);
        return c;
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
